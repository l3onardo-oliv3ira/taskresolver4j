package com.github.taskresolver4j;

import java.util.function.Supplier;

public interface ITask<T> extends Supplier<ITaskResponse<T>> {
  
  String PARAM_NAME = ITask.class.getSimpleName() + ".instance";

  String getId();

  boolean isValid(StringBuilder reasonIfNot);

  default void dispose() {}
}
