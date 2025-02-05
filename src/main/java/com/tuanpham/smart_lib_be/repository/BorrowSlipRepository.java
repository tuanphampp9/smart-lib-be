package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.BorrowSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowSlipRepository extends JpaRepository<BorrowSlip, String>, JpaSpecificationExecutor<BorrowSlip> {
}
