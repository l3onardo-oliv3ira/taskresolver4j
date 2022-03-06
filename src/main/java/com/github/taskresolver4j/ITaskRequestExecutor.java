package com.github.taskresolver4j;

import com.github.taskresolver4j.exception.TaskExecutorException;

public interface ITaskRequestExecutor<I, O> {
  
  void execute(I request, O response) throws TaskExecutorException;
  void close();
}
