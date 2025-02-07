package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReturnBorrowSlipReq {
    private String borrowSlipId;
    private List<RegistrationUniqueStatus> registrationUniqueStatuses;
    private String note;

    @Getter
    @Setter
    public static class RegistrationUniqueStatus{
        private String registrationId;
        private PublicationStatusEnum status;
    }
}
