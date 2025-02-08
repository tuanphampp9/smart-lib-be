package com.tuanpham.smart_lib_be.domain.Response;

import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportReceiptDetailRes {
    private String id;
    private Double price;
    private int quantity;
    private PublicationRes publication;
    private List<RegistrationUnique> registrationUniques;
    @Getter
    @Setter
    public static class PublicationRes{
        private String name;
    }
}
