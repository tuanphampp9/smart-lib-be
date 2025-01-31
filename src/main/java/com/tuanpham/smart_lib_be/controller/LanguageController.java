package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Request.LanguageReq;
import com.tuanpham.smart_lib_be.domain.Response.LanguageRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.CategoryService;
import com.tuanpham.smart_lib_be.service.LanguageService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @PostMapping("/languages")
    public ResponseEntity<Language> create(@Valid @RequestBody Language language)
            throws IdInvalidException {
        boolean isExist = this.languageService.handleLanguageExist(language.getName());
        if (isExist) {
            throw new IdInvalidException("Ngôn ngữ đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.languageService.handleCreateLanguage(language));
    }

    @PutMapping("/languages/{id}")
    public ResponseEntity<Language> update(@Valid @RequestBody LanguageReq languageReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.languageService.handleUpdateLanguage(languageReq, id));
    }

    @GetMapping("/languages/{id}")
    public ResponseEntity<LanguageRes> getLanguageById(@PathVariable("id") String id)
            throws IdInvalidException {
        LanguageRes languageRes = this.languageService.handleGetLanguageResById(id);
        if (languageRes == null) {
            throw new IdInvalidException("Ngôn ngữ không tồn tại");
        }
        return ResponseEntity.ok().body(languageRes);
    }

    @GetMapping("/languages")
    public ResponseEntity<ResultPaginationDTO> getAllLanguages(
            @Filter Specification<Language> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.languageService.handleGetAlLanguages(spec, pageable));
    }

    @DeleteMapping("/languages/{id}")
    public ResponseEntity<String> deleteLanguage(@PathVariable("id") String id)
            throws IdInvalidException {
        Language language = this.languageService.handleFindLanguageById(id);
        if (language == null) {
            throw new IdInvalidException("Ngôn ngữ không tồn tại");
        }
        this.languageService.handleDeleteLanguage(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    // import data from excel
    @PostMapping("/languages/import-excel")
    public ResponseEntity<String> importDataFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        this.languageService.saveAllFromExcel(file);
        return ResponseEntity.ok().body("Import thành công");
    }
}
