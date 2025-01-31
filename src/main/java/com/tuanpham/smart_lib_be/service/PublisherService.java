package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.PublisherReq;
import com.tuanpham.smart_lib_be.domain.Response.PublisherRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.PublisherMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.PublicationRepository;
import com.tuanpham.smart_lib_be.repository.PublisherRepository;
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
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final PublicationRepository publicationRepository;
    
    public PublisherService(PublisherRepository publisherRepository, PublisherMapper publisherMapper, PublicationRepository publicationRepository) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
        this.publicationRepository = publicationRepository;
    }

    public boolean handlePublisherExist(String name) {
        return this.publisherRepository.existsByName(name);
    }

    public Publisher handleCreatePublisher(Publisher publisher) {
        return this.publisherRepository.save(publisher);
    }

    public Publisher handleUpdatePublisher(PublisherReq publisherReq, String id) throws IdInvalidException {
        Publisher publisher = this.publisherRepository.findById(id).orElse(null);
        if(publisher == null) {
            throw new IdInvalidException("Nhà xuất bản không tồn tại");
        }
        if(this.publisherRepository.existsByNameAndIdNot(publisherReq.getName(), id)) {
            throw new IdInvalidException("Nhà xuất bản đã tồn tại");
        }
        this.publisherMapper.updatePublisher(publisher, publisherReq);
        return this.publisherRepository.save(publisher);
    }
    public Publisher handleFindPublisherById(String id) {
        return this.publisherRepository.findById(id).orElse(null);
    }
    public PublisherRes handleGetPublisherResById(String id) {
        Publisher publisher = this.publisherRepository.findById(id).orElse(null);
        if (publisher == null) {
            return null;
        }

        PublisherRes publisherRes = this.publisherMapper.toPublisherRes(publisher);
        int numberOfPublications = this.publicationRepository.countPublicationPublisher(id);
        publisherRes.setNumberOfPublications(numberOfPublications);
        return publisherRes;
    }

    public ResultPaginationDTO handleGetAllPublishers(Specification<Publisher> spec,
                                                      Pageable pageable) {
        Page<Publisher> pagePublishers = this.publisherRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePublishers.getSize());
        meta.setTotal(pagePublishers.getTotalElements());// amount of elements
        meta.setPages(pagePublishers.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<PublisherRes> listPublishers = pagePublishers.getContent().stream().map(
                        p -> {
                            PublisherRes publisherRes = this.publisherMapper.toPublisherRes(p);
                            int numberOfPublications = this.publicationRepository.countPublicationPublisher(p.getId());
                            publisherRes.setNumberOfPublications(numberOfPublications);
                            return publisherRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPublishers);
        return resultPaginationDTO;
    }

    public void handleDeletePublisher(String id) {
        this.publisherRepository.deleteById(id);
    }

    public List<Publisher> getPublishersFromExcel(InputStream inputStream) {
        List<Publisher> listPublishers = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("publishers");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Publisher publisher = new Publisher();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            publisher.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            publisher.setDescription(cell.getStringCellValue());
                            break;
                        case 2:
                            publisher.setAddress(cell.getStringCellValue());
                            break;
                        case 3:
                            publisher.setPhone(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listPublishers.add(publisher);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listPublishers;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Publisher> listPublishers = getPublishersFromExcel(file.getInputStream());
                this.publisherRepository.saveAll(listPublishers);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
