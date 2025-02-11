package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "borrow_slip_details")
@Getter
@Setter
public class BorrowSlipDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    // one borrow slip detail belongs to one borrow slip
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrowSlipId")
    @JsonIgnore
    private BorrowSlip borrowSlip;

    // one borrow slip detail belongs to one registration
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registrationId", referencedColumnName = "registrationId")
    private RegistrationUnique registrationUnique;

    // one borrow slip detail has one publication rating
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publicationRatingId", referencedColumnName = "id")
    private PublicationRating publicationRating;
}
