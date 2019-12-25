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

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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

        userRepository.save(defaultAdminUser);

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
