package com.example.payment.auth.service;

import com.example.payment.auth.dto.AuthResponse;
import com.example.payment.auth.dto.RegisterRequest;
import com.example.payment.common.exception.BadRequestException;
import com.example.payment.common.security.JwtService;
import com.example.payment.user.entity.User;
import com.example.payment.user.repository.UserRepository;
import com.example.payment.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletService walletService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_WhenValidRequest_ShouldReturnAuthResponse() {
        RegisterRequest request = new RegisterRequest("Test", "test@test.com", "1234567890", "password");
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
        assertNotNull(response.getUserId());
        
        verify(walletService, times(1)).createWalletForUser(any(User.class));
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("Test", "test@test.com", "1234567890", "password");
        
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(walletService, never()).createWalletForUser(any(User.class));
    }
}
