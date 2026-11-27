package com.commune.app.socle.repository;

import com.commune.app.socle.entity.Utilisateur;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// Base : recherche l'utilisateur sans distinguer les majuscules de l'e-mail.
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {

    @EntityGraph(attributePaths = { "role", "role.permissions" })
    Optional<Utilisateur> findByEmail(String email);

    @EntityGraph(attributePaths = { "role", "role.permissions" })
    Optional<Utilisateur> findById(String id); // idem findByEmail

    boolean existsByRole_IdRole(String idRole);

    @Query(value = "select generer_id('UTI')", nativeQuery = true)
    String genererId();
}
