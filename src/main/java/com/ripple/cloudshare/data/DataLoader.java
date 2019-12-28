package com.ripple.cloudshare.data;

import com.ripple.cloudshare.data.entity.*;
import com.ripple.cloudshare.data.repository.ServerRepository;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.data.repository.VirtualMachineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final VirtualMachineRepository virtualMachineRepository;

    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, ServerRepository serverRepository,
                      VirtualMachineRepository virtualMachineRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
        this.virtualMachineRepository = virtualMachineRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User systemDaemonUser = new User();
        systemDaemonUser.setName("SYSTEM");
        systemDaemonUser.setMobile("9999988888");
        systemDaemonUser.setEmail("no-reply@ripple.com");
        systemDaemonUser.setPassword(passwordEncoder.encode("cloudshare"));
        systemDaemonUser.setUserType(UserType.ADMIN);
        systemDaemonUser = userRepository.save(systemDaemonUser);

        Long userId = systemDaemonUser.getId();

        Server managerServer = new Server();
        managerServer.setServerName("server-A");
        managerServer.setSharedCpuCores(256);
        managerServer.setSharedMemory(768);
        managerServer.setSharedDisk(32000);
        managerServer = serverRepository.save(managerServer);

        VirtualMachine systemVirtualMachine = new VirtualMachine();
        systemVirtualMachine.setDescription("Sample service VM");
        systemVirtualMachine.setCpuCores(2);
        systemVirtualMachine.setHdd(10);
        systemVirtualMachine.setRam(16);
        systemVirtualMachine.setOperatingSystem(OperatingSystem.LINUX);
        systemVirtualMachine.setUser(systemDaemonUser);
        systemVirtualMachine.setServer(managerServer);
        virtualMachineRepository.save(systemVirtualMachine);

        //below is for experiments and trials

        User user = new User();
        user.setName("Vishal");
        user.setMobile("9816923670");
        user.setEmail("vishalgoel03@gmail.com");
        user.setPassword(passwordEncoder.encode("vg003"));
        user.setUserType(UserType.ADMIN);

        userRepository.save(user);

        user = new User();
        user.setName("Abhishek");
        user.setMobile("9816923600");
        user.setEmail("vishalgoel5@gmail.com");
        user.setPassword(passwordEncoder.encode("root"));
        user.setUserType(UserType.NON_ADMIN);

        userRepository.save(user);

        List<VirtualMachine> machineList = virtualMachineRepository.findAllByUserId(userId);
        machineList.forEach(m -> System.out.println(m.getId()));

        Optional<User> machineUser = userRepository.getUserByMachineId(machineList.get(0).getId());
        System.out.println(machineUser.get().getUserType().name());
    }
}
