package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.BorrowSlipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowSlipDetailRepository extends JpaRepository<BorrowSlipDetail, String>, JpaSpecificationExecutor<BorrowSlipDetail> {
}
