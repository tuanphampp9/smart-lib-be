package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InventoryCheckDetailCreate {
    private List<RegistrationUnique> registrationUniques;
    private String note;
}
