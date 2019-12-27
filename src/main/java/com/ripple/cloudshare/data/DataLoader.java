package com.ripple.cloudshare.data;

import com.ripple.cloudshare.data.entity.*;
import com.ripple.cloudshare.data.repository.ServerRepository;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.data.repository.VirtualMachineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        User defaultAdminUser = new User();
        defaultAdminUser.setName("Vishal");
        defaultAdminUser.setMobile("9816923672");
        defaultAdminUser.setEmail("vishalgoel003@gmail.com");
        defaultAdminUser.setPassword(passwordEncoder.encode("root"));
        defaultAdminUser.setUserType(UserType.ADMIN);
        defaultAdminUser = userRepository.save(defaultAdminUser);

        Server defaultServer = new Server();
        defaultServer.setServerName("server-A");
        defaultServer.setSharedCpuCores(256);
        defaultServer.setSharedMemory(768);
        defaultServer.setSharedDisk(32000);
        defaultServer = serverRepository.save(defaultServer);

        VirtualMachine defaultVirtualMachine = new VirtualMachine();
        defaultVirtualMachine.setDescription("Sample service VM");
        defaultVirtualMachine.setCpuCores(2);
        defaultVirtualMachine.setHdd(10);
        defaultVirtualMachine.setRam(16);
        defaultVirtualMachine.setOperatingSystem(OperatingSystem.LINUX);
        defaultVirtualMachine.setUser(defaultAdminUser);
        defaultVirtualMachine.setServer(defaultServer);
        virtualMachineRepository.save(defaultVirtualMachine);


        User user = new User();
        user.setName("Tishal");
        user.setMobile("9816923671");
        user.setEmail("vishalgoel004@gmail.com");
        user.setPassword(passwordEncoder.encode("root"));
        user.setUserType(UserType.ADMIN);

        userRepository.save(user);

        user = new User();
        user.setName("Rishal");
        user.setMobile("9816923670");
        user.setEmail("vishalgoel005@gmail.com");
        user.setPassword(passwordEncoder.encode("root"));
        user.setUserType(UserType.NON_ADMIN);

        userRepository.save(user);
    }
}
