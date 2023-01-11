package com.lhu.globalexceptionhandler.utills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum CustomExceptionCodeEnum {
  EXP_TEST_CODE_404("EXP_TEST_CODE_404"),
  EXP_TEST_CODE_400("EXP_TEST_CODE_400"),
  EXP_TEST_CODE_500("EXP_TEST_CODE_500");

  @Getter @Setter private String exceptionMessage;
}
