package com.lhu.globalexceptionhandler.config.exception;

import com.lhu.globalexceptionhandler.utills.CustomExceptionCodeEnum;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

public class CustomResponseStatusException extends NestedRuntimeException {

  private final int status;
  private final String customExceptionCode;
  @Nullable private final String reason;

  public CustomResponseStatusException(
      final HttpStatus status, final CustomExceptionCodeEnum customExceptionCode) {
    this(status, customExceptionCode, (String) null);
  }

  public CustomResponseStatusException(
      final HttpStatus status,
      final CustomExceptionCodeEnum customExceptionCode,
      @Nullable final String reason) {
    super("");
    Assert.notNull(status, "HttpStatus is required");
    this.status = status.value();
    this.customExceptionCode = customExceptionCode.getExceptionMessage();
    this.reason = reason;
  }

  public CustomResponseStatusException(
      final HttpStatus status,
      final CustomExceptionCodeEnum customExceptionCode,
      @Nullable final String reason,
      @Nullable final Throwable cause) {
    super((String) null, cause);
    Assert.notNull(status, "HttpStatus is required");
    this.status = status.value();
    this.customExceptionCode = customExceptionCode.getExceptionMessage();
    this.reason = reason;
  }

  public CustomResponseStatusException(
      final int rawStatusCode,
      final CustomExceptionCodeEnum customExceptionCode,
      @Nullable final String reason,
      @Nullable final Throwable cause) {
    super((String) null, cause);
    this.status = rawStatusCode;
    this.customExceptionCode = customExceptionCode.getExceptionMessage();
    this.reason = reason;
  }

  /**
   * getStatus
   *
   * @return HttpStatus
   */
  public HttpStatus getStatus() {
    return HttpStatus.valueOf(this.status);
  }

  /**
   * getRawStatusCode
   *
   * @return int
   */
  public int getRawStatusCode() {
    return this.status;
  }

  /**
   * getHeaders
   *
   * @return Map
   */
  @Deprecated
  public Map<String, String> getHeaders() {
    return Collections.emptyMap();
  }

  /**
   * getResponseHeaders
   *
   * @return HttpHeaders
   */
  public HttpHeaders getResponseHeaders() {
    Map<String, String> headers = this.getHeaders();
    if (headers.isEmpty()) {
      return HttpHeaders.EMPTY;
    } else {
      HttpHeaders result = new HttpHeaders();
      this.getHeaders().forEach(result::add);
      return result;
    }
  }

  /**
   * getReason
   *
   * @return reason
   */
  @Nullable
  public String getReason() {
    return this.reason;
  }

  /**
   * getCustomExceptionCode
   *
   * @return common exception code
   */
  public String getCustomExceptionCode() {
    return this.customExceptionCode;
  }

  /**
   * getMessage
   *
   * @return message
   */
  public String getMessage() {
    HttpStatus code = HttpStatus.resolve(this.status);
    String msg =
        (code != null ? code : this.status)
            + (this.reason != null ? " \"" + this.reason + "\"" : "");
    return NestedExceptionUtils.buildMessage(msg, this.getCause());
  }
}
