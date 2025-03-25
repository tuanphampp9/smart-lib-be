package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Publication;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
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
    @Query(value = "select COALESCE(SUM(quantity), 0) from import_receipt_details where publication_id= ?1", nativeQuery = true)
    long countPublicationImportReceiptDetails(Long publicationId);
    @Query(value =
            """
            SELECT COALESCE(count(*),0) FROM import_receipt_details inner join registration_unique
                                    on import_receipt_details.id = registration_unique.import_receipt_detail_id
                                    where registration_unique.import_receipt_detail_id = ?1
                                    and registration_unique.status= ?2
            """, nativeQuery = true)
    long countPublicationByStatus(Long publicationId, PublicationStatusEnum status);
}
