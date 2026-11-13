
package com.commune.app.socle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PERMISSION")
public class Permission {

    @Id
    @Column(name = "id_permission", length = 80)
    private String idPermission;

    @Column(name = "action", length = 30, nullable = false)
    private String action;

    @Column(name = "ressource", length = 50, nullable = false)
    private String ressource;

    @Column(name = "description", length = 255)
    private String description;

    public Permission() {
    }

    public String getIdPermission() {
        return idPermission;
    }

    public void setIdPermission(String idPermission) {
        this.idPermission = idPermission;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRessource() {
        return ressource;
    }

    public void setRessource(String ressource) {
        this.ressource = ressource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

 
}
