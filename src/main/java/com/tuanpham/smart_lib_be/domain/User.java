package com.tuanpham.smart_lib_be.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.constant.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

//domain driven design
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank(message = "fullName doesn't empty")
    private String fullName;
    @NotBlank(message = "email doesn't empty")
    private String email;
    private String password;
    @NotBlank(message = "portraitImg doesn't empty")
    private String portraitImg;
    @NotBlank(message = "DOB doesn't empty")
    private String dob;
    @NotBlank(message = "phone doesn't empty")
    private String phone;
    @NotBlank(message = "phone doesn't empty")
    private String identityCardNumber;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @NotBlank(message = "phone doesn't empty")
    private String address;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;


    //one user have many inventory checks
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InventoryCheck> inventoryChecks;

    //one user have many liquidations
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Liquidation> liquidations;


    // many users belong to one role
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = true)
    private CardRead cardRead;

    // one user have many ratings
    @OneToMany(mappedBy = "user")
    private List<PublicationRating> publicationRatings;

    // one user have many cart users
    @OneToMany(mappedBy = "user")
    private List<CartUser> cartUsers;

    // one user have many post
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Post> posts;

    // one user have many publication requests
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<PublicationRequest> publicationRequests;

    // one user have many import receipts
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ImportReceipt> importReceipts;

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
