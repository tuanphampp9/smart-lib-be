package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.LiquidationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LiquidationDetailRepository extends JpaRepository<LiquidationDetail, Long>, JpaSpecificationExecutor<LiquidationDetail> {
    List<LiquidationDetail> findByLiquidation(Liquidation liquidation);
}
