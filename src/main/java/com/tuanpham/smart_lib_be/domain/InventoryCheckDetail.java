package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_check_details")
@Getter
@Setter
public class InventoryCheckDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //one inventory check detail belong to one inventory check
    @ManyToOne
    @JoinColumn(name = "inventory_check_id")
    @JsonIgnore
    private InventoryCheck inventoryCheck;

    //one inventory check detail belong to one registration unique
    @ManyToOne
    @JoinColumn(name = "registration_unique_id")
    private RegistrationUnique registrationUnique;

    //note about status registration_unique
    private String note;
}
