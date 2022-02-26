package com.github.taskresolver4j.imp;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.github.taskresolver4j.exception.TaskExecutorException;
import com.github.taskresolver4j.exception.TaskResolverException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Services;

public class TaskRequestExecutor<I, O, R extends ITaskRequest<O>> implements ITaskRequestExecutor<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(TaskRequestExecutor.class);
  
  private final IProgressFactory factory;

  private final IRequestResolver<I, O, R> requestResolver;
  
  protected final AtomicBoolean localRequest = new AtomicBoolean(false);

  protected final ExecutorService executor;
  
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

    @Override
    public final String interval() {
      return "(" + (ordinal() + 1) + " de 2)";
    }
  };

  protected TaskRequestExecutor(IRequestResolver<I, O, R> resolver, IProgressFactory factory) {
    this(resolver, factory, Executors.newCachedThreadPool());
  }

  protected TaskRequestExecutor(IRequestResolver<I, O, R> resolver, IProgressFactory factory, ExecutorService executor) {
    this.requestResolver = Args.requireNonNull(resolver, "resolver is null");
    this.factory = Args.requireNonNull(factory, "factory is null");
    this.executor = Args.requireNonNull(executor, "executor is null");
  }
  
  @Override
  public void close() {
    Services.shutdownNow(executor, 2); 
  }
  
  @Override
  public final void execute(I request, O response) throws TaskExecutorException {
    try {
      IProgressView progress = factory.get(); 
      try {
        beginExecution(progress);
        progress.begin(Stage.REQUEST_HANDLING, 2);
        progress.step("Resolvendo URL");
        R taskRequest;
        try {
          taskRequest = requestResolver.resolve(request);
        } catch (TaskResolverException e) {
          throw new TaskExecutorException("Não foi possível resolver a requisição", e);
        }
        progress.step("Notificando criação de requisção");
        onRequestResolved(taskRequest);
        progress.end();
        ITask<O> task = taskRequest.getTask(progress, factory);
        try {
          progress.begin(Stage.PROCESSING_TASK, 2);
          progress.step("Iniciando a execução da tarefa '%s'", task.getId());
          ITaskResponse<O> output = task.get();
          progress.step("Processando resultados.");
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

  @Override
  public final void setAllowLocalRequest(boolean enabled) {
    this.localRequest.set(enabled);
  }

  protected void onRequestResolved(R taskRequest) {
  }

  protected void beginExecution(IProgressView progress) {
    progress.display();
  }

  protected void endExecution(IProgressView progress) {
    setAllowLocalRequest(false);
    progress.undisplay();
    progress.stackTracer(s -> LOGGER.info(s.toString()));
    progress.dispose();
  }
}



