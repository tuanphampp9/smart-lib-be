package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Request.LanguageReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.LanguageMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.LanguageRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    public LanguageService(LanguageRepository languageRepository, LanguageMapper languageMapper) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    public boolean handleLanguageExist(String name) {
        return this.languageRepository.existsByName(name);
    }

    public Language handleCreateLanguage(Language language) {
        return this.languageRepository.save(language);
    }
    public Language handleUpdateLanguage(LanguageReq languageReq, String id) throws IdInvalidException {
        Language languageExist = this.languageRepository.findById(id).orElse(null);
        if (languageExist == null) {
            throw new IdInvalidException("Ngôn ngữ không tồn tại");
        }
        if(this.languageRepository.existsByNameAndIdNot(languageReq.getName(), id)) {
            throw new IdInvalidException("Ngôn ngữ đã tồn tại");
        }
        this.languageMapper.updateLanguage(languageExist, languageReq);
        return this.languageRepository.save(languageExist);
    }
    public Language handleFindLanguageById(String id) {
        return this.languageRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO handleGetAlLanguages(Specification<Language> spec,
                                                      Pageable pageable) {
        Page<Language> pageLanguages = this.languageRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageLanguages.getSize());
        meta.setTotal(pageLanguages.getTotalElements());// amount of elements
        meta.setPages(pageLanguages.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Language> listLanguages = pageLanguages.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listLanguages);
        return resultPaginationDTO;
    }

    public void handleDeleteLanguage(String id) {
        this.languageRepository.deleteById(id);
    }
}
