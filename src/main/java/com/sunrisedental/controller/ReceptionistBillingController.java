package com.sunrisedental.controller;

import com.sunrisedental.dto.BillRequest;
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
@RequestMapping("/api/receptionist/billing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReceptionistBillingController {

    private final BillingService billingService;

    public ReceptionistBillingController(
            BillingService billingService) {

        this.billingService = billingService;
    }


    @PostMapping
    public ResponseEntity<BillResponse>
    createBill(
            @Valid @RequestBody BillRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                billingService.createBill(
                        request,
                        authentication.getName()
                )
        );
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
