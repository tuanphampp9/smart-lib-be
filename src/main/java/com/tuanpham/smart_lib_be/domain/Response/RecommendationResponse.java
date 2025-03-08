package com.tuanpham.smart_lib_be.domain.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class RecommendationResponse {
    private Map<String, List<Long>> recommendations;
}
