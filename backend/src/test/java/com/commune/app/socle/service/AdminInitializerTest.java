package com.commune.app.socle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminInitializerTest {

    @Test
    void neCreePasUnDeuxiemeAdministrateur() {
        UtilisateurRepository repository = org.mockito.Mockito.mock(UtilisateurRepository.class);
        PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        RoleService roleService = org.mockito.Mockito.mock(RoleService.class);
        when(repository.existsByRole_IdRole("ADMINISTRATEUR")).thenReturn(true);
        UtilisateurService utilisateurService = new UtilisateurService(repository, passwordEncoder, roleService);
        AdminInitializer initializer = new AdminInitializer(utilisateurService, "", "");

        initializer.run();

        verify(repository, never()).genererId();
        verify(repository, never()).save(any(Utilisateur.class));
    }

    @Test
    void refuseLaCreationSansVariablesDenvironnement() {
        UtilisateurRepository repository = org.mockito.Mockito.mock(UtilisateurRepository.class);
        PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        RoleService roleService = org.mockito.Mockito.mock(RoleService.class);
        when(repository.existsByRole_IdRole("ADMINISTRATEUR")).thenReturn(false);
        UtilisateurService utilisateurService = new UtilisateurService(repository, passwordEncoder, roleService);
        AdminInitializer initializer = new AdminInitializer(utilisateurService, "", "");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                initializer::run);

        assertEquals(
                "ADMIN_EMAIL et ADMIN_PASSWORD sont obligatoires pour créer le premier administrateur.",
                exception.getMessage());
        verify(repository, never()).genererId();
    }

    @Test
    void creeLePremierAdministrateurAvecLeMotDePasseEncode() {
        UtilisateurRepository repository = org.mockito.Mockito.mock(UtilisateurRepository.class);
        PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        RoleService roleService = org.mockito.Mockito.mock(RoleService.class);
        when(repository.existsByRole_IdRole("ADMINISTRATEUR")).thenReturn(false);
        when(repository.genererId()).thenReturn("UTI-00001");
        when(passwordEncoder.encode("mot-de-passe")).thenReturn("{bcrypt}hash");
        UtilisateurService utilisateurService = new UtilisateurService(repository, passwordEncoder, roleService);
        AdminInitializer initializer = new AdminInitializer(
                utilisateurService,
                "admin@example.org",
                "mot-de-passe");

        initializer.run();

        verify(passwordEncoder).encode("mot-de-passe");
        verify(repository).save(any(Utilisateur.class));
    }
}
