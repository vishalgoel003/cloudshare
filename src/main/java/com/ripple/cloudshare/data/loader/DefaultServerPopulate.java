package com.ripple.cloudshare.data.loader;

import com.ripple.cloudshare.data.entity.Server;
import com.ripple.cloudshare.data.repository.ServerRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component(value = DefaultServerPopulate.DEFAULT_SERVER_POPULATE_BEAN_NAME)
public class DefaultServerPopulate {

    public static final String DEFAULT_SERVER_POPULATE_BEAN_NAME = "defaultServerPopulate";
    private final ServerRepository serverRepository;

    public DefaultServerPopulate(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @PostConstruct
    void populateServer() {
        Server managerServer = new Server();
        managerServer.setServerName("server-A");
        managerServer.setSharedCpuCores(256);
        managerServer.setSharedMemory(768);
        managerServer.setSharedDisk(32000);
        serverRepository.save(managerServer);
    }

}
