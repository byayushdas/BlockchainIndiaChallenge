package com.example.payment.wallet.controller;

import com.example.payment.user.entity.User;
import com.example.payment.wallet.dto.WalletResponse;
import com.example.payment.wallet.entity.Wallet;
import com.example.payment.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal User user) {
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(mapToResponse(wallet));
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance(@AuthenticationPrincipal User user) {
        // Same as getMyWallet for now, just returning the whole wallet which includes balance
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(mapToResponse(wallet));
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .build();
    }
}
