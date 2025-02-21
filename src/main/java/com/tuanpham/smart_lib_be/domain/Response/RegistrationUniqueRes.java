package com.tuanpham.smart_lib_be.domain.Response;

import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import com.tuanpham.smart_lib_be.util.constant.StatusBorrowSlipEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class RegistrationUniqueRes {
    private Long id;
    private String registrationId;
    private PublicationStatusEnum status;
    private String publicationName;
    private Instant createdAt;
    private List<HistoryBorrow> historyBorrows;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoryBorrow{
        private String borrowSlipId;
        private String cardId;
        private Instant borrowDate;
        private Instant returnDate;
        private String note;
        private StatusBorrowSlipEnum borrowSlipStatus;
    }
}
