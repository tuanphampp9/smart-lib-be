package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Liquidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LiquidationRepository extends JpaRepository<Liquidation, Long>, JpaSpecificationExecutor<Liquidation> {
}
