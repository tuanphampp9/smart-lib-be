package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.domain.Response.ImportReceiptDetailRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.ImportReceiptMapper;
import com.tuanpham.smart_lib_be.repository.ImportReceiptDetailRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportReceiptDetailService {
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final EntityManager entityManager;
    private final ImportReceiptMapper importReceiptMapper;
    public ImportReceiptDetailService(ImportReceiptDetailRepository importReceiptDetailRepository,
                                      EntityManager entityManager, ImportReceiptMapper importReceiptMapper) {
        this.importReceiptDetailRepository = importReceiptDetailRepository;
        this.entityManager = entityManager;
        this.importReceiptMapper = importReceiptMapper;
    }

    public void handleCreateImportReceiptDetail(ImportReceiptDetail importReceiptDetail) {
        this.importReceiptDetailRepository.save(importReceiptDetail);
    }

    @Transactional
    public void handleDeleteImportReceiptDetailByImportReceipt(ImportReceipt importReceipt) {
        this.importReceiptDetailRepository.deleteByImportReceipt(importReceipt);
    }

    public ResultPaginationDTO handleGetAllImportReceiptDetails(Specification<ImportReceiptDetail> spec,
                                                          Pageable pageable) {
        Page<ImportReceiptDetail> pageImportReceiptDetails = this.importReceiptDetailRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageImportReceiptDetails.getSize());
        meta.setTotal(pageImportReceiptDetails.getTotalElements());// amount of elements
        meta.setPages(pageImportReceiptDetails.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<ImportReceiptDetailRes> listImportReceiptDetails = pageImportReceiptDetails.getContent().stream().map(
                        i -> {
                            ImportReceiptDetailRes importReceiptDetailRes = this.importReceiptMapper.toImportReceiptDetailRes(i);
                            return importReceiptDetailRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listImportReceiptDetails);
        return resultPaginationDTO;
    }
}
