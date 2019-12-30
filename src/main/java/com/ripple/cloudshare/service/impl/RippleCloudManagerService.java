package com.ripple.cloudshare.service.impl;

import com.ripple.cloudshare.data.entity.Server;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.VirtualMachine;
import com.ripple.cloudshare.data.loader.DefaultServerPopulate;
import com.ripple.cloudshare.data.repository.ServerRepository;
import com.ripple.cloudshare.data.repository.VirtualMachineRepository;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.service.CloudManager;
import com.ripple.cloudshare.service.ServerCapacity;
import com.ripple.cloudshare.service.VirtualMachineDetail;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Service
@DependsOn(value = {
        DefaultServerPopulate.DEFAULT_SERVER_POPULATE_BEAN_NAME
})
public class RippleCloudManagerService implements CloudManager {

    private static final String CLASS_NAME = "RippleCloudManagerService";

    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    private final ServerRepository serverRepository;

    private final VirtualMachineRepository virtualMachineRepository;

    private final Map<Long, ServerCapacity> serverMaxCapacity = new HashMap<>();
    private final Map<Long, ServerCapacity> serverSpareCapacity = new ConcurrentHashMap<>();

    private final List<Map.Entry<Long, ServerCapacity>> serverSpareCapacityList = new ArrayList<>();

    //TODO: use below in batches: compaction, general db_cleanup of deleted VM entries
    // AND in deletion of VM while server deregistration is in progress
    // AND in provisioning new VMs
    static final Set<Long> serverDeregistrationInProgress = new HashSet<>();

    static final Set<Long> recentlyFailedDeregistrationServers = new HashSet<>(); //prevent repeated server delete attempts
    //static final ServerCapacity smallestFailedCapacityRelocationAttempt = new ServerCapacity(); //for fail fast in server delete

