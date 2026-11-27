package com.commune.app.socle.dto;

import com.commune.app.socle.entity.Role;

public record RoleResponse(String idRole, String nom, String description) {
    public static RoleResponse fromEntity(Role role) {
        return new RoleResponse(role.getIdRole(), role.getNom(), role.getDescription());
    }

}
