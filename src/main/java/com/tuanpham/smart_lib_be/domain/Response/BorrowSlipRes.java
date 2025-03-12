package com.tuanpham.smart_lib_be.domain.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.domain.BorrowSlipDetail;
import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.PublicationRating;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.util.constant.StatusBorrowSlipEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class BorrowSlipRes {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant borrowDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant registerDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant expiredRegisterDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant returnDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant dueDate;
    @Enumerated(EnumType.STRING)
    private StatusBorrowSlipEnum status;
    private List<BorrowSlipDetailRes> borrowSlipDetails;
    @JsonIgnoreProperties(value = { "borrowSlips","hibernateLazyInitializer", "handler" })
    private CardRead cardRead;
    private String note;
    private int renewDueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BorrowSlipDetailRes{
        private String id;
        private RegistrationUnique registrationUnique;
        private String nameBook;
        private Long publicationId;
        private PublicationRating publicationRating;
    }

}
