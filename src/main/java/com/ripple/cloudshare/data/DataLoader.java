package com.ripple.cloudshare.data;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User defaultAdminUser = new User();
        defaultAdminUser.setName("Vishal");
        defaultAdminUser.setMobile("9816923672");
        defaultAdminUser.setEmail("vishalgoel003@gmail.com");
        defaultAdminUser.setPassword("root");
        defaultAdminUser.setUserType(UserType.ADMIN);

        userRepository.save(defaultAdminUser);
    }
}
