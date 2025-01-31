package com.tuanpham.smart_lib_be.service;
import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Request.LanguageReq;
import com.tuanpham.smart_lib_be.domain.Response.LanguageRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Topic;
import com.tuanpham.smart_lib_be.mapper.LanguageMapper;
import com.tuanpham.smart_lib_be.repository.LanguageRepository;
import com.tuanpham.smart_lib_be.repository.PublicationRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final PublicationRepository publicationRepository;

    public LanguageService(LanguageRepository languageRepository, LanguageMapper languageMapper, PublicationRepository publicationRepository) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
        this.publicationRepository = publicationRepository;
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

    public LanguageRes handleGetLanguageResById(String id) {
        Language language = this.languageRepository.findById(id).orElse(null);
        if (language == null) {
            return null;
        }
        int numberOfPublications = this.publicationRepository.countPublicationLanguage(id);
        LanguageRes languageRes = this.languageMapper.toLanguageRes(language);
        languageRes.setNumberOfPublications(numberOfPublications);
        return languageRes;
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
        List<LanguageRes> listLanguages = pageLanguages.getContent().stream().map(
                        l -> {
                            LanguageRes languageRes = this.languageMapper.toLanguageRes(l);
                            int numberOfPublications = this.publicationRepository.countPublicationLanguage(l.getId());
                            languageRes.setNumberOfPublications(numberOfPublications);
                            return languageRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listLanguages);
        return resultPaginationDTO;
    }

    public void handleDeleteLanguage(String id) {
        this.languageRepository.deleteById(id);
    }

    public List<Language> getLanguagesFromExcel(InputStream inputStream) {
        List<Language> listLanguages = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("languages");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Language language = new Language();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            language.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            language.setDescription(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listLanguages.add(language);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listLanguages;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Language> listLanguages = getLanguagesFromExcel(file.getInputStream());
                this.languageRepository.saveAll(listLanguages);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
