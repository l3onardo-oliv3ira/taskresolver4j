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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.progress4j.IStage;
import com.github.taskresolver4j.IRequestResolver;
import com.github.taskresolver4j.ITask;
import com.github.taskresolver4j.ITaskRequest;
import com.github.taskresolver4j.ITaskRequestExecutor;
import com.github.taskresolver4j.ITaskResponse;
import com.github.taskresolver4j.exception.TaskExecutorDiscartingException;
import com.github.taskresolver4j.exception.TaskExecutorException;
import com.github.taskresolver4j.exception.TaskExecutorFinishingException;
import com.github.taskresolver4j.exception.TaskResolverException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.BooleanTimeout;
import com.github.utils4j.imp.Services;

public class TaskRequestExecutor<I, O, R extends ITaskRequest<O>> implements ITaskRequestExecutor<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(TaskRequestExecutor.class);
  
  private final IProgressFactory factory;

  private volatile boolean closing = false;

  protected final ExecutorService executor;
  
  private final IRequestResolver<I, O, R> resolver;

  private final BooleanTimeout discarting = new BooleanTimeout(2500);
  
  private static enum Stage implements IStage {
    REQUEST_HANDLING("Tratando requisição"),
    
    PROCESSING_TASK("Processando a tarefa");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }
  };

  protected TaskRequestExecutor(IRequestResolver<I, O, R> resolver, IProgressFactory factory) {
    this(resolver, factory, Executors.newCachedThreadPool());
  }

  protected TaskRequestExecutor(IRequestResolver<I, O, R> resolver, IProgressFactory factory, ExecutorService executor) {
    this.resolver = Args.requireNonNull(resolver, "resolver is null");
    this.factory = Args.requireNonNull(factory, "factory is null");
    this.executor = Args.requireNonNull(executor, "executor is null");
  }
  
  @Override
  public final void notifyClosing() {
    closing = true;
    factory.interrupt();
  }
  
  @Override
  public final void notifyOpening() {
    closing = false;
    bindCanceller();
  }

  private void bindCanceller() {
    factory.ifCanceller(discarting::setTrue);
  }

  @Override
  public void close() {
    Services.shutdownNow(executor, 2); 
    discarting.shutdown();
  }
  
  protected boolean isClosing() {
    return closing;
  }

  @Override
  public final void async(Runnable runnable) {
    this.executor.execute(runnable);
  }
  
  private void checkAvailability() throws TaskExecutorException {
    if (closing) {
      throw new TaskExecutorFinishingException();
    }
    if (discarting.isTrue()) {
      throw new TaskExecutorDiscartingException();
    }
  }
  
  @Override
  public final void execute(I request, O response) throws TaskExecutorException {
    checkAvailability();
    try {
      IProgressView progress = factory.get(); 
      try {
        beginExecution(progress);
        progress.begin(Stage.REQUEST_HANDLING, 2);
        progress.step("Resolvendo URL");
        R taskRequest;
        try {
          taskRequest = resolver.resolve(request);
        } catch (TaskResolverException e) {
          throw new TaskExecutorException("Não foi possível resolver a requisição", e);
        }
        progress.step("Notificando criação de requisção");
        onRequestResolved(taskRequest);
        progress.end();
        ITask<O> task = taskRequest.getTask(factory, this::isClosing);
        try {
          progress.begin(Stage.PROCESSING_TASK);
          progress.info("Tarefa '%s'", task.getId());
          ITaskResponse<O> output = task.get();          
          try {
            output.processResponse(response);
          } catch (IOException e) {
            LOGGER.warn("Exceção no processamento da resposta", e);
            progress.abort(e);
            return;
          }          
          progress.end();
        } finally {
          task.dispose();
        }
      }catch(Exception e) {
        progress.abort(e);
      }finally {
        endExecution(progress);
      }
    }catch(Throwable e) {
      throw new TaskExecutorException("Exceção inesperada na execução da tarefa", e);
    }
  }

  protected void onRequestResolved(R taskRequest) {
  }

  protected void beginExecution(IProgressView progress) {
    progress.display();
  }

  protected void endExecution(IProgressView progress) {
    try {
      progress.undisplay();
      progress.stackTracer(s -> LOGGER.info(s.toString()));
    } finally {
      progress.dispose();
    }
  }
}



