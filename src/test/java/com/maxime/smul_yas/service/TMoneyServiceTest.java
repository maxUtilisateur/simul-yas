package com.maxime.smul_yas.service;

import com.maxime.smul_yas.entity.TmoneyAccount;
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
class TMoneyServiceTest {

    @Mock
    private TmoneyAccountRepository tmoneyAccountRepository;

    @InjectMocks
    private TMoneyService tMoneyService;

    @Test
    void debitAccount_Success() {
        // Arrange
        String phone = "+22896118586";
        BigDecimal amount = new BigDecimal("2000.00");
        String password = "1234";

        TmoneyAccount account = new TmoneyAccount();
        account.setPhone(phone);
        account.setBalance(new BigDecimal("10000.00"));
        account.setPassword(password);

        when(tmoneyAccountRepository.findByPhone(phone)).thenReturn(Optional.of(account));

        // Act
        tMoneyService.debitAccount(phone, amount, password);

        // Assert
        assertEquals(new BigDecimal("8000.00"), account.getBalance());
        verify(tmoneyAccountRepository, times(1)).save(account);
    }

    @Test
    void debitAccount_WrongPassword_ThrowsException() {
        // Arrange
        String phone = "+22896118586";
        BigDecimal amount = new BigDecimal("2000.00");
        String password = "wrong_password";

        TmoneyAccount account = new TmoneyAccount();
        account.setPhone(phone);
        account.setBalance(new BigDecimal("10000.00"));
        account.setPassword("1234");

        when(tmoneyAccountRepository.findByPhone(phone)).thenReturn(Optional.of(account));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tMoneyService.debitAccount(phone, amount, password);
        });

        assertEquals("Mot de passe T-Money incorrect. Échec du débit.", exception.getMessage());
        verify(tmoneyAccountRepository, never()).save(any(TmoneyAccount.class));
    }

    @Test
    void debitAccount_InsufficientBalance_ThrowsException() {
        // Arrange
        String phone = "+22896118586";
        BigDecimal amount = new BigDecimal("15000.00");
        String password = "1234";

        TmoneyAccount account = new TmoneyAccount();
        account.setPhone(phone);
        account.setBalance(new BigDecimal("10000.00"));
        account.setPassword(password);

        when(tmoneyAccountRepository.findByPhone(phone)).thenReturn(Optional.of(account));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tMoneyService.debitAccount(phone, amount, password);
        });

        assertEquals("Solde T-Money insuffisant pour effectuer le débit.", exception.getMessage());
        verify(tmoneyAccountRepository, never()).save(any(TmoneyAccount.class));
    }
}
