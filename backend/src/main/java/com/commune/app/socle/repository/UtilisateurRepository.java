package com.commune.app.socle.repository;

import com.commune.app.socle.entity.Utilisateur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Base : recherche l'utilisateur sans distinguer les majuscules de l'e-mail.
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {

    @Query("select u from Utilisateur u where lower(u.email) = lower(:email)")
    Optional<Utilisateur> findByEmail(@Param("email") String email);

    boolean existsByIdRole(String idRole);

    @Query(value = "select generer_id('UTI')", nativeQuery = true)
    String genererId();
}
