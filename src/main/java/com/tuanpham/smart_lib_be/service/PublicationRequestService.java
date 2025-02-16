package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Post;
import com.tuanpham.smart_lib_be.domain.PublicationRequest;
import com.tuanpham.smart_lib_be.domain.Request.PubReqRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.mapper.PublicationRequestMapper;
import com.tuanpham.smart_lib_be.repository.PublicationRequestRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.constant.PublicationRequestStatus;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationRequestService {
    private final PublicationRequestRepository publicationRequestRepository;
    private final PublicationRequestMapper publicationRequestMapper;
    private final UserRepository userRepository;
    
    public PublicationRequestService(PublicationRequestRepository publicationRequestRepository,
                                     PublicationRequestMapper publicationRequestMapper, UserRepository userRepository) {
        this.publicationRequestRepository = publicationRequestRepository;
        this.publicationRequestMapper = publicationRequestMapper;
        this.userRepository = userRepository;
    }
    
    public PublicationRequest handleCreatePublicationRequest(PublicationRequest publicationRequest) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get():"";
        User user = this.userRepository.findByEmail(email);
        publicationRequest.setUser(user);
        return this.publicationRequestRepository.save(publicationRequest);
    }
    
    public PublicationRequest handleUpdatePublicationRequest(PubReqRes pubReqRes, Long id) throws IdInvalidException {
        PublicationRequest publicationRequest = this.publicationRequestRepository.findById(id).orElse(null);
        if (publicationRequest == null) {
            throw new IdInvalidException("Yêu cầu ấn phẩm không tồn tại");
        }
        publicationRequest.setStatus(pubReqRes.getStatus());
        publicationRequest.setResponse(pubReqRes.getResponse());
        return this.publicationRequestRepository.save(publicationRequest);
    }
    
    public ResultPaginationDTO handleGetAllPublicationRequests(Specification<PublicationRequest> spec,
                                                               Pageable pageable) {
        Page<PublicationRequest> pagePublicationRequests = this.publicationRequestRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePublicationRequests.getSize());
        meta.setTotal(pagePublicationRequests.getTotalElements());// amount of elements
        meta.setPages(pagePublicationRequests.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<PublicationRequest> listPublicationRequest = pagePublicationRequests.getContent().stream().map(
                        p-> p)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPublicationRequest);
        return resultPaginationDTO;
    }

    public ResultPaginationDTO handleGetAllPublicationRequestsForUser(String id, Specification<PublicationRequest> spec,
                                                                      Pageable pageable) {
        Page<PublicationRequest> pagePublicationRequests = this.publicationRequestRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePublicationRequests.getSize());
        meta.setTotal(pagePublicationRequests.getTotalElements());// amount of elements
        meta.setPages(pagePublicationRequests.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<PublicationRequest> listPublicationRequest = pagePublicationRequests.getContent().stream().filter(
                p -> p.getUser().getId().equals(id)
                )
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPublicationRequest);
        return resultPaginationDTO;
    }

    public void handleDeletePublicationRequest(Long id) throws IdInvalidException {
        PublicationRequest publicationRequest = this.publicationRequestRepository.findById(id).orElse(null);
        if (publicationRequest == null) {
            throw new IdInvalidException("Yêu cầu ấn phẩm không tồn tại");
        }
        if(publicationRequest.getStatus().equals(PublicationRequestStatus.ACCEPTED)) {
            throw new IdInvalidException("Không thể xóa yêu cầu ấn phẩm đã được duyệt");
        }
        if(publicationRequest.getStatus().equals(PublicationRequestStatus.REJECTED)) {
            throw new IdInvalidException("Không thể xóa yêu cầu ấn phẩm đã bị từ chối");
        }
        if(publicationRequest.getStatus().equals(PublicationRequestStatus.ADDED)) {
            throw new IdInvalidException("Không thể xóa yêu cầu ấn phẩm đã được thêm vào hệ thống");
        }
        this.publicationRequestRepository.deleteById(id);
    }
}
