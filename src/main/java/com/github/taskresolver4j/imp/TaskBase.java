package com.github.taskresolver4j.imp;

import static com.github.utils4j.imp.Strings.empty;

import java.awt.Image;
import java.util.function.Supplier;

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

  private final boolean isClosing() {
    Supplier<Boolean> closing = getParameter(DefaultTaskRequest.PARAM_EXECUTOR_CLOSING).orElse(() -> false);
    return closing.get();
  }

  private final boolean isBatchState() {
    Supplier<Boolean> discarding = getParameter(DefaultTaskRequest.PARAM_EXECUTOR_ISBATCHSTATE).orElse(() -> false);
    return discarding.get();
  }

  protected final void ifNotClosing(Runnable code) {
    if (!isClosing()) {
      code.run();
    }
  }

  protected final void ifBatchStateThrowDiscard(String message) throws TaskDiscardException {
    if (isBatchState()) {
      //ifNotClosing(() -> CancelAlert.showWaiting());
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
    ifNotClosing(() -> ExceptionAlert.show(getIcon(), message, detail, cause));
    return new TaskException(message + "\n" + detail, cause);
  }

  protected String getSupport() {
    return "Informe os detalhes técnicos abaixo ao suporte/callcenter";
  }

  protected abstract Image getIcon();
}