package com.sunrisedental.controller;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.dto.PaymentRequest;
import com.sunrisedental.dto.PaymentResponse;
import com.sunrisedental.service.BillingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/billing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientBillingController {

    private final BillingService billingService;

    public PatientBillingController(
            BillingService billingService) {

        this.billingService = billingService;
    }


    @GetMapping
    public ResponseEntity<List<BillResponse>>
    getMyBills(
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.getPatientBills(
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
                billingService.getPatientBill(
                        billId,
                        authentication.getName()
                )
        );
    }


    @PostMapping("/{billId}/payments")
    public ResponseEntity<PaymentResponse>
    makePayment(
            @PathVariable Long billId,
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.makePayment(
                        billId,
                        request,
                        authentication.getName()
                )
        );
    }



    @GetMapping("/{billId}/payments")
    public ResponseEntity<List<PaymentResponse>>
    getPayments(
            @PathVariable Long billId,
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.getPaymentsForBill(
                        billId,
                        authentication.getName()
                )
        );
    }
}
