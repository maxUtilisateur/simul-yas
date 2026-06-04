package com.maxime.smul_yas.service;

import com.maxime.smul_yas.dto.crm_dto.CreditBalanceResponseDto;
import com.maxime.smul_yas.dto.crm_dto.RechargeCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TransfererCreditDto;
import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.mapper.crm.CrmMapper;
import com.maxime.smul_yas.repository.CrmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CrmService {

    private final CrmRepository crmRepository;
    private final CrmMapper crmMapper;
    private final TMoneyService tmoneyService;

    @Transactional
    public CreditBalanceResponseDto rechargerCredit(RechargeCreditDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de recharge doit être supérieur à 0");
        }

        // Valider l'acheteur
        Crm buyer = crmRepository.findByPhone(dto.getBuyerPhone())
                .orElseThrow(() -> new IllegalArgumentException("Acheteur non trouvé avec le numéro de téléphone: " + dto.getBuyerPhone()));

        // Déterminer le bénéficiaire
        final String finalBeneficiaryPhone = (dto.getBeneficiaryPhone() == null || dto.getBeneficiaryPhone().trim().isEmpty())
                ? dto.getBuyerPhone()
                : dto.getBeneficiaryPhone();

        Crm beneficiary = crmRepository.findByPhone(finalBeneficiaryPhone)
                .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire non trouvé avec le numéro de téléphone: " + finalBeneficiaryPhone));

        // Débit via l'API externe T-Money sur le compte de l'acheteur avant d'accorder le crédit
        tmoneyService.debitAccount(dto.getBuyerPhone(), dto.getAmount(), dto.getPassword());

        BigDecimal currentBalance = beneficiary.getCreditBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        beneficiary.setCreditBalance(currentBalance.add(dto.getAmount()));
        Crm saved = crmRepository.save(beneficiary);
        return crmMapper.toCreditBalanceResponseDto(saved);
    }

    @Transactional
    public CreditBalanceResponseDto transfererCredit(TransfererCreditDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du transfert doit être supérieur à 0");
        }
        if (dto.getSenderPhone().equals(dto.getReceiverPhone())) {
            throw new IllegalArgumentException("Le numéro de téléphone de l'expéditeur et du destinataire doit être différent");
        }

        Crm sender = crmRepository.findByPhone(dto.getSenderPhone())
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé avec le numéro: " + dto.getSenderPhone()));

        Crm receiver = crmRepository.findByPhone(dto.getReceiverPhone())
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé avec le numéro: " + dto.getReceiverPhone()));

        BigDecimal senderBalance = sender.getCreditBalance();
        if (senderBalance == null || senderBalance.compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("Crédit insuffisant pour effectuer le transfert. Solde actuel: " + senderBalance);
        }

        sender.setCreditBalance(senderBalance.subtract(dto.getAmount()));

        BigDecimal receiverBalance = receiver.getCreditBalance();
        if (receiverBalance == null) {
            receiverBalance = BigDecimal.ZERO;
        }
        receiver.setCreditBalance(receiverBalance.add(dto.getAmount()));

        crmRepository.save(sender);
        crmRepository.save(receiver);

        return crmMapper.toCreditBalanceResponseDto(sender);
    }

    @Transactional(readOnly = true)
    public CreditBalanceResponseDto consulterSoldeCredit(String phone) {
        final String sanitizedPhone = phone == null ? null : phone.trim().replace(" ", "+");
        Crm crm = crmRepository.findByPhone(sanitizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec le numéro de téléphone: " + sanitizedPhone));
        return crmMapper.toCreditBalanceResponseDto(crm);
    }
}
