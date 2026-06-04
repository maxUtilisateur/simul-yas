package com.maxime.smul_yas.service;

import com.maxime.smul_yas.dto.offres_dto.AcheterForfaitDto;
import com.maxime.smul_yas.dto.offres_dto.ActiveForfaitResponseDto;
import com.maxime.smul_yas.dto.offres_dto.OffreResponseDto;
import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.entity.Offres;
import com.maxime.smul_yas.entity.UserForfait;
import com.maxime.smul_yas.enums.CategoriesType;
import com.maxime.smul_yas.mapper.offres.OffresMapper;
import com.maxime.smul_yas.repository.CrmRepository;
import com.maxime.smul_yas.repository.OffresRepository;
import com.maxime.smul_yas.repository.UserForfaitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffresService {

    private final OffresRepository offresRepository;
    private final CrmRepository crmRepository;
    private final UserForfaitRepository userForfaitRepository;
    private final OffresMapper offresMapper;

    @Transactional(readOnly = true)
    public List<OffreResponseDto> afficherForfaits() {
        List<Offres> activeOffres = offresRepository.findByStatusTrue();
        return offresMapper.toOffreResponseDtoList(activeOffres);
    }

    public List<CategoriesType> afficherCategories() {
        return List.of(CategoriesType.values());
    }

    @Transactional
    public ActiveForfaitResponseDto acheterForfait(AcheterForfaitDto dto) {
        // Find Buyer
        Crm buyer = crmRepository.findByPhone(dto.getBuyerPhone())
                .orElseThrow(() -> new IllegalArgumentException("Acheteur non trouvé avec le numéro de téléphone: " + dto.getBuyerPhone()));

        // Determine Beneficiary
        final String finalBeneficiaryPhone = (dto.getBeneficiaryPhone() == null || dto.getBeneficiaryPhone().trim().isEmpty())
                ? dto.getBuyerPhone()
                : dto.getBeneficiaryPhone();

        Crm beneficiary = crmRepository.findByPhone(finalBeneficiaryPhone)
                .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire non trouvé avec le numéro de téléphone: " + finalBeneficiaryPhone));

        // Find Offer
        Offres offre = offresRepository.findByForfaitsId(dto.getForfaitsId())
                .orElseThrow(() -> new IllegalArgumentException("Forfait non trouvé avec l'identifiant: " + dto.getForfaitsId()));

        if (!offre.isStatus()) {
            throw new IllegalArgumentException("Ce forfait n'est plus actif.");
        }

        // Verify Credit Balance of Buyer
        BigDecimal buyerBalance = buyer.getCreditBalance();
        if (buyerBalance == null || buyerBalance.compareTo(offre.getPrice()) < 0) {
            throw new IllegalArgumentException("Crédit insuffisant pour acheter ce forfait. Prix: " 
                    + offre.getPrice() + ", Solde: " + buyerBalance);
        }

        // Deduct price from buyer
        buyer.setCreditBalance(buyerBalance.subtract(offre.getPrice()));
        crmRepository.save(buyer);

        // Create new UserForfait for beneficiary
        LocalDateTime expiration = LocalDateTime.now().plusDays(offre.getValidite());
        UserForfait userForfait = UserForfait.builder()
                .crm(beneficiary)
                .offres(offre)
                .voiceRemaining(offre.getVoice())
                .smsRemaining(offre.getSms())
                .internetRemaining(offre.getInternet())
                .expirationDate(expiration)
                .active(true)
                .build();

        UserForfait saved = userForfaitRepository.save(userForfait);

        return offresMapper.toActiveForfaitResponseDto(saved);
    }

    @Transactional
    public List<ActiveForfaitResponseDto> consulterSoldeForfaits(String phone) {
        final String sanitizedPhone = phone == null ? null : phone.trim().replace(" ", "+");
        // Verify user exists
        crmRepository.findByPhone(sanitizedPhone)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec le numéro de téléphone: " + sanitizedPhone));

        // Retrieve active forfaits
        List<UserForfait> activeForfaits = userForfaitRepository.findByCrmPhoneAndActiveTrue(sanitizedPhone);
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;

        for (UserForfait uf : activeForfaits) {
            if (uf.getExpirationDate().isBefore(now)) {
                uf.setActive(false);
                userForfaitRepository.save(uf);
                changed = true;
            }
        }

        if (changed) {
            activeForfaits = userForfaitRepository.findByCrmPhoneAndActiveTrue(sanitizedPhone);
        }

        return offresMapper.toActiveForfaitResponseDtoList(activeForfaits);
    }
}
