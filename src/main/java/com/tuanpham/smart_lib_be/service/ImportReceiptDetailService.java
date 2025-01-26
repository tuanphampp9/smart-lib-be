package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.repository.ImportReceiptDetailRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportReceiptDetailService {
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final EntityManager entityManager;
    public ImportReceiptDetailService(ImportReceiptDetailRepository importReceiptDetailRepository, EntityManager entityManager) {
        this.importReceiptDetailRepository = importReceiptDetailRepository;
        this.entityManager = entityManager;
    }

    public void handleCreateImportReceiptDetail(ImportReceiptDetail importReceiptDetail) {
        this.importReceiptDetailRepository.save(importReceiptDetail);
    }

    @Transactional
    public void handleDeleteImportReceiptDetailByImportReceipt(ImportReceipt importReceipt) {
        this.importReceiptDetailRepository.deleteByImportReceipt(importReceipt);
    }
}
