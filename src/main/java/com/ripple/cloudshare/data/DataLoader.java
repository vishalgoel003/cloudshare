package com.ripple.cloudshare.data;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        User defaultAdminUser = new User();
        defaultAdminUser.setName("Vishal");
        defaultAdminUser.setMobile("9816923672");
        defaultAdminUser.setEmail("vishalgoel003@gmail.com");
        defaultAdminUser.setPassword(passwordEncoder.encode("root"));
        defaultAdminUser.setUserType(UserType.ADMIN);

        userRepository.save(defaultAdminUser);
    }
}
