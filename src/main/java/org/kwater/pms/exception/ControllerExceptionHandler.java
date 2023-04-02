package org.kwater.pms.exception;

import org.kwater.pms.web.common.Message;
import org.kwater.pms.web.common.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(basePackages = "org.kwater.pms.web")
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseDTO> exception(Exception e) {
        return ResponseEntity.badRequest().body(ResponseDTO.badRequest(e.getMessage(), Message.ERROR.getMessage()));
    }

}
