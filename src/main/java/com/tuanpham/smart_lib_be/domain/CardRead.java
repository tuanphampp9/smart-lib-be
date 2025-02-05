package com.tuanpham.smart_lib_be.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "card_reads")
@Getter
@Setter
public class CardRead {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String cardId;
    private LocalDateTime activeAt;
    private LocalDateTime expiredAt;
    private boolean isLocked;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonIgnore
    private User user;

    // one cardRead has many serve
    @OneToMany(mappedBy = "cardRead", fetch = FetchType.LAZY)
    List<Serve> serves;

    // one cardRead has many borrowSlip
    @OneToMany(mappedBy = "cardRead", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cardRead","hibernateLazyInitializer", "handler" })
    List<BorrowSlip> borrowSlips;

    @PrePersist
    private void prePersist() {
        if (this.activeAt == null) {
            this.activeAt = LocalDateTime.now();
        }
        if (this.expiredAt == null) {
            this.expiredAt = this.activeAt.plusYears(1);
        }
    }
}
