package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "registration_unique")
@Getter
@Setter
public class RegistrationUnique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String registrationId;

    @Enumerated(EnumType.STRING)
    private PublicationStatusEnum status;

    // many registration unique belong to one import receipt detail
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_receipt_detail_id")
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ImportReceiptDetail importReceiptDetail;

    // one registration unique belongs to many borrow slip details
    @OneToMany(mappedBy = "registrationUnique", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BorrowSlipDetail> borrowSlipDetails;

    //one registration unique belong to many inventory check details
    @OneToMany(mappedBy = "registrationUnique", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InventoryCheckDetail> inventoryCheckDetails;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist // action before save
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate // action before update
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
