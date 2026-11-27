package com.commune.app.socle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.commune.app.socle.dto.PermissionResponse;
import com.commune.app.socle.dto.RoleResponse;
import com.commune.app.socle.entity.Role;
import com.commune.app.socle.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Role findByIdRole(String idRole) {
        return roleRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le rôle avec l'ID '" + idRole + "' n'existe pas."));
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> findPermissionsByRoleId(String idRole) {
        Role role = findByIdRole(idRole);
        return role.getPermissions().stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
