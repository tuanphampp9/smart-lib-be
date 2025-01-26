package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.ImportReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportReceiptRepository extends JpaRepository<ImportReceipt, Long>, JpaSpecificationExecutor<ImportReceipt> {
}
