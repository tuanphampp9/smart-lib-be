package com.tuanpham.smart_lib_be.domain.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WarehousePub {
    private String id;
    private String name;
    private List<Publication> publications;
    @Getter
    @Setter
    public static class Publication {
        private long id;
        private String publicationName;
        private long quantity;
        private long availableQuantity;
        private long borrowedQuantity;
        private long lostQuantity;
    }
}
