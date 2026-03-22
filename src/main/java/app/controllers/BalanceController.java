package app.controllers;

import app.dto.BalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.BalanceService;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {


    private final BalanceService balanceService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody BalanceRequest request) {
        try {
            balanceService.topUp(request.getUserDataId(), request.getAmount());
            return ResponseEntity.ok("Баланс успешно пополнен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/spend")
    public ResponseEntity<?> spend(@RequestBody BalanceRequest request) {
        try {
            balanceService.spend(request.getUserDataId(), request.getAmount());
            return ResponseEntity.ok("Средства успешно списаны");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}