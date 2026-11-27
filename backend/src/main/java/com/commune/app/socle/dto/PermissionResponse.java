package com.commune.app.socle.dto;

import com.commune.app.socle.entity.Permission;

public record PermissionResponse(String idPermission, String action, String ressource, String description, String idService) {
 

    public static PermissionResponse fromEntity(Permission permission) {
        return new PermissionResponse(permission.getIdPermission(), permission.getAction(), permission.getRessource(), permission.getDescription(), permission.getIdService());
    }
}
