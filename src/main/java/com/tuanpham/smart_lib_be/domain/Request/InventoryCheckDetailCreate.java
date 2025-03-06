package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InventoryCheckDetailCreate {
    private String registrationId;
    private String note;
    private PublicationStatusEnum status;
}
