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
  
  private final BooleanTimeout debt = new BooleanTimeout("async-fail", 2100, this::checkDebt);

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
