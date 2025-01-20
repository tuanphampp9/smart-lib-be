package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.TopicReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Topic;
import com.tuanpham.smart_lib_be.service.CategoryService;
import com.tuanpham.smart_lib_be.service.TopicService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/topics")
    public ResponseEntity<Topic> create(@Valid @RequestBody Topic topic)
            throws IdInvalidException {
        boolean isExist = this.topicService.handleTopicExist(topic.getName());
        if (isExist) {
            throw new IdInvalidException("Chủ đề đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.topicService.handleCreateTopic(topic));
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<Topic> update(@Valid @RequestBody TopicReq topicReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.topicService.handleUpdateTopic(topicReq, id));
    }

    @GetMapping("/topics/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable("id") String id)
            throws IdInvalidException {
        Topic topic = this.topicService.handleFindTopicById(id);
        if (topic == null) {
            throw new IdInvalidException("Chủ đề không tồn tại");
        }
        return ResponseEntity.ok().body(topic);
    }

    @GetMapping("/topics")
    public ResponseEntity<ResultPaginationDTO> getAllTopics(
            @Filter Specification<Topic> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.topicService.handleGetAllTopics(spec, pageable));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<String> deleteTopic(@PathVariable("id") String id)
            throws IdInvalidException {
        Topic topic = this.topicService.handleFindTopicById(id);
        if (topic == null) {
            throw new IdInvalidException("Chủ đề không tồn tại");
        }
        this.topicService.handleDeleteTopic(id);
        return ResponseEntity.ok().body("Xóa chủ đề thành công");
    }
}
