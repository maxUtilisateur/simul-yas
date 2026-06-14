package com.maxime.smul_yas.service;

import com.maxime.smul_yas.dto.crm_dto.CreditBalanceResponseDto;
import com.maxime.smul_yas.dto.crm_dto.RechargeCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TransfererCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TmoneyAccountResponseDto;
import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.entity.TmoneyAccount;
import com.maxime.smul_yas.mapper.crm.CrmMapper;
import com.maxime.smul_yas.mapper.crm.TmoneyAccountMapper;
import com.maxime.smul_yas.repository.CrmRepository;
import com.maxime.smul_yas.repository.TmoneyAccountRepository;
import com.maxime.smul_yas.utils.PhoneUtils;
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
    private final TmoneyAccountRepository tmoneyAccountRepository;
    private final TmoneyAccountMapper tmoneyAccountMapper;

    @Transactional
    public CreditBalanceResponseDto rechargerCredit(RechargeCreditDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de recharge doit être supérieur à 0");
        }

        // Valider l'acheteur
        String buyerPhone = PhoneUtils.normalizePhone(dto.getBuyerPhone());
        Crm buyer = crmRepository.findByPhone(buyerPhone)
                .orElseThrow(() -> new IllegalArgumentException("Acheteur non trouvé avec le numéro de téléphone: " + buyerPhone));

        // Déterminer le bénéficiaire
        final String beneficiaryPhone = (dto.getBeneficiaryPhone() == null || dto.getBeneficiaryPhone().trim().isEmpty())
                ? dto.getBuyerPhone()
                : dto.getBeneficiaryPhone();
        String normalizedBeneficiaryPhone = PhoneUtils.normalizePhone(beneficiaryPhone);

        Crm beneficiary = crmRepository.findByPhone(normalizedBeneficiaryPhone)
                .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire non trouvé avec le numéro de téléphone: " + normalizedBeneficiaryPhone));

        // Débit via l'API externe T-Money sur le compte de l'acheteur avant d'accorder le crédit
        tmoneyService.debitAccount(buyerPhone, dto.getAmount(), dto.getPassword());

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

        String senderPhone = PhoneUtils.normalizePhone(dto.getSenderPhone());
        String receiverPhone = PhoneUtils.normalizePhone(dto.getReceiverPhone());

        if (senderPhone.equals(receiverPhone)) {
            throw new IllegalArgumentException("Le numéro de téléphone de l'expéditeur et du destinataire doit être différent");
        }

        Crm sender = crmRepository.findByPhone(senderPhone)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé avec le numéro: " + senderPhone));

        Crm receiver = crmRepository.findByPhone(receiverPhone)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé avec le numéro: " + receiverPhone));

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
        final String sanitizedPhone = PhoneUtils.normalizePhone(phone);
        Crm crm = crmRepository.findByPhone(sanitizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec le numéro de téléphone: " + sanitizedPhone));
        return crmMapper.toCreditBalanceResponseDto(crm);
    }

    @Transactional(readOnly = true)
    public TmoneyAccountResponseDto consulterSoldeTmoney(String phone) {
        final String sanitizedPhone = PhoneUtils.normalizePhone(phone);
        TmoneyAccount account = tmoneyAccountRepository.findByPhone(sanitizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Compte financier T-Money non trouvé pour le numéro de téléphone: " + sanitizedPhone));
        return tmoneyAccountMapper.toTmoneyAccountResponseDto(account);
    }
}
