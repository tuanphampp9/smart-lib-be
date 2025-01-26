package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.repository.RegistrationUniqueRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationUniqueService {
    private final RegistrationUniqueRepository registrationUniqueRepository;
    private final EntityManager entityManager;

    public RegistrationUniqueService(RegistrationUniqueRepository registrationUniqueRepository, EntityManager entityManager) {
        this.registrationUniqueRepository = registrationUniqueRepository;
        this.entityManager = entityManager;
    }

    public String generateNextRegistrationId() {
        String sql = "SELECT COUNT(*) FROM registration_unique";
        Number count = (Number) this.entityManager.createNativeQuery(sql).getSingleResult();
        return String.format("PUB%09d", count.longValue() + 1);
    }
    public RegistrationUnique handleCreateRegistrationUnique(RegistrationUnique registrationUnique) {
        return this.registrationUniqueRepository.save(registrationUnique);
    }

    @Transactional
    public void handleDeleteRegistrationUniqueByImportReceiptDetail(ImportReceiptDetail importReceiptDetail) {
        this.registrationUniqueRepository.deleteByImportReceiptDetail(importReceiptDetail);
    }
}
