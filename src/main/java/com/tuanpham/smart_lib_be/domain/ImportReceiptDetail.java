package com.tuanpham.smart_lib_be.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "import_receipt_details")
@Getter
@Setter
public class ImportReceiptDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Double price;
    private int quantity;

    // many import receipt details belong to one import receipt
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "import_receipt_id")
    @JsonIgnoreProperties(value = { "importReceiptDetails", "hibernateLazyInitializer", "handler" })
    private ImportReceipt importReceipt;

    // many import receipt details belong to one publication
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publication_id")
    @JsonIgnoreProperties(value = { "importReceiptDetails" })
    private Publication publication;


    // one import receipt detail have many registration unique codes
    @OneToMany(mappedBy = "importReceiptDetail", fetch = FetchType.LAZY)
    private List<RegistrationUnique> registrationUniques;
}
