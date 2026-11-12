package com.commune.app.socle.service;

import com.commune.app.socle.entity.HistoriqueConnexion;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.HistoriqueConnexionRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoriqueConnexionService {

    private final HistoriqueConnexionRepository historiqueRepository;

    public HistoriqueConnexionService(HistoriqueConnexionRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    // Base : enregistre une connexion réussie dans la transaction du login.
    @Transactional
    public void enregistrerConnexion(Utilisateur utilisateur, String adresseIp) {
        enregistrer(
                utilisateur.getEmail(),
                "CONNEXION",
                adresseIp,
                utilisateur.getIdUtilisateur());
    }

    // Base : conserve l'échec dans une transaction indépendante du rollback.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enregistrerEchec(String email, String adresseIp, String idUtilisateur) {
        enregistrer(email, "ECHEC", adresseIp, idUtilisateur);
    }

    // Base : conserve la déconnexion de l'utilisateur identifié.
    @Transactional
    public void enregistrerDeconnexion(Utilisateur utilisateur, String adresseIp) {
        enregistrer(
                utilisateur.getEmail(),
                "DECONNEXION",
                adresseIp,
                utilisateur.getIdUtilisateur());
    }

    private void enregistrer(
            String email,
            String type,
            String adresseIp,
            String idUtilisateur) {
        HistoriqueConnexion historique = new HistoriqueConnexion(
                historiqueRepository.genererId(),
                email,
                type,
                LocalDateTime.now(),
                adresseIp,
                idUtilisateur);
        historiqueRepository.save(historique);
    }
}
