package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long>, JpaSpecificationExecutor<Publication> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    @Query(value = "select count(id) from publications where language_id= ?1", nativeQuery = true)
    int countPublicationLanguage(String languageId);
    @Query(value = "select count(id) from publications where warehouse_id= ?1", nativeQuery = true)
    int countPublicationWarehouse(String warehouseId);
    @Query(value = "select count(id) from publications where publisher_id= ?1", nativeQuery = true)
    int countPublicationPublisher(String publisherId);
}
