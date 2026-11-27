import type { PermissionResponse } from "../types/permission";
import type { RoleResponse } from "../types/role";
import { apiFetch } from "./apiClient";

// Intégration : récupère tous les rôles depuis l'API.
export async function getAllRoles(): Promise<RoleResponse[]> {
    return apiFetch<RoleResponse[]>("/api/socle/roles");
}

// Intégration : récupère les permissions associées à un rôle depuis l'API.
export async function getPermissionsByRoleId(roleId: string): Promise<PermissionResponse[]> {
    return apiFetch<PermissionResponse[]>(`/api/socle/roles/${roleId}/permissions`);
}
