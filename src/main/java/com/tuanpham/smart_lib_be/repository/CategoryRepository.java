package com.tuanpham.smart_lib_be.repository;


import com.tuanpham.smart_lib_be.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
    List<Category> findByIdIn(List<String> ids);
}
