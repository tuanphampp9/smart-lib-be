package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.PublisherReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.PublisherMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.PublisherRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    
    public PublisherService(PublisherRepository publisherRepository, PublisherMapper publisherMapper) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    public boolean handlePublisherExist(String name) {
        return this.publisherRepository.existsByName(name);
    }

    public Publisher handleCreatePublisher(Publisher publisher) {
        return this.publisherRepository.save(publisher);
    }

    public Publisher handleUpdatePublisher(PublisherReq publisherReq, String id) throws IdInvalidException {
        Publisher publisher = this.publisherRepository.findById(id).orElse(null);
        if(publisher == null) {
            throw new IdInvalidException("Nhà xuất bản không tồn tại");
        }
        if(this.publisherRepository.existsByNameAndIdNot(publisherReq.getName(), id)) {
            throw new IdInvalidException("Nhà xuất bản đã tồn tại");
        }
        this.publisherMapper.updatePublisher(publisher, publisherReq);
        return this.publisherRepository.save(publisher);
    }
    public Publisher handleFindPublisherById(String id) {
        return this.publisherRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO handleGetAllPublishers(Specification<Publisher> spec,
                                                      Pageable pageable) {
        Page<Publisher> pagePublishers = this.publisherRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePublishers.getSize());
        meta.setTotal(pagePublishers.getTotalElements());// amount of elements
        meta.setPages(pagePublishers.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Publisher> listPublishers = pagePublishers.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPublishers);
        return resultPaginationDTO;
    }

    public void handleDeletePublisher(String id) {
        this.publisherRepository.deleteById(id);
    }
}
