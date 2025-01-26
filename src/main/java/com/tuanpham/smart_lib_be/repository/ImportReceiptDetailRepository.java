package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportReceiptDetailRepository extends JpaRepository<ImportReceiptDetail, String> {
    void deleteByImportReceipt(ImportReceipt importReceipt);
}
