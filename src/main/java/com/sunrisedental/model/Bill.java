package com.sunrisedental.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bill_treatment_record",
                        columnNames = "record_id"
                )
        }
)
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @Column(
            nullable = false,
            unique = true
    )
    private String billNumber;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal treatmentAmount;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal consultationAmount;


    /*
     * Treatment record for which this bill was generated.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "record_id",
            nullable = false,
            unique = true
    )
    private TreatmentRecord treatmentRecord;

    /*
     * Patient who must pay.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "patient_id",
            nullable = false
    )
    private Patient patient;

    /*
     * Original treatment fee.
     */
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalAmount;

    /*
     * Amount already paid.
     */
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Bill() {
    }

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (billNumber == null) {

            billNumber =
                    "BILL-" +
                            UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8)
                                    .toUpperCase();
        }

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        if (status == null) {
            status = BillStatus.UNPAID;
        }
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public TreatmentRecord getTreatmentRecord() {
        return treatmentRecord;
    }

    public void setTreatmentRecord(
            TreatmentRecord treatmentRecord) {

        this.treatmentRecord = treatmentRecord;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public BigDecimal getTreatmentAmount() {
        return treatmentAmount;
    }

    public void setTreatmentAmount(BigDecimal treatmentAmount) {
        this.treatmentAmount = treatmentAmount;
    }

    public BigDecimal getConsultationAmount() {
        return consultationAmount;
    }

    public void setConsultationAmount(BigDecimal consultationAmount) {
        this.consultationAmount = consultationAmount;
    }
}
