package com.github.taskresolver4j;

public interface IExecutorContext extends IFailureAlerter{
  public boolean isClosing();
  public boolean isBatchState();
  public boolean isRunningInBatch();
}