    public RippleCloudManagerService(ServerRepository serverRepository, VirtualMachineRepository virtualMachineRepository) {
        this.serverRepository = serverRepository;
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @PostConstruct
    void initialize(){
        logger.info("Executing initialize of " + CLASS_NAME);

        //Initializing server initial capacity map
        List<Server> serverList = serverRepository.findAll();
        logger.info("serverList " + serverList);

        serverList.forEach(server -> {
            logger.info("server details " + server);
            logger.info("server Id " + server.getId() + " adding capacity: " + ServerCapacity.fromServer(server));
            serverMaxCapacity.put(server.getId(), ServerCapacity.fromServer(server));
        });

        logger.info("serverMaxCapacity: " + serverMaxCapacity);

        //Initializing server spare capacity map
        serverMaxCapacity.forEach((id, capacity) -> serverSpareCapacity.put(id, capacity.clone()));

        logger.info("serverSpareCapacity: " + serverSpareCapacity);

        List<VirtualMachine> virtualMachineList = virtualMachineRepository.findAll();
        logger.info("virtualMachineList " + virtualMachineList);

        virtualMachineList.stream()
                .filter(vm -> !vm.getRemoved())
                .forEach(virtualMachine -> {
                    serverSpareCapacity
                        .get(virtualMachine.getServer().getId())
                        .consumeCapacity(VirtualMachineRequest.fromVirtualMachine(virtualMachine));
        });

        logger.info("serverSpareCapacity: " + serverSpareCapacity);

        //Initialising server spare capacity list to answer availability queries without locking
        serverSpareCapacityList.addAll(serverSpareCapacity.entrySet());

        logger.info("Current spare capacity: " + serverSpareCapacityList);
        //TODO: Sort serverSpareCapacityList so that server with minimum load comes up first
    }

    @Override
    public boolean checkAvailability(VirtualMachineRequest virtualMachineRequest) {
        return serverSpareCapacityList.stream()
                .filter(entry -> !serverDeregistrationInProgress.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .anyMatch(currentCapacity -> currentCapacity.canServeRequest(virtualMachineRequest));
    }

    //TODO: use fair re-entrant lock below
    @Override
    public synchronized VirtualMachineDetail acquireVirtualMachine(User user, VirtualMachineRequest virtualMachineRequest) {
        Optional<Map.Entry<Long, ServerCapacity>> prospectServer = serverSpareCapacityList.stream()
                .filter(entry -> !serverDeregistrationInProgress.contains(entry.getKey()))
                .filter(entry -> entry.getValue().canServeRequest(virtualMachineRequest))
                .findAny();
        if (!prospectServer.isPresent()) {
            logger.info("Could not find a suitable server to cater the request");
            return null;
        }
        //TODO: try it, fallback: String pool
        synchronized (Long.valueOf(prospectServer.get().getKey())) { //cached reference, not full proof, should serve purpose
            if (serverDeregistrationInProgress.contains(prospectServer.get().getKey())){
                //Server deregister request registered in parallel
                return acquireVirtualMachine(user, virtualMachineRequest);
            }
            prospectServer.get().getValue().consumeCapacity(virtualMachineRequest);
            Server server = new Server();
            server.setId(prospectServer.get().getKey());
            VirtualMachine virtualMachine = new VirtualMachine();
            virtualMachine.setCpuCores(virtualMachineRequest.getCpuCores());
            virtualMachine.setHdd(virtualMachineRequest.getHdd());
            virtualMachine.setRam(virtualMachineRequest.getRam());
            virtualMachine.setOperatingSystem(virtualMachineRequest.getOperatingSystem());
            virtualMachine.setServer(server);
            virtualMachine.setUser(user);
            virtualMachine = virtualMachineRepository.save(virtualMachine);
            return VirtualMachineDetail.fromVirtualMachine(virtualMachine);
        }
    }

    @Override
    public boolean removeVirtualMachine(Long virtualMachineId) {
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(virtualMachineId)
                .filter(vm ->!vm.getRemoved());

        Long serverId;
        ServerCapacity usedCapacity;

        if(virtualMachine.isPresent()){
            serverId = virtualMachine.get().getServer().getId();
            usedCapacity = ServerCapacity.fromVirtualMachine(virtualMachine.get());
        } else {
            logger.warn("Could not find VM with given id");
            return false;
        }

        int rowsEffected = virtualMachineRepository.softDeleteVirtualMachine(virtualMachineId);
        if (rowsEffected > 0) {
            // add capacity back
            synchronized (Long.valueOf(serverId)) {
                serverSpareCapacity.get(serverId).addCapacity(usedCapacity);
            }
            logger.info("Deleted VM and added capacity back to server");
            recentlyFailedDeregistrationServers.clear(); //marking some capacity is freed and remove server can be attempted again
            //smallestFailedCapacityRelocationAttempt.clear();
            return true;
        }
        logger.info("Could not delete the VM. Please try again");
        return false;
    }

    @Override
    public boolean registerServer(Server server) {
        synchronized (Long.valueOf(server.getId())) { //cached reference, not full proof, should serve purpose
            if (serverMaxCapacity.containsKey(server.getId())) {
                logger.error("Server is already registered with daemon");
                return false;
            } else {
                serverMaxCapacity.put(server.getId(), ServerCapacity.fromServer(server));
                serverSpareCapacity.put(server.getId(), ServerCapacity.fromServer(server));
                serverSpareCapacityList.add(new AbstractMap.SimpleEntry<>(server.getId(), serverSpareCapacity.get(server.getId())));
                return true;
            }
        }
    }

    @Override
    public boolean deregisterServer(Server server) {
        synchronized (Long.valueOf(server.getId())) {
            if (!serverMaxCapacity.containsKey(server.getId())) {
                logger.error("Server is not registered with daemon");
                return false;
            } else if (serverDeregistrationInProgress.contains(server.getId())) {
                logger.error("Server de-registration already in progress");
                return true;
            } else if (recentlyFailedDeregistrationServers.contains(server.getId())) {
                logger.error("Could not de-register server, load not decreased since last failed request");
                return false;
            } else {
                //TODO: refer below:
                /**
                 * 1.3. In SYNC block (serverDeregistrationInProgress object) add server id for deregisteration to serverDeregistrationInProgress set
                 * 1.1. clone the serverSpareCapacityList (except for the capacity of server being removed)
                 * 1.2. Simulate migration, if fails, remove server id from serverDeregistrationInProgress return false
                 * 1.4. Remove spare capacity from serverSpareCapacityList and serverSpareCapacity map
                 * 1.5. Acquire capacity for each VM on other machine: create Map VM id -> new ServerId
                 * 1.6. If acquisition fails at some point, revert already acquired resources,
                 *      put id in recentlyFailedDeregistrationServers (and update) and return false
                 * 1.6. Pass (submit) this map and server Id for removal to async method to perform actual migration -- SYNC Block ends
                 * 1.7. Return true to indicate request is accepted
                 *
                 * 2.1  Async method to perform actual migration (scheduled to run on single thread sequential executor service)
                 * 2.2. Change respective server ids and save one by one
                 * 2.3. Entry might fail if user deleted VM in mean while (also in cases user was deleted)
                 * 2.4. Handle failure, sleep few seconds, re-fetch and retry to succeed.
                 * 2.5. ...
                 * 2.6. Remove server id from serverMaxCapacity
                 * 2.7. Invoke clean-up batch to remove stale entries for deleted server
                 */
                throw new RippleAppRuntimeException("Operation not yet supported");
            }
        }
    }
}
