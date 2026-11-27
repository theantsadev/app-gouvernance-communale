package com.commune.app.socle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.commune.app.socle.dto.PermissionResponse;
import com.commune.app.socle.dto.RoleResponse;
import com.commune.app.socle.service.RoleService;

@RestController
@RequestMapping("/api/socle/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.findAll();
    }

    @GetMapping("/{idRole}/permissions")
    public List<PermissionResponse> getPermissionsByRoleId(@PathVariable String idRole) {
        return roleService.findPermissionsByRoleId(idRole);
    }

}
