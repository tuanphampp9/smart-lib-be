package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long>, JpaSpecificationExecutor<InventoryCheck> {
}
