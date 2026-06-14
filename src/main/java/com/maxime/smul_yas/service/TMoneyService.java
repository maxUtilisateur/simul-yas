package com.maxime.smul_yas.service;

import com.maxime.smul_yas.entity.TmoneyAccount;
import com.maxime.smul_yas.repository.TmoneyAccountRepository;
import com.maxime.smul_yas.utils.PhoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TMoneyService {

    private final TmoneyAccountRepository tmoneyAccountRepository;

    /**
     * Debits the client's database-backed T-Money account.
     *
     * @param phone The subscriber's phone number.
     * @param amount The amount to debit.
     * @param password The T-Money account password / PIN.
     * @throws IllegalArgumentException if the password is wrong or invalid, or if balance is insufficient.
     */
    @Transactional
    public void debitAccount(String phone, BigDecimal amount, String password) {
        String normalizedPhone = PhoneUtils.normalizePhone(phone);

        log.info("Débit T-Money: Initialisation de la demande de débit pour {}...", normalizedPhone);

        if (password == null || password.trim().isEmpty()) {
            log.error("Débit T-Money: Échec. Le mot de passe est obligatoire.");
            throw new IllegalArgumentException("Le mot de passe T-Money est obligatoire.");
        }

        TmoneyAccount account = tmoneyAccountRepository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Compte financier T-Money non trouvé pour le numéro de téléphone: " + normalizedPhone));

        if (!account.getPassword().equals(password)) {
            log.warn("Débit T-Money: Échec. Mot de passe incorrect pour le numéro {}.", normalizedPhone);
            throw new IllegalArgumentException("Mot de passe T-Money incorrect. Échec du débit.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Débit T-Money: Échec. Solde insuffisant pour le numéro {} (Solde: {}, Demandé: {}).", 
                    normalizedPhone, account.getBalance(), amount);
            throw new IllegalArgumentException("Solde T-Money insuffisant pour effectuer le débit.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        tmoneyAccountRepository.save(account);

        log.info("Débit T-Money: Débit de {} XOF effectué avec succès sur le compte {}.", amount, normalizedPhone);
    }
}
