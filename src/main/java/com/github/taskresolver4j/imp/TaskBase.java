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

import static com.github.utils4j.imp.Strings.empty;

import java.awt.Image;

import com.github.taskresolver4j.IExecutorContext;
import com.github.taskresolver4j.exception.TaskDiscardException;
import com.github.taskresolver4j.exception.TaskException;
import com.github.utils4j.gui.imp.CancelAlert;
import com.github.utils4j.gui.imp.ExceptionAlert;
import com.github.utils4j.gui.imp.MessageAlert;
import com.github.utils4j.imp.Params;

public abstract class TaskBase<T> extends ParamBasedTask<T> {

  public TaskBase(Params params) {
    super(params);
  }

  protected final void throwCancel(String message) throws InterruptedException {
    throwCancel(message, true);
  }

  protected final void throwCancel() throws InterruptedException {
    throwCancel(CancelAlert.CANCELED_OPERATION_MESSAGE);
  }

  protected final void throwCancel(boolean interrupt) throws InterruptedException {
    throwCancel(CancelAlert.CANCELED_OPERATION_MESSAGE, interrupt);
  }

  protected final void throwCancel(String message, boolean interrupt) throws InterruptedException {
    if (interrupt)
      Thread.currentThread().interrupt();
    throw getProgress().abort(new InterruptedException(message));
  }
  
  protected final void showCancel() {
    ifNotClosing(() -> CancelAlert.show(getIcon()));    
  }

  protected final IExecutorContext getExecutorContext() {
    return (IExecutorContext)getParameterValue(DefaultTaskRequest.PARAM_EXECUTOR_CONTEXT);
  }
  
  private final boolean isClosing() {
    return getExecutorContext().isClosing();
  }

  private final boolean isBatchState() {
    return getExecutorContext().isBatchState();
  }

  protected final void ifNotClosing(Runnable code) {
    if (!isClosing()) {
      code.run();
    }
  }

  protected final void ifBatchStateThrowDiscard(String message) throws TaskDiscardException {
    if (isBatchState()) {
      throw new TaskDiscardException(message);
    }
  }

  protected final void showInfo(String message) {
    ifNotClosing(() -> MessageAlert.showInfo(message, getIcon()));
  }

  protected final void showInfo(String message, String textButton) {
    ifNotClosing(() -> MessageAlert.showInfo(message, textButton, getIcon()));
  }

  protected final TaskException showFail(String message) {
    return showFail(message, message, null);
  }

  protected final TaskException showFail(String message, Throwable cause) {
    return showFail(message, empty(), cause);
  }

  protected final TaskException showFail(String message, String detail) {
    return showFail(message, detail, null);
  }

  protected final TaskException showFail(String message, String detail, Throwable cause) {    
    ifNotClosing(getAlertFailCode(message, detail, cause));
    return new TaskException(message + "\n" + detail, cause);
  }

  protected Runnable getAlertFailCode(String message, String detail, Throwable cause) {
    return () -> ExceptionAlert.show(getIcon(), message, detail, cause);
  }  
  
  protected String getSupport() {
    return "Informe os detalhes técnicos abaixo ao suporte/callcenter";
  }

  protected abstract Image getIcon();
}