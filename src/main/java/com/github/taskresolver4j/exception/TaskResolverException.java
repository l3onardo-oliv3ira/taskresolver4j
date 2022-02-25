package com.github.taskresolver4j.exception;

public class TaskResolverException extends Exception {

  private static final long serialVersionUID = 1L;

  public TaskResolverException(String message) {
    super(message);
  }

  public TaskResolverException(String message, Throwable cause) {
    super(message, cause);
  }
}
