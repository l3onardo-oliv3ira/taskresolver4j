package com.github.taskresolver4j;

public interface IExceptionContext {
  String getDetail();
  String getMessage();
  Throwable getCause();
}