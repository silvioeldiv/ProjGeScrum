package com.projectinnovacion.controller;



import com.projectinnovacion.dto.request.RoleAssignmentDTO;
import com.projectinnovacion.dto.response.MessageResponse;
import com.projectinnovacion.dto.response.UserDTO;
import com.projectinnovacion.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Obteniendo usuario con id: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Obteniendo usuario con username: {}", username);
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable Long id, @RequestBody RoleAssignmentDTO roleAssignment) {
        log.info("Asignando roles al usuario con id: {}", id);
        UserDTO updatedUser = userService.assignRoles(id, roleAssignment.getRoles());
        return ResponseEntity.ok(updatedUser);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("Actualizando usuario con id: {}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        log.info("Eliminando usuario con id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("Usuario eliminado exitosamente!"));
    }
}