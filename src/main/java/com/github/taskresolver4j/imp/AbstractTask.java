package com.github.taskresolver4j.imp;

import com.github.progress4j.IProgress;
import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.progress4j.imp.ProgressOptions;
import com.github.taskresolver4j.ITask;
import com.github.utils4j.IParam;
import com.github.utils4j.imp.Params;

public abstract class AbstractTask<T> implements ITask<T> {

  protected final Params params;

  protected AbstractTask(Params params) {
    this.params = params.of(ITask.PARAM_NAME, this);
  }

  protected final Params getParams() {
    return params;
  }
  
  protected final IParam getParameter(String key) {
    return params.get(key);
  }
  
  protected final <V> V getParameterValue(String key) {
    return params.getValue(key);
  }
  
  protected final IProgressView getProgress() {
    return getParameter(IProgress.PARAM_NAME).orElse(ProgressOptions.IDLE);
  }
  
  protected final IProgressView newProgress() {
    return params.of(IProgress.PARAM_NAME, getParameter(IProgressFactory.PARAM_NAME).<IProgressFactory>get().get()).getValue(IProgress.PARAM_NAME);
  }
  
  @Override
  public boolean isValid(StringBuilder whyNot) {
    return true;
  }
}
