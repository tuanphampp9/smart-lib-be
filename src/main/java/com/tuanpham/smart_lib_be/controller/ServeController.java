package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Request.ReqCheckInCheckOut;
import com.tuanpham.smart_lib_be.domain.Response.ResCreateUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.RestResponse;
import com.tuanpham.smart_lib_be.domain.Serve;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.service.CardReaderService;
import com.tuanpham.smart_lib_be.service.ServeService;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.annotation.ApiMessage;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class ServeController {
    private final ServeService serveService;
    private final CardReaderService cardReaderService;

    public ServeController(ServeService serveService, CardReaderService cardReaderService) {
        this.serveService = serveService;
        this.cardReaderService = cardReaderService;
    }

    @PostMapping("/serves/check-in-check-out")
    @ApiMessage("Check-in or check-out")
    public ResponseEntity<RestResponse<String>> checkInCheckOut(@Valid @RequestBody ReqCheckInCheckOut req)
            throws IdInvalidException {
        CardRead cardRead = this.cardReaderService.handleGetCardReader(req.getCardId());
        if (cardRead == null) {
            throw new IdInvalidException("Thẻ không hợp lệ");
        }
        String message = "";
        RestResponse restResponse = new RestResponse();
        //find cardRead has cardId and status CHECK_IN into table serves
        Serve serveFound = this.serveService.handleGetServeByCardReadAndStatus(cardRead, "CHECKED_IN");
        if (serveFound != null) {
            //check-out
            message = "Check-out thành công";
            restResponse.setMessage(message);
            serveFound.setStatus("CHECK_OUT");
            serveFound.setCheckOutTime(LocalDateTime.now());
            this.serveService.handleCreateServe(serveFound);
            return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }
        //check-in card
        message = "Check-in thành công";
        restResponse.setMessage(message);
        Serve serve = new Serve();
        serve.setCardRead(cardRead);
        this.serveService.handleCreateServe(serve);

        return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
    }
}
