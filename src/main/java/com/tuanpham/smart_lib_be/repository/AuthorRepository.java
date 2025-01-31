package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String>, JpaSpecificationExecutor<Author> {
    List<Author> findByIdIn(List<String> ids);
    @Query(value = "select count(author_id) from author_publication where author_id= ?1", nativeQuery = true)
    int countAuthorPublication(String authorId);
}
