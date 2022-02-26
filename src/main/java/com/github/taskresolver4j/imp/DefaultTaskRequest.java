package com.github.taskresolver4j.imp;

import com.github.progress4j.IProgress;
import com.github.progress4j.IProgressFactory;
import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.ITaskRequest;
import com.github.utils4j.imp.Params;

public class DefaultTaskRequest<T> extends Params implements ITaskRequest<T> {
  
  @Override
  public final ITask<T> getTask(IProgress progress, IProgressFactory factory) {
    return super
      .of(IProgress.PARAM_NAME, progress)
      .of(IProgressFactory.PARAM_NAME, factory)
      .getValue(ITask.PARAM_NAME);
  }
  
  @Override
  public final boolean isValid(StringBuilder invalidCause) {
    return true;
  }
}
