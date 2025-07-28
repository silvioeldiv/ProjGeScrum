package com.projectinnovacion.config;

import com.projectinnovacion.model.User;
import com.projectinnovacion.model.UserRole;
import com.projectinnovacion.model.enums.ERole;
import com.projectinnovacion.repository.UserRepository;
import com.projectinnovacion.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Iniciando inicialización de roles...");

        // Crear roles
        Arrays.asList(ERole.values()).forEach(eRole -> {
            if (!roleRepository.existsByName(eRole)) {
                UserRole role = new UserRole();
                role.setName(eRole);

                switch (eRole) {
                    case ADMIN:  // Cambio aquí
                        role.setDescription("Administrador del sistema con acceso completo");
                        break;
                    case SCRUM_MASTER:  // Cambio aquí
                        role.setDescription("Scrum Master del equipo");
                        break;
                    case PRODUCT_OWNER:  // Cambio aquí
                        role.setDescription("Product Owner del proyecto");
                        break;
                    case DEVELOPER:  // Cambio aquí
                        role.setDescription("Desarrollador del equipo");
                        break;
                }
                roleRepository.save(role);
                log.info("Rol {} creado", eRole);
            }
        });

        log.info("Inicialización de roles completada");

        // Crear usuario admin si no existe
        if (!userRepository.existsByUsername("adm1")) {
            log.info("Creando usuario admin...");

            UserRole adminRole = roleRepository.findByName(ERole.ADMIN)  // Cambio aquí
                    .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado"));

            User admin = new User();
            admin.setUsername("adm1");
            admin.setPassword(passwordEncoder.encode("1234567"));
            admin.setEmail("adm1@example.com");
            admin.setFirstName("adm1");
            admin.setLastName("User");
            admin.setActive(true);

            Set<UserRole> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);

            log.info("Usuario admin creado exitosamente - username: adm1, password: 1234567");
        } else {
            log.info("Usuario admin ya existe");
            // Opcional: Actualizar la contraseña del admin existente
            User admin = userRepository.findByUsername("adm1").orElseThrow();
            admin.setPassword(passwordEncoder.encode("1234567"));
            userRepository.save(admin);
            log.info("Contraseña de admin actualizada");
        }
    }
}