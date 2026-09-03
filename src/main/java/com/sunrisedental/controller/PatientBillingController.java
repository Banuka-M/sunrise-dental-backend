package com.sunrisedental.controller;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.dto.PaymentRequest;
import com.sunrisedental.dto.PaymentResponse;
import com.sunrisedental.service.BillPdfService;
import com.sunrisedental.service.BillingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/billing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientBillingController {

    private final BillingService billingService;
    private final BillPdfService billPdfService;

    public PatientBillingController(
            BillingService billingService,
            BillPdfService billPdfService) {

        this.billingService = billingService;
        this.billPdfService = billPdfService;
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

    @GetMapping("/{billId}/download")
    public ResponseEntity<byte[]> downloadBill(
            @PathVariable Long billId,
            Authentication authentication) {

        BillResponse bill = billingService.getPatientBill(
                billId,
                authentication.getName()
        );

        List<PaymentResponse> payments = billingService.getPaymentsForBill(
                billId,
                authentication.getName()
        );

        byte[] pdfBytes = billPdfService.generateBillPdf(bill, payments);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"bill-" + bill.getBillNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}