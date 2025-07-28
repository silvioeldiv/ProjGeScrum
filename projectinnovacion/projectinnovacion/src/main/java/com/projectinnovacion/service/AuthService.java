package com.projectinnovacion.service;

import com.projectinnovacion.dto.request.LoginRequest;
import com.projectinnovacion.dto.request.SignupRequest;
import com.projectinnovacion.dto.response.JwtResponse;
import com.projectinnovacion.dto.response.MessageResponse;
import com.projectinnovacion.model.User;
import com.projectinnovacion.model.UserRole;
import com.projectinnovacion.model.enums.ERole;  // CAMBIO: ERole en lugar de RoleType
import com.projectinnovacion.repository.UserRepository;
import com.projectinnovacion.repository.UserRoleRepository;
import com.projectinnovacion.security.jwt.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        // Crear nueva cuenta de usuario
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .active(true)
                .build();

        Set<String> strRoles = signUpRequest.getRoles();
        Set<UserRole> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Por defecto, asignar rol DEVELOPER
            UserRole userRole = roleRepository.findByName(ERole.DEVELOPER)  // CAMBIO
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        UserRole adminRole = roleRepository.findByName(ERole.ADMIN)  // CAMBIO
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "SCRUM_MASTER":
                        UserRole scrumMasterRole = roleRepository.findByName(ERole.SCRUM_MASTER)  // CAMBIO
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(scrumMasterRole);
                        break;
                    case "PRODUCT_OWNER":
                        UserRole productOwnerRole = roleRepository.findByName(ERole.PRODUCT_OWNER)  // CAMBIO
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(productOwnerRole);
                        break;
                    default:
                        UserRole developerRole = roleRepository.findByName(ERole.DEVELOPER)  // CAMBIO
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(developerRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("Usuario registrado exitosamente!");
    }
}