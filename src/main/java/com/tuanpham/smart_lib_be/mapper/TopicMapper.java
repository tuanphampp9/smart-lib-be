package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Request.TopicReq;
import com.tuanpham.smart_lib_be.domain.Response.TopicRes;
import com.tuanpham.smart_lib_be.domain.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    void updateTopic(@MappingTarget Topic topic, TopicReq topicReq);
    TopicRes toTopicRes(Topic topic);
}
