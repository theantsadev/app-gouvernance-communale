package com.commune.app.socle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commune.app.socle.repository.SessionAuthentificationRepository;
import com.commune.app.socle.entity.SessionAuthentification;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SessionAuthentificationServiceTest {

    private static final String TOKEN = "token-de-test";

    @Test
    void valideUneSessionNonRevoqueeEtNonExpiree() {
        SessionAuthentificationRepository repository =
                org.mockito.Mockito.mock(SessionAuthentificationRepository.class);
        when(repository.trouverValide(any(), any()))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(SessionAuthentification.class)));
        SessionAuthentificationService service =
                new SessionAuthentificationService(repository);

        assertTrue(service.estValide(TOKEN));
        verify(repository).trouverValide(eq(service.empreinte(TOKEN)), any());
    }

    @Test
    void refuseUneSessionExpireeOuRevoquee() {
        SessionAuthentificationRepository repository =
                org.mockito.Mockito.mock(SessionAuthentificationRepository.class);
        when(repository.trouverValide(any(), any())).thenReturn(Optional.empty());
        SessionAuthentificationService service =
                new SessionAuthentificationService(repository);

        assertFalse(service.estValide(TOKEN));
    }

    @Test
    void revoqueLaSessionCourante() {
        SessionAuthentificationRepository repository =
                org.mockito.Mockito.mock(SessionAuthentificationRepository.class);
        SessionAuthentificationService service =
                new SessionAuthentificationService(repository);

        service.revoquer(TOKEN);

        verify(repository).revoquer(eq(service.empreinte(TOKEN)), any(LocalDateTime.class));
    }

    @Test
    void genereUneEmpreinteSha256Stable() {
        SessionAuthentificationRepository repository =
                org.mockito.Mockito.mock(SessionAuthentificationRepository.class);
        SessionAuthentificationService service =
                new SessionAuthentificationService(repository);

        assertEquals(
                "088cee27967dc09bff08ce1dc7a9193f8dfd857a5db8e3b35d97ad71a7eb29d4",
                service.empreinte(TOKEN));
    }
}
