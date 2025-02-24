package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.InventoryCheckDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InventoryCheckDetailRepository extends JpaRepository<InventoryCheckDetail, Long>, JpaSpecificationExecutor<InventoryCheckDetail> {
    List<InventoryCheckDetail> findByInventoryCheck(InventoryCheck inventoryCheck);
}
