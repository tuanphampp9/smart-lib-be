package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.TopicReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Topic;
import com.tuanpham.smart_lib_be.mapper.TopicMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.TopicRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;


    public TopicService(TopicRepository topicRepository, TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }

    public boolean handleTopicExist(String name) {
        return this.topicRepository.existsByName(name);
    }

    public Topic handleCreateTopic(Topic topic) {
        return this.topicRepository.save(topic);
    }

    public Topic handleUpdateTopic(TopicReq req, String id) throws IdInvalidException {
        Topic topicExist = this.topicRepository.findById(id).orElse(null);
        if (topicExist == null) {
            throw new IdInvalidException("Chủ đề không tồn tại");
        }
        if(this.topicRepository.existsByNameAndIdNot(req.getName(), id)) {
            throw new IdInvalidException("Chủ đề đã tồn tại");
        }
        this.topicMapper.updateTopic(topicExist, req);
        return this.topicRepository.save(topicExist);
    }
    public Topic handleFindTopicById(String id) {
        return this.topicRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO handleGetAllTopics(Specification<Topic> spec,
                                                      Pageable pageable) {
        Page<Topic> pageTopics = this.topicRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageTopics.getSize());
        meta.setTotal(pageTopics.getTotalElements());// amount of elements
        meta.setPages(pageTopics.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Topic> listTopics = pageTopics.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listTopics);
        return resultPaginationDTO;
    }

    public void handleDeleteTopic(String id) {
        this.topicRepository.deleteById(id);
    }
}
