package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Serve;
import com.tuanpham.smart_lib_be.repository.ServeRepository;
import org.springframework.stereotype.Service;

@Service
public class ServeService {
    private final ServeRepository serveRepository;

    public ServeService(ServeRepository serveRepository) {
        this.serveRepository = serveRepository;
    }

    public Serve handleCreateServe(Serve serve) {
        return this.serveRepository.save(serve);
    }

    public Serve handleGetServeByCardReadAndStatus(CardRead cardRead, String status) {
        return this.serveRepository.findByCardReadAndStatus(cardRead, status).orElse(null);
    }
}
