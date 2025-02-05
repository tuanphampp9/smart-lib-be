package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.constant.StatusBorrowSlipEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "borrow_slips")
@Getter
@Setter
public class BorrowSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant borrowDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant registerDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant expiredRegisterDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant returnDate;
    @Enumerated(EnumType.STRING)
    private StatusBorrowSlipEnum status;

    // one borrow slip has many borrow slip details
    @OneToMany(mappedBy = "borrowSlip", fetch = FetchType.LAZY)
    private List<BorrowSlipDetail> borrowSlipDetails;

    // one borrow slip belongs to one cardRead
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardId", referencedColumnName = "cardId")
    @JsonIgnoreProperties(value = { "borrowSlips","hibernateLazyInitializer", "handler" })
    private CardRead cardRead;

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
        this.registerDate = Instant.now();
        this.expiredRegisterDate = Instant.now().plus(2, java.time.temporal.ChronoUnit.DAYS);
        this.status = StatusBorrowSlipEnum.NOT_BORROWED;
    }

    @PreUpdate // action before update
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
