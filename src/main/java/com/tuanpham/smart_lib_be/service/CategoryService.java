package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Response.CategoryRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.CategoryMapper;
import com.tuanpham.smart_lib_be.mapper.TopicMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
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
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public boolean handleCategoryExist(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public Category handleCreateCategory(Category category) {
        return this.categoryRepository.save(category);
    }
    public Category handleFindCategoryById(String id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    public CategoryRes handleGetCategoryById(String id) {
       CategoryRes categoryRes = this.categoryMapper.toCategoryRes(this.categoryRepository.findById(id).orElse(null));
       int count = this.categoryRepository.countCategoryPublication(id);
       categoryRes.setNumberOfPublications(count);
       return categoryRes;
    }

    public Category handleUpdateCategory(CategoryReq categoryReq, String id) throws IdInvalidException {
        Category categoryExist = this.categoryRepository.findById(id).orElse(null);
        if (categoryExist == null) {
            throw new IdInvalidException("Thể loại không tồn tại");
        }
        if(this.categoryRepository.existsByNameAndIdNot(categoryReq.getName(), id)) {
            throw new IdInvalidException("Thể loại đã tồn tại");
        }
        this.categoryMapper.updateCategory(categoryExist, categoryReq);
        return this.categoryRepository.save(categoryExist);
    }

    public ResultPaginationDTO handleGetAllCategories(Specification<Category> spec,
                                                       Pageable pageable) {
        Page<Category> pageCategories = this.categoryRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageCategories.getSize());
        meta.setTotal(pageCategories.getTotalElements());// amount of elements
        meta.setPages(pageCategories.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<CategoryRes> listCategories = pageCategories.getContent().stream().map(
                        c -> {
                            CategoryRes categoryRes = this.categoryMapper.toCategoryRes(c);
                            int count = this.categoryRepository.countCategoryPublication(c.getId());
                            categoryRes.setNumberOfPublications(count);
                            return categoryRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listCategories);
        return resultPaginationDTO;
    }

    public void handleDeleteCategory(String id) {
        this.categoryRepository.deleteById(id);
    }

    public List<Category> getCategoriesFromExcel(InputStream inputStream) {
        List<Category> listCategories = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("categories");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Category category = new Category();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            category.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            category.setDescription(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listCategories.add(category);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listCategories;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Category> listCategories = getCategoriesFromExcel(file.getInputStream());
                this.categoryRepository.saveAll(listCategories);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
