package com.github.taskresolver4j.imp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.taskresolver4j.IExceptionContext;
import com.github.taskresolver4j.IFailureAlerter;
import com.github.taskresolver4j.exception.ExceptionContext;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.BooleanTimeout;

class AsyncFailureAlerter implements IFailureAlerter {
  
  private final Supplier<Boolean> isToPostpone;

  private final Consumer<IExceptionContext> display;

  private final List<IExceptionContext> stack = new LinkedList<>();
  
  private final BooleanTimeout debt = new BooleanTimeout("async-fail", 1500, this::checkDebt);

  public AsyncFailureAlerter(Consumer<IExceptionContext> display, Supplier<Boolean> isToPostpone) {
    this.display = Args.requireNonNull(display, "display is null");
    this.isToPostpone = Args.requireNonNull(isToPostpone, "isToPostpone is null");
  }

  @Override
  public final void alert(IExceptionContext context) {
    if (context != null) {
      synchronized(stack) {
        stack.add(context);
      }
      debt.setTrue();
    }
  }
  
  private void checkDebt() {
    if (isToPostpone.get()) {
      debt.setTrue();
      return;
    }

    List<IExceptionContext> failList;
    synchronized(stack) {
      failList = new ArrayList<>(stack);
      stack.clear();
    }
    
    final int count = failList.size();
    if (count == 0) {
      return;
    }
    
    String title = null;
    String detail = null;
    Throwable rootCause = null;   
    
    StringBuilder details = new StringBuilder();
    
    for(IExceptionContext context: failList) {
      final Throwable contextCause = context.getCause();
      final String contextMessage = context.getMessage();
      final String contextDetail = context.getDetail();
      
      if (rootCause == null) {
        rootCause = contextCause;
      } else if (contextCause != null) {
        rootCause.addSuppressed(contextCause);
      }        
      int s = details.length();
      if (title == null || !title.startsWith(contextMessage)) {
        details.append(title = contextMessage);
      }
      if (detail == null || !detail.startsWith(contextDetail)) {
        details.append(detail = contextDetail);
      }
      if (s != detail.length()) { //se acrescentou detalhes!
        details.append('\n');
      }
    };

    String message = count + ((count > 1) ? 
      " operações falharam" : 
      " operação falhou") + 
      ". Tente novamente!";
    
    display.accept(new ExceptionContext(message, details.toString(), rootCause));
  }
}
