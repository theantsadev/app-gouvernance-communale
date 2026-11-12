package com.commune.app.socle.repository;

import com.commune.app.socle.entity.SessionAuthentification;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionAuthentificationRepository
        extends JpaRepository<SessionAuthentification, String> {

    @Query(value = "select generer_id('SES')", nativeQuery = true)
    String genererId();

    @Query("""
            select s from SessionAuthentification s
            where s.empreinteToken = :empreinte
              and s.dateRevoquee is null
              and s.dateExpiration > :maintenant
            """)
    Optional<SessionAuthentification> trouverValide(
            @Param("empreinte") String empreinte,
            @Param("maintenant") LocalDateTime maintenant);

    @Modifying
    @Query("""
            update SessionAuthentification s
            set s.dateRevoquee = :dateRevoquee
            where s.empreinteToken = :empreinte
              and s.dateRevoquee is null
            """)
    int revoquer(
            @Param("empreinte") String empreinte,
            @Param("dateRevoquee") LocalDateTime dateRevoquee);
}
