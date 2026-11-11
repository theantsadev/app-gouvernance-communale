package com.commune.app.socle.repository;

import com.commune.app.socle.entity.HistoriqueConnexion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoriqueConnexionRepository extends JpaRepository<HistoriqueConnexion, String> {

    // Base : génère l'identifiant HCO dans la transaction d'insertion.
    @Query(value = "select generer_id('HCO')", nativeQuery = true)
    String genererId();
}
