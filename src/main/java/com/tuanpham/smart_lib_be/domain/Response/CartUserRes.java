package com.tuanpham.smart_lib_be.domain.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CartUserRes {
    private String id;
    private String userId;
    private Long publicationId;
    private int quantity;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private CartUserRes.Publication publication;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Publication {
        private String name;
        private String bannerImg;
    }
}
