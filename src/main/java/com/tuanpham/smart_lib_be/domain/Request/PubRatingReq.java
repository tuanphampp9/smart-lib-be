package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubRatingReq {
    private String userId;
    private Long publicationId;
    private int rating;
}
