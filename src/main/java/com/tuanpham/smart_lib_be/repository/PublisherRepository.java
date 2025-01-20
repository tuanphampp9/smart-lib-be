package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, String>, JpaSpecificationExecutor<Publisher> {
    boolean existsByName(String name);
    // Check if the name exists and the id is not the same
    boolean existsByNameAndIdNot(String name, String id);
}
