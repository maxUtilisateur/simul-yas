package com.maxime.smul_yas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class TMoneyService {

    /**
     * Simulates a debit call to the external T-Money API.
     *
     * @param phone The subscriber's phone number.
     * @param amount The amount to debit.
     * @param password The T-Money account password / PIN.
     * @throws IllegalArgumentException if the password is wrong or invalid.
     */
    public void debitAccount(String phone, BigDecimal amount, String password) {
        log.info("Simulation T-Money: Initialisation de la demande de paiement...");
        log.info("Détails: Téléphone = {}, Montant = {} XOF", phone, amount);

        if (password == null || password.trim().isEmpty()) {
            log.error("Simulation T-Money: Échec du paiement. Le mot de passe est obligatoire.");
            throw new IllegalArgumentException("Le mot de passe T-Money est obligatoire.");
        }

        // For simulation, the default password is "1234"
        if (!"1234".equals(password)) {
            log.warn("Simulation T-Money: Échec du paiement pour le numéro {}. Mot de passe incorrect.", phone);
            throw new IllegalArgumentException("Mot de passe T-Money incorrect. Échec du débit.");
        }

        log.info("Simulation T-Money: Débit de {} XOF sur le compte {} effectué avec succès par l'API externe.", amount, phone);
    }
}
