package com.commune.app.socle.service;

import org.springframework.stereotype.Service;

import com.commune.app.socle.entity.Role;
import com.commune.app.socle.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByIdRole(String idRole) {
        return roleRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le rôle avec l'ID '" + idRole + "' n'existe pas."));
    }
}
