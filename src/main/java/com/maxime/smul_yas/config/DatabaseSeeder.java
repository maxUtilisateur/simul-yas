package com.maxime.smul_yas.config;

import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.entity.TmoneyAccount;
import com.maxime.smul_yas.repository.CrmRepository;
import com.maxime.smul_yas.repository.TmoneyAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final CrmRepository crmRepository;
    private final TmoneyAccountRepository tmoneyAccountRepository;

    @org.springframework.beans.factory.annotation.Value("${spring.datasource.url}")
    private String dbUrl;

    @org.springframework.beans.factory.annotation.Value("${spring.datasource.username}")
    private String dbUser;

    @Override
    public void run(String... args) {
        log.info("==================================================");
        log.info("DatabaseSeeder: Démarrage de la mise à jour des données...");
        log.info("Database URL : {}", dbUrl);
        log.info("Database User: {}", dbUser);
        log.info("==================================================");

        String phone = "+22896118586";

        // 1. Initialiser le compte TMoney avec le solde demandé de 40 000 F
        TmoneyAccount account = tmoneyAccountRepository.findByPhone(phone)
                .orElse(new TmoneyAccount());

        account.setPhone(phone);
        account.setBalance(new BigDecimal("40000.00")); // Force la balance à 40000 F
        account.setPassword("1234"); // Mot de passe / PIN par défaut

        tmoneyAccountRepository.save(account);
        log.info("[TMONEY] Compte TMoney mis à jour : {} avec un solde de 40000.00 XOF", phone);

        // 2. Initialiser le client CRM associé
        Crm crm = crmRepository.findByPhone(phone)
                .orElse(new Crm());

        crm.setUser_id("USR-001");
        crm.setFirstName("Maxime");
        crm.setLastName("Togo");
        crm.setEmail("maxime.togo@example.com");
        crm.setPhone(phone);
        crm.setCity("Lomé");
        if (crm.getCreditBalance() == null) {
            crm.setCreditBalance(new BigDecimal("1000.00"));
        }

        crmRepository.save(crm);
        log.info("[CRM] Client CRM associé mis à jour : {}", phone);

        // 3. Initialiser le bénéficiaire secondaire de test (+22896699098)
        String phoneBeneficiary = "+22896699098";
        Crm beneficiary = crmRepository.findByPhone(phoneBeneficiary)
                .orElse(new Crm());

        beneficiary.setUser_id("USR-002");
        beneficiary.setFirstName("Ablavi");
        beneficiary.setLastName("Koffi");
        beneficiary.setEmail("ablavi@example.com");
        beneficiary.setPhone(phoneBeneficiary);
        beneficiary.setCity("Lomé");
        if (beneficiary.getCreditBalance() == null) {
            beneficiary.setCreditBalance(new BigDecimal("500.00"));
        }

        crmRepository.save(beneficiary);
        log.info("[CRM] Client CRM bénéficiaire mis à jour : {}", phoneBeneficiary);

        log.info("==================================================");
        log.info("DatabaseSeeder: Mise à jour terminée avec succès !");
        log.info("==================================================");
    }
}
