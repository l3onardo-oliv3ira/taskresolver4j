package com.github.taskresolver4j;

public interface IFailureAlerter {
  void alert(IExceptionContext context);
}