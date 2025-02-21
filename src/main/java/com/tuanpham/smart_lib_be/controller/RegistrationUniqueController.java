package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.RegistrationUniqueService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RegistrationUniqueController {
    private final RegistrationUniqueService registrationUniqueService;

    public RegistrationUniqueController(RegistrationUniqueService registrationUniqueService) {
        this.registrationUniqueService = registrationUniqueService;
    }

    //get all registration unique
    @GetMapping("/registration-uniques")
    public ResponseEntity<ResultPaginationDTO> getAllRegistrationUniques(
            @Filter Specification<RegistrationUnique> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.registrationUniqueService.handleGetAllRegistrationUniques(spec, pageable));
    }
}
