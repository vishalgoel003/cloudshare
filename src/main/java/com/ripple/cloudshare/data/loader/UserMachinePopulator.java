package com.ripple.cloudshare.data.loader;

import com.ripple.cloudshare.data.dao.VirtualMachineDAOService;
import com.ripple.cloudshare.data.entity.OperatingSystem;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component(value = UserMachinePopulator.USER_MACHINE_POPULATOR_BEAN_NAME)
@DependsOn(value = {
        DefaultServerPopulate.DEFAULT_SERVER_POPULATE_BEAN_NAME
})
public class UserMachinePopulator implements CommandLineRunner {

    public static final String USER_MACHINE_POPULATOR_BEAN_NAME = "userMachinePopulator";

    private final UserRepository userRepository;
    private final VirtualMachineDAOService virtualMachineDAOService;

    private final PasswordEncoder passwordEncoder;

    public UserMachinePopulator(UserRepository userRepository,
                                VirtualMachineDAOService virtualMachineDAOService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.virtualMachineDAOService = virtualMachineDAOService;
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

        VirtualMachineRequest virtualMachineRequest = new VirtualMachineRequest();
        virtualMachineRequest.setCpuCores(2);
        virtualMachineRequest.setHdd(10);
        virtualMachineRequest.setRam(16);
        virtualMachineRequest.setOperatingSystem(OperatingSystem.LINUX);
        virtualMachineDAOService.acquireVirtualMachineFromCloudManager(systemDaemonUser.getId(), virtualMachineRequest);

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

        user = userRepository.save(user);

        virtualMachineRequest = new VirtualMachineRequest();
        virtualMachineRequest.setCpuCores(2);
        virtualMachineRequest.setHdd(10);
        virtualMachineRequest.setRam(2);
        virtualMachineRequest.setOperatingSystem(OperatingSystem.LINUX);
        virtualMachineDAOService.acquireVirtualMachineFromCloudManager(user.getId(), virtualMachineRequest);

        user = new User();
        user.setName("Lakshay");
        user.setMobile("9816923000");
        user.setEmail("vishalgoel@gmail.com");
        user.setPassword(passwordEncoder.encode("root"));
        user.setUserType(UserType.NON_ADMIN);

        user = userRepository.save(user);

        virtualMachineRequest = new VirtualMachineRequest();
        virtualMachineRequest.setCpuCores(2);
        virtualMachineRequest.setHdd(10);
        virtualMachineRequest.setRam(8);
        virtualMachineRequest.setOperatingSystem(OperatingSystem.LINUX);
        virtualMachineDAOService.acquireVirtualMachineFromCloudManager(user.getId(), virtualMachineRequest);
    }
}
