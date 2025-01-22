package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String>, JpaSpecificationExecutor<Author> {
}
