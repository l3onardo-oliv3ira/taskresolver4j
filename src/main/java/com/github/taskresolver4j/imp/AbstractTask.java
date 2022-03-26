/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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
