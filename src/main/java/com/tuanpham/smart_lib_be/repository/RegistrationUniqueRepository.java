package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegistrationUniqueRepository extends JpaRepository<RegistrationUnique, Long>, JpaSpecificationExecutor<RegistrationUnique> {
    void deleteByImportReceiptDetail(ImportReceiptDetail importReceiptDetail);
    RegistrationUnique findByRegistrationId(String registrationId);
}
