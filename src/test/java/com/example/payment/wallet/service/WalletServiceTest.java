package com.example.payment.wallet.service;

import com.example.payment.common.exception.ResourceNotFoundException;
import com.example.payment.user.entity.User;
import com.example.payment.wallet.entity.Wallet;
import com.example.payment.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void createWalletForUser_ShouldSaveAndReturnWallet() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Wallet savedWallet = new Wallet();
        savedWallet.setId(UUID.randomUUID());
        savedWallet.setUser(user);
        savedWallet.setCurrency("INR");
        savedWallet.setBalance(BigDecimal.ZERO);

        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        Wallet result = walletService.createWalletForUser(user);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertEquals("INR", result.getCurrency());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void getWalletByUserId_WhenExists_ShouldReturnWallet() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByUserId(userId);

        assertNotNull(result);
        assertEquals(wallet.getId(), result.getId());
    }

    @Test
    void getWalletByUserId_WhenNotExists_ShouldThrowException() {
        UUID userId = UUID.randomUUID();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> walletService.getWalletByUserId(userId));
    }
}
