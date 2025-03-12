package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.BorrowSlip;
import com.tuanpham.smart_lib_be.util.constant.StatusBorrowSlipEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BorrowSlipRepository extends JpaRepository<BorrowSlip, String>, JpaSpecificationExecutor<BorrowSlip> {
    List<BorrowSlip> findAllByStatus(StatusBorrowSlipEnum status);
}
