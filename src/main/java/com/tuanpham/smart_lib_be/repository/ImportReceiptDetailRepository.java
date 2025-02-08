package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportReceiptDetailRepository extends JpaRepository<ImportReceiptDetail, String>, JpaSpecificationExecutor<ImportReceiptDetail> {
    void deleteByImportReceipt(ImportReceipt importReceipt);
}
