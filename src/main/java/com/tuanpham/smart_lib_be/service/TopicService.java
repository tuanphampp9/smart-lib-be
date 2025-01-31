package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.TopicReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Response.TopicRes;
import com.tuanpham.smart_lib_be.domain.Topic;
import com.tuanpham.smart_lib_be.mapper.TopicMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.TopicRepository;
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
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;


    public TopicService(TopicRepository topicRepository, TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }

    public boolean handleTopicExist(String name) {
        return this.topicRepository.existsByName(name);
    }

    public Topic handleCreateTopic(Topic topic) {
        return this.topicRepository.save(topic);
    }

    public Topic handleUpdateTopic(TopicReq req, String id) throws IdInvalidException {
        Topic topicExist = this.topicRepository.findById(id).orElse(null);
        if (topicExist == null) {
            throw new IdInvalidException("Chủ đề không tồn tại");
        }
        if(this.topicRepository.existsByNameAndIdNot(req.getName(), id)) {
            throw new IdInvalidException("Chủ đề đã tồn tại");
        }
        this.topicMapper.updateTopic(topicExist, req);
        return this.topicRepository.save(topicExist);
    }
    public Topic handleFindTopicById(String id) {
        return this.topicRepository.findById(id).orElse(null);
    }

    public TopicRes handleGetTopicRes(String topicId) {
        Topic topicExist = this.topicRepository.findById(topicId).orElse(null);
        if (topicExist == null) {
            return null;
        }
        TopicRes topicRes = this.topicMapper.toTopicRes(topicExist);
        int count = this.topicRepository.countTopicPublication(topicId);
        topicRes.setNumberOfPublications(count);
        return topicRes;
    }

    public ResultPaginationDTO handleGetAllTopics(Specification<Topic> spec,
                                                      Pageable pageable) {
        Page<Topic> pageTopics = this.topicRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageTopics.getSize());
        meta.setTotal(pageTopics.getTotalElements());// amount of elements
        meta.setPages(pageTopics.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<TopicRes> listTopics = pageTopics.getContent().stream().map(
                        t -> {
                            TopicRes topicRes = this.topicMapper.toTopicRes(t);
                            int count = this.topicRepository.countTopicPublication(t.getId());
                            topicRes.setNumberOfPublications(count);
                            return topicRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listTopics);
        return resultPaginationDTO;
    }

    public void handleDeleteTopic(String id) {
        this.topicRepository.deleteById(id);
    }

    public List<Topic> getTopicsFromExcel(InputStream inputStream) {
        List<Topic> listTopics = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("topics");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Topic topic = new Topic();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            topic.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            topic.setDescription(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listTopics.add(topic);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listTopics;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Topic> listTopics = getTopicsFromExcel(file.getInputStream());
                this.topicRepository.saveAll(listTopics);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
