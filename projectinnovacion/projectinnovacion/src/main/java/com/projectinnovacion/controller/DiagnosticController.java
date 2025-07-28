package com.projectinnovacion.controller;

import com.projectinnovacion.model.User;
import com.projectinnovacion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diagnostic")
@CrossOrigin(origins = "*")
public class DiagnosticController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/check-admin")
    public ResponseEntity<?> checkAdmin() {
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            return ResponseEntity.ok("Admin NO existe");
        }

        // Verificar si la contraseña coincide
        boolean passwordMatches = passwordEncoder.matches("admin123", admin.getPassword());

        // Generar un nuevo hash para comparar
        String newHash = passwordEncoder.encode("admin123");

        Map<String, Object> info = new HashMap<>();
        info.put("exists", true);
        info.put("username", admin.getUsername());
        info.put("email", admin.getEmail());
        info.put("passwordMatches", passwordMatches);
        info.put("currentHash", admin.getPassword().substring(0, 20) + "..."); // Mostrar solo parte del hash
        info.put("active", admin.getActive());
        info.put("roles", admin.getRoles().stream()
                .map(r -> r.getName().toString())
                .collect(Collectors.toList()));

        return ResponseEntity.ok(info);
    }

    @PostMapping("/update-admin-password")
    public ResponseEntity<?> updateAdminPassword() {
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        String newPassword = passwordEncoder.encode("admin123");
        admin.setPassword(newPassword);
        userRepository.save(admin);

        return ResponseEntity.ok(Map.of(
                "message", "Contraseña actualizada",
                "username", "admin",
                "password", "admin123",
                "hash", newPassword.substring(0, 20) + "..."
        ));
    }
}