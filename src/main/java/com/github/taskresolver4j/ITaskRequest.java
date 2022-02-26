package com.github.taskresolver4j;

import com.github.progress4j.IProgress;
import com.github.progress4j.IProgressFactory;

public interface ITaskRequest<O> {
  
  ITask<O> getTask(IProgress progress, IProgressFactory factory);
  
  boolean isValid(StringBuilder because);
}
