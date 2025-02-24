package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.constant.InventoryCheckStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "inventory_checks")
@Getter
@Setter
public class InventoryCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //one inventory check belong to one warehouse
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    //one inventory check belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //one inventory check has many inventory check details
    @OneToMany(mappedBy = "inventoryCheck")
    private List<InventoryCheckDetail> inventoryCheckDetails;

    private String note;

    @Enumerated(EnumType.STRING)
    private InventoryCheckStatus status;

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
