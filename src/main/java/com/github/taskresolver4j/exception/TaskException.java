package com.github.taskresolver4j.exception;

public class TaskException extends Exception {
  private static final long serialVersionUID = 5036913751425298195L;
  
  public TaskException(Exception cause) {
    super(cause);
  }
  
  public TaskException(String message) {
    super(message);
  }
  
  public TaskException(String message, Throwable cause) {
    super(message, cause);
  }
}
