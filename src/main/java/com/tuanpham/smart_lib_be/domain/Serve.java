package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "serves")
@Getter
@Setter
public class Serve {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;

    // many serves belong to one cardRead
    @ManyToOne
    @JoinColumn(name = "card_read_id", referencedColumnName = "cardId")
    @JsonIgnore
    private CardRead cardRead;

    @PrePersist // action before save
    public void handleCreateCheckInTime() {
        this.checkInTime = LocalDateTime.now();
        this.status = "CHECKED_IN";
    }
}
