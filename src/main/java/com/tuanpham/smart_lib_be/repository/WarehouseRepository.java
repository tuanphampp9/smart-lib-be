package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>, JpaSpecificationExecutor<Warehouse> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
}
