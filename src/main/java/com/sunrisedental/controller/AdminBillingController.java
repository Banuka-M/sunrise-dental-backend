package com.sunrisedental.controller;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.service.BillingService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/billing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminBillingController {

    private final BillingService billingService;

    public AdminBillingController(
            BillingService billingService) {

        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>>
    getAllBills(
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.getAllBills(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse>
    getBill(
            @PathVariable Long billId,
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.getStaffBill(
                        billId,
                        authentication.getName()
                )
        );
    }
}
