package com.github.taskresolver4j.exception;

import com.github.taskresolver4j.IExceptionContext;
import com.github.utils4j.imp.Strings;

public final class ExceptionContext implements IExceptionContext {
  
  private final String detail;
  private final String message;
  private final Throwable cause;
  
  public ExceptionContext(String message, String detail, Throwable cause) {
    this.message = Strings.trim(message);
    this.detail = Strings.trim(detail);
    this.cause = cause;
  }

  @Override
  public String getDetail() {
    return detail;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Throwable getCause() {
    return cause;
  }
}
