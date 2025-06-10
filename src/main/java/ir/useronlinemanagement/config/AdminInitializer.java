package ir.useronlinemanagement.config;

import ir.useronlinemanagement.model.Role;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.repository.RoleRepository;
import ir.useronlinemanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInitializer.class);

    public AdminInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin_panel").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role("ADMIN");
                        role.setUsers(new ArrayList<>());
                        return roleRepository.save(role);
                    });

            User admin = new User();
            admin.setUsername("admin_panel");
            admin.setPassword(passwordEncoder.encode("admin_panel"));
            admin.setEmail("admin@mail");
            admin.setPhone("09123456783");
            admin.setDeleted(false);
            admin.setFirstName("mohammad");
            admin.setLastName("maleki");
            admin.setCreatedAt(Instant.now());

            // اضافه کردن نقش به کاربر
            admin.setRoles(List.of(adminRole));

            // اضافه کردن کاربر به نقش
            adminRole.getUsers().add(admin);

            userRepository.save(admin); // چون رابطه ManyToMany هست، این باید کافیه باشه

            LOGGER.info("default admin panel has been created successfully");
        }
    }


}

