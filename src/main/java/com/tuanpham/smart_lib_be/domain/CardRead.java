package com.tuanpham.smart_lib_be.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

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
