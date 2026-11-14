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

    public Role findByNom(String nom) {
        return roleRepository.findByNom(nom)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le rôle avec le nom '" + nom + "' n'existe pas."));
    }
}
