package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.repository.ImportReceiptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportReceiptService {
    private final ImportReceiptRepository importReceiptRepository;

    public ImportReceiptService(ImportReceiptRepository importReceiptRepository) {
        this.importReceiptRepository = importReceiptRepository;
    }

    public ImportReceipt createImportReceipt(ImportReceipt importReceipt) {
        return this.importReceiptRepository.save(importReceipt);
    }

    public ResultPaginationDTO handleGetAllImportReceipts(Specification<ImportReceipt> spec,
                                                      Pageable pageable) {
        Page<ImportReceipt> pageImportReceipts = this.importReceiptRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageImportReceipts.getSize());
        meta.setTotal(pageImportReceipts.getTotalElements());// amount of elements
        meta.setPages(pageImportReceipts.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<ImportReceipt> listImportReceipts = pageImportReceipts.getContent().stream().map(
                        i -> i)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listImportReceipts);
        return resultPaginationDTO;
    }

    public ImportReceipt handleFindImportReceiptById(Long id) {
        return this.importReceiptRepository.findById(id).orElse(null);
    }
}
