package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.*;
import com.tuanpham.smart_lib_be.domain.Request.BorrowSlipAdminReq;
import com.tuanpham.smart_lib_be.domain.Request.BorrowSlipClientReq;
import com.tuanpham.smart_lib_be.domain.Request.ReturnBorrowSlipReq;
import com.tuanpham.smart_lib_be.domain.Response.AuthorRes;
import com.tuanpham.smart_lib_be.domain.Response.BorrowSlipRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.BorrowSlipMapper;
import com.tuanpham.smart_lib_be.repository.BorrowSlipDetailRepository;
import com.tuanpham.smart_lib_be.repository.BorrowSlipRepository;
import com.tuanpham.smart_lib_be.repository.CartUserRepository;
import com.tuanpham.smart_lib_be.repository.RegistrationUniqueRepository;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import com.tuanpham.smart_lib_be.util.constant.StatusBorrowSlipEnum;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowSlipService {

    private final BorrowSlipRepository borrowSlipRepository;
    private final CartUserRepository cartUserRepository;
    private final RegistrationUniqueRepository registrationUniqueRepository;
    private final CardReaderService cardReaderService;
    private final BorrowSlipDetailRepository borrowSlipDetailRepository;
    private final BorrowSlipMapper borrowSlipMapper;
    private final EmailService emailService;

    public BorrowSlipService(BorrowSlipRepository borrowSlipRepository, CartUserRepository cartUserRepository,
                             RegistrationUniqueRepository registrationUniqueRepository,
                             CardReaderService cardReaderService, BorrowSlipDetailRepository borrowSlipDetailRepository,
                             BorrowSlipMapper borrowSlipMapper, EmailService emailService) {
        this.borrowSlipRepository = borrowSlipRepository;
        this.cartUserRepository = cartUserRepository;
        this.registrationUniqueRepository = registrationUniqueRepository;
        this.cardReaderService = cardReaderService;
        this.borrowSlipDetailRepository = borrowSlipDetailRepository;
        this.borrowSlipMapper = borrowSlipMapper;
        this.emailService = emailService;
    }

    public BorrowSlip handleCreateBorrowSlipForClient(BorrowSlipClientReq borrowSlipClientReq) throws IdInvalidException {
        CardRead cardRead = this.cardReaderService.handleGetCardReader(borrowSlipClientReq.getCardId());
        if (cardRead == null) {
            throw new IdInvalidException("Thẻ đọc không tồn tại");
        }
        int sumQuantity = 0;
        for (String cartId : borrowSlipClientReq.getCartIds()) {
            CartUser cartUser = this.cartUserRepository.findById(cartId).orElse(null);
            sumQuantity += cartUser.getQuantity();
        }
        if (sumQuantity>3){
            throw new IdInvalidException("Số lượng ấn phẩm mượn không được vượt quá 3");
        }
        //create borrow slip
        BorrowSlip borrowSlip = new BorrowSlip();
        borrowSlip.setCardRead(cardRead);
        borrowSlip.setStatus(StatusBorrowSlipEnum.NOT_BORROWED);
        this.borrowSlipRepository.save(borrowSlip);
        List<BorrowSlipDetail> borrowSlipDetails = new ArrayList<>();

        for (String cartId : borrowSlipClientReq.getCartIds()) {
            CartUser cartUser = this.cartUserRepository.findById(cartId).orElse(null);
            if (cartUser == null) {
                continue;
            }
            // get list unique registration by publication id
            List<String> registrationIds = this.cartUserRepository.getRegistrationIdsByPublicationId(cartUser.getPublicationId());
            int i = 0;
            while (i < cartUser.getQuantity()) {
                // update status registration_unique
                RegistrationUnique registrationUnique = this.registrationUniqueRepository.findByRegistrationId(registrationIds.get(i));
                registrationUnique.setStatus(PublicationStatusEnum.BORROWED);
                this.registrationUniqueRepository.save(registrationUnique);
                // create borrow slip detail
                 BorrowSlipDetail borrowSlipDetail = new BorrowSlipDetail();
                borrowSlipDetail.setBorrowSlip(borrowSlip);
                borrowSlipDetail.setRegistrationUnique(registrationUnique);
                this.borrowSlipDetailRepository.save(borrowSlipDetail);
                borrowSlipDetails.add(borrowSlipDetail);
                i++;
            }
            //delete cart user
            this.cartUserRepository.deleteById(cartUser.getId());
        }
        borrowSlip.setBorrowSlipDetails(borrowSlipDetails);
        return borrowSlip;
    }

    public ResultPaginationDTO handleGetAllBorrowSlips(Specification<BorrowSlip> spec,
                                                      Pageable pageable) {
        Page<BorrowSlip> pageBorrowSlips = this.borrowSlipRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageBorrowSlips.getSize());
        meta.setTotal(pageBorrowSlips.getTotalElements());// amount of elements
        meta.setPages(pageBorrowSlips.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<BorrowSlipRes> listBorrowSlips = pageBorrowSlips.getContent().stream().map(
                        b -> {
                            BorrowSlipRes borrowSlipRes = this.borrowSlipMapper.toBorrowSlipRes(b);
                            List<BorrowSlipRes.BorrowSlipDetailRes> borrowSlipDetailResList = b.getBorrowSlipDetails().stream().map(
                                    bd -> {
                                        BorrowSlipRes.BorrowSlipDetailRes borrowSlipDetailRes = new BorrowSlipRes.BorrowSlipDetailRes();
                                        borrowSlipDetailRes.setId(bd.getId());
                                        borrowSlipDetailRes.setNameBook(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getName());
                                        borrowSlipDetailRes.setPublicationId(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getId());
                                        borrowSlipDetailRes.setRegistrationUnique(bd.getRegistrationUnique());
                                        borrowSlipDetailRes.setPublicationRating(bd.getPublicationRating());
                                        return borrowSlipDetailRes;
                                    }
                            ).collect(Collectors.toList());
                            borrowSlipRes.setBorrowSlipDetails(borrowSlipDetailResList);
                            return borrowSlipRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listBorrowSlips);
        return resultPaginationDTO;
    }

    public BorrowSlip handleAcceptBorrowSlip(String borrowSlipId) throws IdInvalidException {
        BorrowSlip borrowSlip = this.borrowSlipRepository.findById(borrowSlipId).orElse(null);
        if (borrowSlip == null) {
            throw new IdInvalidException("Phiếu mượn không tồn tại");
        }
        borrowSlip.setStatus(StatusBorrowSlipEnum.BORROWING);
        borrowSlip.setBorrowDate(Instant.now());
        borrowSlip.setDueDate(Instant.now().plus(15, java.time.temporal.ChronoUnit.DAYS));
        this.borrowSlipRepository.save(borrowSlip);
        return borrowSlip;
    }

    public void handleDeleteBorrowSlip(String borrowSlipId) throws IdInvalidException {
        BorrowSlip borrowSlip = this.borrowSlipRepository.findById(borrowSlipId).orElse(null);
        if (borrowSlip == null) {
            throw new IdInvalidException("Phiếu mượn không tồn tại");
        }
        List<BorrowSlipDetail> borrowSlipDetails = borrowSlip.getBorrowSlipDetails();
        for (BorrowSlipDetail borrowSlipDetail : borrowSlipDetails) {
            RegistrationUnique registrationUnique = borrowSlipDetail.getRegistrationUnique();
            registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
            this.registrationUniqueRepository.save(registrationUnique);
            this.borrowSlipDetailRepository.delete(borrowSlipDetail);
        }
        this.borrowSlipRepository.delete(borrowSlip);
    }

    public BorrowSlip handleCreateBorrowSlipByAdmin(BorrowSlipAdminReq borrowSlipAdminReq) throws IdInvalidException {
        CardRead cardRead = this.cardReaderService.handleGetCardReader(borrowSlipAdminReq.getCardId());
        if (cardRead == null) {
            throw new IdInvalidException("Thẻ đọc không tồn tại");
        }
        //create borrow slip
        BorrowSlip borrowSlip = new BorrowSlip();
        borrowSlip.setCardRead(cardRead);
        borrowSlip.setBorrowDate(Instant.now());
        borrowSlip.setDueDate(Instant.now().plus(15, java.time.temporal.ChronoUnit.DAYS));
        borrowSlip.setStatus(StatusBorrowSlipEnum.BORROWING);
        this.borrowSlipRepository.save(borrowSlip);
        List<BorrowSlipDetail> borrowSlipDetails = new ArrayList<>();
        for (String registrationId : borrowSlipAdminReq.getRegistrationIds()) {
            RegistrationUnique registrationUnique = this.registrationUniqueRepository.findByRegistrationId(registrationId);
            if (registrationUnique == null) {
                continue;
            }
            // update status registration_unique
            registrationUnique.setStatus(PublicationStatusEnum.BORROWED);
            this.registrationUniqueRepository.save(registrationUnique);
            // create borrow slip detail
            BorrowSlipDetail borrowSlipDetail = new BorrowSlipDetail();
            borrowSlipDetail.setBorrowSlip(borrowSlip);
            borrowSlipDetail.setRegistrationUnique(registrationUnique);
            this.borrowSlipDetailRepository.save(borrowSlipDetail);
            borrowSlipDetails.add(borrowSlipDetail);
        }
        borrowSlip.setBorrowSlipDetails(borrowSlipDetails);
        return borrowSlip;
    }

    public BorrowSlip handleReturnBorrowSlip(ReturnBorrowSlipReq returnBorrowSlipReq) throws IdInvalidException {
        BorrowSlip borrowSlip = this.borrowSlipRepository.findById(returnBorrowSlipReq.getBorrowSlipId()).orElse(null);
        if (borrowSlip == null) {
            throw new IdInvalidException("Phiếu mượn không tồn tại");
        }
        borrowSlip.setStatus(StatusBorrowSlipEnum.RETURNED);
        borrowSlip.setReturnDate(Instant.now());
        borrowSlip.setNote(returnBorrowSlipReq.getNote());
        //loop through list registration request
        for (ReturnBorrowSlipReq.RegistrationUniqueStatus registrationUniqueStatus : returnBorrowSlipReq.getRegistrationUniqueStatuses()) {
            //find registration unique by registration id into list borrow slip details
            RegistrationUnique registrationUnique = borrowSlip.getBorrowSlipDetails().stream()
                    .filter(bd -> bd.getRegistrationUnique().getRegistrationId().equals(registrationUniqueStatus.getRegistrationId()))
                    .findFirst()
                    .map(BorrowSlipDetail::getRegistrationUnique)
                    .orElse(null);
            if (registrationUnique == null) {
                continue;
            }
            registrationUnique.setStatus(registrationUniqueStatus.getStatus());
            this.registrationUniqueRepository.save(registrationUnique);
        }
        this.borrowSlipRepository.save(borrowSlip);
        //send email to user
        User newUser = borrowSlip.getCardRead().getUser();
        this.emailService.sendSimpleEmail(newUser.getEmail(), "Thông báo trả ấn phẩm thành công", "Cảm ơn bạn đã mượn ấn phẩm của thư viện, bạn có thể đánh giá ấn phẩm đã mượn trong vào 3 ngày tới!!! Chúng tôi rất vui khi nhận được các đánh giá thiết thực của bạn. Đây là link đánh giá: http://localhost:3000/my-account/borrowed-history/"+borrowSlip.getId());
        return borrowSlip;
    }

    public BorrowSlipRes handleGetBorrowSlipById(String borrowSlipId) throws IdInvalidException {
        BorrowSlip borrowSlip = this.borrowSlipRepository.findById(borrowSlipId).orElse(null);
        if (borrowSlip == null) {
            throw new IdInvalidException("Phiếu mượn không tồn tại");
        }
        BorrowSlipRes borrowSlipRes = this.borrowSlipMapper.toBorrowSlipRes(borrowSlip);
        List<BorrowSlipRes.BorrowSlipDetailRes> borrowSlipDetailResList = borrowSlip.getBorrowSlipDetails().stream().map(
                bd -> {
                    BorrowSlipRes.BorrowSlipDetailRes borrowSlipDetailRes = new BorrowSlipRes.BorrowSlipDetailRes();
                    borrowSlipDetailRes.setId(bd.getId());
                    borrowSlipDetailRes.setNameBook(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getName());
                    borrowSlipDetailRes.setPublicationId(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getId());
                    borrowSlipDetailRes.setRegistrationUnique(bd.getRegistrationUnique());
                    return borrowSlipDetailRes;
                }
        ).collect(Collectors.toList());
        borrowSlipRes.setBorrowSlipDetails(borrowSlipDetailResList);
        return borrowSlipRes;
    }

    public BorrowSlip handleRenewBorrowSlip(String borrowSlipId) throws IdInvalidException {
        BorrowSlip borrowSlip = this.borrowSlipRepository.findById(borrowSlipId).orElse(null);
        if (borrowSlip == null) {
            throw new IdInvalidException("Phiếu mượn không tồn tại");
        }
        borrowSlip.setDueDate(borrowSlip.getDueDate().plus(7, java.time.temporal.ChronoUnit.DAYS));
        borrowSlip.setRenewDueDate(borrowSlip.getRenewDueDate() + 1);
        this.borrowSlipRepository.save(borrowSlip);
        return borrowSlip;
    }

    //if overdue date, email user and update the status of borrow slip
    //run cron job every day (5:00 PM) to check due date
    @Scheduled(cron = "0 0 17 * * *")
    @Transactional
    public void handleCheckDueDate() {
        List<BorrowSlip> borrowSlips = this.borrowSlipRepository.findAllByStatus(StatusBorrowSlipEnum.BORROWING);
        for (BorrowSlip borrowSlip : borrowSlips) {
            if (Instant.now().isAfter(borrowSlip.getDueDate())) {
                borrowSlip.setStatus(StatusBorrowSlipEnum.OVERDUE);
                this.borrowSlipRepository.save(borrowSlip);
                //send email to user
                User newUser = borrowSlip.getCardRead().getUser();
                this.emailService.sendSimpleEmail(newUser.getEmail(), "Thông báo quá hạn trả ấn phẩm với mã phiếu mượn: "+borrowSlip.getId(), "Bạn đã quá hạn trả ấn phẩm, vui lòng tới thư viện để trả ấn phẩm ngay tại thư viện để tránh phạt tiền. Xin cảm ơn!!!");
            }
        }
    }

    //if expired register date, email user and delete borrow slip
    //run cron job every day (5:00 PM) to check expired register date
    @Scheduled(cron = "0 0 17 * * *")
    @Transactional
    public void handleCheckExpiredRegisterDate() {
        List<BorrowSlip> borrowSlips = this.borrowSlipRepository.findAllByStatus(StatusBorrowSlipEnum.NOT_BORROWED);
        for (BorrowSlip borrowSlip : borrowSlips) {
            if (Instant.now().isAfter(borrowSlip.getExpiredRegisterDate())) {
                //send email to user
                User newUser = borrowSlip.getCardRead().getUser();
                this.emailService.sendSimpleEmail(newUser.getEmail(), "Thông báo hết hạn đăng ký mượn ấn phẩm với mã phiếu mượn: "+borrowSlip.getId(), "Bạn đã hết hạn đăng ký mượn ấn phẩm, phiếu của bạn đã bị hủy. Vui lòng đăng ký mượn lại ấn phẩm khác. Xin cảm ơn!!!");
                //delete borrow slip
                List<BorrowSlipDetail> borrowSlipDetails = borrowSlip.getBorrowSlipDetails();
                for (BorrowSlipDetail borrowSlipDetail : borrowSlipDetails) {
                    RegistrationUnique registrationUnique = borrowSlipDetail.getRegistrationUnique();
                    registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
                    this.registrationUniqueRepository.save(registrationUnique);
                    this.borrowSlipDetailRepository.delete(borrowSlipDetail);
                }
                this.borrowSlipRepository.delete(borrowSlip);
            }
        }
    }
}
