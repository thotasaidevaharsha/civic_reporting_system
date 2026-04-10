package com.civicreport.config;

import com.civicreport.model.Admin;
import com.civicreport.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DevDataSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevDataSeeder.class);

    @Bean
    public CommandLineRunner seedAdmins(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.count() > 0) {
                return;
            }

            List<Admin> admins = List.of(
                    buildAdmin("north_admin", "North Zone Officer", "North Zone", "north@123", passwordEncoder),
                    buildAdmin("south_admin", "South Zone Officer", "South Zone", "south@123", passwordEncoder),
                    buildAdmin("east_admin", "East Zone Officer", "East Zone", "east@123", passwordEncoder),
                    buildAdmin("west_admin", "West Zone Officer", "West Zone", "west@123", passwordEncoder),
                    buildAdmin("central_admin", "Central Zone Officer", "Central Zone", "central@123", passwordEncoder)
            );

            adminRepository.saveAll(admins);
            LOGGER.info("Seeded {} admin records for development use", admins.size());
        };
    }

    private Admin buildAdmin(
            String adminId,
            String name,
            String zone,
            String rawPassword,
            BCryptPasswordEncoder passwordEncoder
    ) {
        Admin admin = new Admin();
        admin.setAdminId(adminId);
        admin.setName(name);
        admin.setZone(zone);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        return admin;
    }
}
