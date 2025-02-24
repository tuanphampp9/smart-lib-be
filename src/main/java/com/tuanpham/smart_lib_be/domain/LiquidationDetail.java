package com.tuanpham.smart_lib_be.domain;

import com.tuanpham.smart_lib_be.util.constant.LiquidationDetailStatus;
import com.tuanpham.smart_lib_be.util.constant.LiquidationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "liquidation_details")
@Getter
@Setter
public class LiquidationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //one liquidation detail belong to one liquidation
    @ManyToOne
    @JoinColumn(name = "liquidation_id")
    private Liquidation liquidation;

    //one liquidation detail belong to one registration_unique
    @ManyToOne
    @JoinColumn(name = "registration_unique_id")
    private RegistrationUnique registrationUnique;

    private String note;

    @Enumerated(EnumType.STRING)
    private LiquidationDetailStatus conditionStatus;

    private Double price;
}
