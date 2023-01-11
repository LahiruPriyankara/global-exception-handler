package com.lhu.globalexceptionhandler.controller;

import com.lhu.globalexceptionhandler.config.exception.CustomResponseStatusException;
import com.lhu.globalexceptionhandler.utills.CustomExceptionCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionTestController {

  @GetMapping("/test-exception/{type}")
  public ResponseEntity testMethodForExceptionTest(@PathVariable(name = "type") final String type) {

    if (type.equalsIgnoreCase("1")) {
      throw new CustomResponseStatusException(
          HttpStatus.BAD_REQUEST,
          CustomExceptionCodeEnum.EXP_TEST_CODE_400,
          "CustomResponseStatusException Thrown.");

    } else if (type.equalsIgnoreCase("2")) {
      int a = 10 / 0;
    }

    return ResponseEntity.ok("test-exception called successfully.");
  }
}
