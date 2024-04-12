package com.gachonoj.problemservice.config.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gachonoj.problemservice.common.codes.ErrorCode;
import com.gachonoj.problemservice.common.response.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    /*
     * [ Exception List ]
     * MethodArgumentNotValidException
     * HttpMessageNotReadableException
     * HttpClientErrorException
     * NullPointerException
     * NoHandlerFoundException
     * IOException
     * JsonParseException
     * JsonProcessingException
     * Exception
     */

    /**
     * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.NOT_VALID_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     *
     * @param ex MissingRequestHeaderException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.REQUEST_BODY_MISSING_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }

    /**
     * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
     *
     * @param ex HttpMessageNotReadableException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.REQUEST_BODY_MISSING_ERROR,ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
     *
     * @param ex MissingServletRequestParameterException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleMissingRequestHeaderExceptionException(
            MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR,ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    /**
     * [Exception] 잘못된 서버 요청일 경우 발생한 경우
     *
     * @param e HttpClientErrorException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<CommonResponseDto<String>> handleBadRequestException(HttpClientErrorException e) {
        log.error("HttpClientErrorException.BadRequest", e);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.BAD_REQUEST_ERROR,e.getMessage()), HTTP_STATUS_OK);
    }


    /**
     * [Exception] 잘못된 주소로 요청 한 경우
     *
     * @param e NoHandlerFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleNoHandlerFoundExceptionException(NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundExceptionException", e);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.NOT_FOUND_ERROR,e.getMessage()), HTTP_STATUS_OK);
    }


    /**
     * [Exception] NULL 값이 발생한 경우
     *
     * @param e NullPointerException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<CommonResponseDto> handleNullPointerException(NullPointerException e) {
        log.error("handleNullPointerException", e);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.NULL_POINT_ERROR,e.getMessage()), HTTP_STATUS_OK);
    }

    /**
     * Input / Output 내에서 발생한 경우
     *
     * @param ex IOException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleIOException(IOException ex) {
        log.error("handleIOException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.IO_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }


    /**
     * com.google.gson 내에 Exception 발생하는 경우
     *
     * @param ex JsonParseException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleJsonParseExceptionException(JsonParseException ex) {
        log.error("handleJsonParseExceptionException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.JSON_PARSE_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }

    /**
     * com.fasterxml.jackson.core 내에 Exception 발생하는 경우
     *
     * @param ex JsonProcessingException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<CommonResponseDto<String>> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("handleJsonProcessingException", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.REQUEST_BODY_MISSING_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }

    // ==================================================================================================================

    /**
     * [Exception] 모든 Exception 경우 발생
     *
     * @param ex Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<CommonResponseDto<String>> handleAllExceptions(Exception ex) {
        log.error("Exception", ex);
        return new ResponseEntity<>(CommonResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR,ex.getMessage()), HTTP_STATUS_OK);
    }
}
