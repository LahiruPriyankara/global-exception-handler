package com.lhu.globalexceptionhandler.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorDescriptionDto implements Serializable {
  private String message;
}
