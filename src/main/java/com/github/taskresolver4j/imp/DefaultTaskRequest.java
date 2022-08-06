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

import java.util.function.Supplier;

import com.github.progress4j.IProgressFactory;
import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.ITaskRequest;
import com.github.taskresolver4j.ITaskRequestExecutor;
import com.github.utils4j.imp.Params;

public class DefaultTaskRequest<T> extends Params implements ITaskRequest<T> {
  
  public static final String PARAM_TASK = ITask.class.getSimpleName() + ".task";
  
  public static final String PARAM_PROGRESS_FACTORY = IProgressFactory.class.getSimpleName() + ".factory";

  public static final String PARAM_EXECUTOR_CLOSING = ITaskRequestExecutor.class.getSimpleName()  + ".closing";
  
  public static final String PARAM_EXECUTOR_ISBATCHSTATE = ITaskRequestExecutor.class.getSimpleName() + ".isbatchstate";

  @Override
  public final ITask<T> getTask(IProgressFactory factory, Supplier<Boolean> closing, Supplier<Boolean> discarding) {
    return of(PARAM_PROGRESS_FACTORY, factory).of(PARAM_EXECUTOR_CLOSING, closing).of(PARAM_EXECUTOR_ISBATCHSTATE, discarding).getValue(PARAM_TASK);
  }
  
  @Override
  public final boolean isValid(StringBuilder whyNot) {
    return true;
  }
}
