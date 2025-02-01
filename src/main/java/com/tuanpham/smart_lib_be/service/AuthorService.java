package com.tuanpham.smart_lib_be.service;
import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.Request.AuthorReq;
import com.tuanpham.smart_lib_be.domain.Response.AuthorRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Topic;
import com.tuanpham.smart_lib_be.mapper.AuthorMapper;
import com.tuanpham.smart_lib_be.repository.AuthorRepository;
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
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public Author handleCreateAuthor(Author author) {
        return this.authorRepository.save(author);
    }
    public AuthorRes handleGetAuthorById(String id) {
        int count = this.countAuthorPublication(id);
        AuthorRes authorRes = this.authorMapper.toAuthorRes(this.authorRepository.findById(id).orElse(null));
        authorRes.setNumberOfPublications(count);
        return authorRes;
    }
    public Author handleFindAuthorById(String id) {
        return this.authorRepository.findById(id).orElse(null);
    }

    public Author handleUpdateAuthor(AuthorReq authorReq, String id) throws IdInvalidException {
        Author authorExist = this.authorRepository.findById(id).orElse(null);
        if (authorExist == null) {
            throw new IdInvalidException("Tác giả không tồn tại");
        }
        this.authorMapper.updateAuthor(authorExist, authorReq);
        return this.authorRepository.save(authorExist);
    }

    public ResultPaginationDTO handleGetAllCategories(Specification<Author> spec,
                                                      Pageable pageable) {
        Page<Author> pageAuthors = this.authorRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageAuthors.getSize());
        meta.setTotal(pageAuthors.getTotalElements());// amount of elements
        meta.setPages(pageAuthors.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<AuthorRes> listAuthors = pageAuthors.getContent().stream().map(
                        c -> {
                            AuthorRes authorRes = this.authorMapper.toAuthorRes(c);
                            authorRes.setNumberOfPublications(this.countAuthorPublication(c.getId()));
                            return authorRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listAuthors);
        return resultPaginationDTO;
    }

    public void handleDeleteAuthor(String id) {
        this.authorRepository.deleteById(id);
    }

    public int countAuthorPublication(String authorId) {
        return this.authorRepository.countAuthorPublication(authorId);
    }

    public List<Author> getAuthorsFromExcel(InputStream inputStream) {
        List<Author> listAuthors = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("authors");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Author author = new Author();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            author.setFullName(cell.getStringCellValue());
                            break;
                        case 1:
                            author.setPenName(cell.getStringCellValue());
                            break;
                        case 2:
                            author.setHomeTown(cell.getStringCellValue());
                            break;
                        case 3:
                            author.setIntroduction(cell.getStringCellValue());
                            break;
                        case 4:
                            author.setAvatar(cell.getStringCellValue());
                            break;
                        case 5:
                            author.setDob(cell.getStringCellValue());
                            break;
                        case 6:
                            author.setDod(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listAuthors.add(author);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listAuthors;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Author> listAuthors = getAuthorsFromExcel(file.getInputStream());
                this.authorRepository.saveAll(listAuthors);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
