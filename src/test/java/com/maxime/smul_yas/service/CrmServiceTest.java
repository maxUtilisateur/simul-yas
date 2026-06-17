package com.maxime.smul_yas.service;

import com.maxime.smul_yas.dto.crm_dto.CreditBalanceResponseDto;
import com.maxime.smul_yas.dto.crm_dto.RechargeCreditDto;
import com.maxime.smul_yas.dto.crm_dto.TmoneyAccountResponseDto;
import com.maxime.smul_yas.dto.crm_dto.CrmResponseDto;
import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.entity.TmoneyAccount;
import com.maxime.smul_yas.mapper.crm.CrmMapper;
import com.maxime.smul_yas.mapper.crm.TmoneyAccountMapper;
import com.maxime.smul_yas.repository.CrmRepository;
import com.maxime.smul_yas.repository.TmoneyAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmServiceTest {

    @Mock
    private CrmRepository crmRepository;

    @Mock
    private CrmMapper crmMapper;

    @Mock
    private TMoneyService tmoneyService;

    @Mock
    private TmoneyAccountRepository tmoneyAccountRepository;

    @Mock
    private TmoneyAccountMapper tmoneyAccountMapper;

    @InjectMocks
    private CrmService crmService;

    @Test
    void rechargerCredit_PourSoi_Success() {
        // Arrange
        String phone = "+22896118586";
        BigDecimal amount = new BigDecimal("5000.00");
        String password = "1234";

        // Recharge for oneself: beneficiaryPhone is empty
        RechargeCreditDto dto = new RechargeCreditDto(phone, "", amount, password);

        Crm crm = new Crm();
        crm.setPhone(phone);
        crm.setCreditBalance(new BigDecimal("1000.00"));

        Crm savedCrm = new Crm();
        savedCrm.setPhone(phone);
        savedCrm.setCreditBalance(new BigDecimal("6000.00"));

        CreditBalanceResponseDto expectedResponse = new CreditBalanceResponseDto(phone, new BigDecimal("6000.00"));

        when(crmRepository.findByPhone(phone)).thenReturn(Optional.of(crm));
        when(crmRepository.save(any(Crm.class))).thenReturn(savedCrm);
        when(crmMapper.toCreditBalanceResponseDto(savedCrm)).thenReturn(expectedResponse);

        // Act
        CreditBalanceResponseDto response = crmService.rechargerCredit(dto);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("6000.00"), response.getCreditBalance());
        verify(tmoneyService, times(1)).debitAccount(phone, amount, password);
        verify(crmRepository, times(1)).save(crm);
        assertEquals(new BigDecimal("6000.00"), crm.getCreditBalance());
    }

    @Test
    void rechargerCredit_PourAutrui_Success() {
        // Arrange
        String buyerPhone = "+22896118586";
        String beneficiaryPhone = "+22896699098";
        BigDecimal amount = new BigDecimal("2500.00");
        String password = "1234";

        // Recharge for others
        RechargeCreditDto dto = new RechargeCreditDto(buyerPhone, beneficiaryPhone, amount, password);

        Crm buyer = new Crm();
        buyer.setPhone(buyerPhone);
        buyer.setCreditBalance(new BigDecimal("5000.00"));

        Crm beneficiary = new Crm();
        beneficiary.setPhone(beneficiaryPhone);
        beneficiary.setCreditBalance(new BigDecimal("1000.00"));

        Crm savedBeneficiary = new Crm();
        savedBeneficiary.setPhone(beneficiaryPhone);
        savedBeneficiary.setCreditBalance(new BigDecimal("3500.00"));

        CreditBalanceResponseDto expectedResponse = new CreditBalanceResponseDto(beneficiaryPhone, new BigDecimal("3500.00"));

        when(crmRepository.findByPhone(buyerPhone)).thenReturn(Optional.of(buyer));
        when(crmRepository.findByPhone(beneficiaryPhone)).thenReturn(Optional.of(beneficiary));
        when(crmRepository.save(any(Crm.class))).thenReturn(savedBeneficiary);
        when(crmMapper.toCreditBalanceResponseDto(savedBeneficiary)).thenReturn(expectedResponse);

        // Act
        CreditBalanceResponseDto response = crmService.rechargerCredit(dto);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("3500.00"), response.getCreditBalance());
        assertEquals(beneficiaryPhone, response.getPhone());
        
        // Ensure T-Money debits the buyer, and credit is added to beneficiary
        verify(tmoneyService, times(1)).debitAccount(buyerPhone, amount, password);
        verify(crmRepository, times(1)).save(beneficiary);
        assertEquals(new BigDecimal("3500.00"), beneficiary.getCreditBalance());
    }

    @Test
    void rechargerCredit_WrongPassword_ThrowsException() {
        // Arrange
        String phone = "+22896118586";
        BigDecimal amount = new BigDecimal("5000.00");
        String password = "wrong_password";

        RechargeCreditDto dto = new RechargeCreditDto(phone, null, amount, password);

        Crm crm = new Crm();
        crm.setPhone(phone);
        crm.setCreditBalance(new BigDecimal("1000.00"));

        when(crmRepository.findByPhone(phone)).thenReturn(Optional.of(crm));
        doThrow(new IllegalArgumentException("Mot de passe T-Money incorrect. Échec du débit."))
                .when(tmoneyService).debitAccount(phone, amount, password);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crmService.rechargerCredit(dto);
        });

        assertEquals("Mot de passe T-Money incorrect. Échec du débit.", exception.getMessage());
        verify(tmoneyService, times(1)).debitAccount(phone, amount, password);
        verify(crmRepository, never()).save(any(Crm.class));
        assertEquals(new BigDecimal("1000.00"), crm.getCreditBalance());
    }

    @Test
    void rechargerCredit_AcheteurNotFound_ThrowsException() {
        // Arrange
        String buyerPhone = "+22896118586";
        String beneficiaryPhone = "+22896699098";
        BigDecimal amount = new BigDecimal("5000.00");
        String password = "1234";

        RechargeCreditDto dto = new RechargeCreditDto(buyerPhone, beneficiaryPhone, amount, password);

        when(crmRepository.findByPhone(buyerPhone)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crmService.rechargerCredit(dto);
        });

        assertTrue(exception.getMessage().contains("Acheteur non trouvé"));
        verify(tmoneyService, never()).debitAccount(anyString(), any(BigDecimal.class), anyString());
        verify(crmRepository, never()).save(any(Crm.class));
    }

    @Test
    void rechargerCredit_BeneficiaireNotFound_ThrowsException() {
        // Arrange
        String buyerPhone = "+22896118586";
        String beneficiaryPhone = "+22896699098";
        BigDecimal amount = new BigDecimal("5000.00");
        String password = "1234";

        RechargeCreditDto dto = new RechargeCreditDto(buyerPhone, beneficiaryPhone, amount, password);

        Crm buyer = new Crm();
        buyer.setPhone(buyerPhone);

        when(crmRepository.findByPhone(buyerPhone)).thenReturn(Optional.of(buyer));
        when(crmRepository.findByPhone(beneficiaryPhone)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            crmService.rechargerCredit(dto);
        });

        assertTrue(exception.getMessage().contains("Bénéficiaire non trouvé"));
        verify(tmoneyService, never()).debitAccount(anyString(), any(BigDecimal.class), anyString());
        verify(crmRepository, never()).save(any(Crm.class));
    }

    @Test
    void consulterSoldeTmoney_Success() {
        // Arrange
        String phone = "+22896118586";
        TmoneyAccount account = new TmoneyAccount();
        account.setPhone(phone);
        account.setBalance(new BigDecimal("10000.00"));

        TmoneyAccountResponseDto expectedResponse = new TmoneyAccountResponseDto(phone, new BigDecimal("10000.00"));

        when(tmoneyAccountRepository.findByPhone(phone)).thenReturn(Optional.of(account));
        when(tmoneyAccountMapper.toTmoneyAccountResponseDto(account)).thenReturn(expectedResponse);

        // Act
        TmoneyAccountResponseDto response = crmService.consulterSoldeTmoney(phone);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("10000.00"), response.getBalance());
        assertEquals(phone, response.getPhone());
        verify(tmoneyAccountRepository, times(1)).findByPhone(phone);
    }

    @Test
    void rechercherClientParTelephone_Success() {
        // Arrange
        String phone = "+22896118586";
        Crm crm = new Crm();
        crm.setId(1L);
        crm.setUser_id("USR-001");
        crm.setFirstName("Maxime");
        crm.setLastName("Togo");
        crm.setCity("Lomé");
        crm.setPhone(phone);
        crm.setCreditBalance(new BigDecimal("1000.00"));

        CrmResponseDto expectedResponse = CrmResponseDto.builder()
                .id(1L)
                .user_id("USR-001")
                .firstName("Maxime")
                .lastName("Togo")
                .city("Lomé")
                .phone(phone)
                .creditBalance(new BigDecimal("1000.00"))
                .build();

        when(crmRepository.findByPhone(phone)).thenReturn(Optional.of(crm));
        when(crmMapper.toCrmResponseDto(crm)).thenReturn(expectedResponse);

        // Act
        CrmResponseDto response = crmService.rechercherClientParTelephone(phone);

        // Assert
        assertNotNull(response);
        assertEquals("Maxime", response.getFirstName());
        assertEquals("Togo", response.getLastName());
        assertEquals(phone, response.getPhone());
        verify(crmRepository, times(1)).findByPhone(phone);
    }
}
