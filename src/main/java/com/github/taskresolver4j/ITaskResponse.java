package com.github.taskresolver4j;

import java.io.IOException;

public interface ITaskResponse<T> {
  boolean isSuccess();
  void processResponse(T response) throws IOException;
}
