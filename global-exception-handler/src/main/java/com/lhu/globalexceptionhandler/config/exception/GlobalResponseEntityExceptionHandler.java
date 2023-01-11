package com.lhu.globalexceptionhandler.config.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lhu.globalexceptionhandler.utills.CustomExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String EXCEPTION_PREFIX = "Caught a Exception";
  private static final String SERVER_ERROR_MSG =
      "Oh no! Something bad happened. Please come back later when we fixed that problem. Thanks.";

  @Override
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  protected final ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    final List<ErrorDescriptionDto> errorMessages = new ArrayList<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errorMessages.add(
          new ErrorDescriptionDto(fieldError.getField() + ":" + fieldError.getDefaultMessage()));
    }
    GeneralResponseDto errorResponse =
        new GeneralResponseDto(String.valueOf(status.value()), errorMessages);
    return ResponseEntity.unprocessableEntity().body(errorResponse);
  }

  @Override
  public final ResponseEntity<Object> handleExceptionInternal(
      final Exception exception,
      final Object body,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    return ResponseEntity.status(status)
        .body(
            this.errorResponse(
                new CustomResponseStatusException(
                    status,
                    CustomExceptionCodeEnum.EXP_TEST_CODE_400,
                    exception.getCause().getMessage()),
                headers,
                status,
                exception));
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public final ResponseEntity<GeneralResponseDto> handleAllUncaughtException(
      final RuntimeException exception, final WebRequest request) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    log.error("RuntimeException accrued.");
    return this.errorResponse(
        new CustomResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            CustomExceptionCodeEnum.EXP_TEST_CODE_500,
            SERVER_ERROR_MSG),
        httpHeaders,
        HttpStatus.INTERNAL_SERVER_ERROR,
        exception);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public final ResponseEntity<GeneralResponseDto> handleItemNotFoundException(
      final NotFoundException exception, final WebRequest request) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    log.error("NotFoundException accrued.");
    return this.errorResponse(
        new CustomResponseStatusException(
            HttpStatus.NOT_FOUND,
            CustomExceptionCodeEnum.EXP_TEST_CODE_404,
            exception.getMessage()),
        httpHeaders,
        HttpStatus.NOT_FOUND,
        exception);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public final ResponseEntity<GeneralResponseDto> handleItemNotFoundException(
      final BadRequestException exception, final WebRequest request) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    log.error("BadRequestException accrued.");
    return this.errorResponse(
        new CustomResponseStatusException(
            HttpStatus.BAD_REQUEST,
            CustomExceptionCodeEnum.EXP_TEST_CODE_400,
            exception.getMessage()),
        httpHeaders,
        HttpStatus.BAD_REQUEST,
        exception);
  }

  @ExceptionHandler({CustomResponseStatusException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public final ResponseEntity<GeneralResponseDto> handleUsageExceptions(
      final CustomResponseStatusException exception) {
    HttpHeaders httpHeaders = new HttpHeaders();
    log.error("CustomResponseStatusException accrued.");
    return this.errorResponse(exception, httpHeaders, exception.getStatus(), exception);
  }

  @ExceptionHandler({InvalidFormatException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public final ResponseEntity<GeneralResponseDto> handleInvalidFormatException(
      final InvalidFormatException exception) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    final String fieldName = exception.getPath().get(0).getFieldName();
    final StringBuilder msgBuilder = new StringBuilder();
    if (StringUtils.hasText(exception.getValue().toString())) {
      msgBuilder
          .append(exception.getValue().toString())
          .append(" is invalid for ")
          .append(fieldName);
    } else {
      msgBuilder.append(fieldName).append(" cannot be blank");
    }
    log.error("InvalidFormatException accrued.");
    return this.errorResponse(
        new CustomResponseStatusException(
            HttpStatus.BAD_REQUEST,
            CustomExceptionCodeEnum.EXP_TEST_CODE_400,
            msgBuilder.toString()),
        httpHeaders,
        HttpStatus.BAD_REQUEST,
        exception);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public final ResponseEntity<GeneralResponseDto> handleInvalidPathParamExceptions(
      final MethodArgumentTypeMismatchException exception, final Exception raisedException) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    log.error("MethodArgumentTypeMismatchException accrued.");
    final String pathParameterName = exception.getName();
    return this.errorResponse(
        new CustomResponseStatusException(
            HttpStatus.BAD_REQUEST,
            CustomExceptionCodeEnum.EXP_TEST_CODE_400,
            pathParameterName.concat(" is invalid")),
        httpHeaders,
        HttpStatus.BAD_REQUEST,
        raisedException);
  }

  private ResponseEntity<GeneralResponseDto> errorResponse(
      final CustomResponseStatusException exception,
      final HttpHeaders httpHeaders,
      final HttpStatus status,
      final Exception raisedException) {
    return this.response(getErrorGeneralResponse(exception), httpHeaders, status, raisedException);
  }

  private GeneralResponseDto getErrorGeneralResponse(
      final CustomResponseStatusException exception) {

    return new GeneralResponseDto(
        exception.getCustomExceptionCode(),
        List.of(new ErrorDescriptionDto(exception.getReason())));
  }

  private <T> ResponseEntity<T> response(
      final T body,
      final HttpHeaders httpHeaders,
      final HttpStatus status,
      final Exception exception) {
    log.error(EXCEPTION_PREFIX, exception);
    return new ResponseEntity<>(body, httpHeaders, status);
  }
}
