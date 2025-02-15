package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.util.constant.PostTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReq {
    private String title;
    private String content;
    private PostTypeEnum postType;
    private String bannerImg;
}
