package com.github.taskresolver4j;

import com.github.taskresolver4j.exception.TaskResolverException;

public interface IRequestResolver<I, O, R extends ITaskRequest<O>> {

  R resolve(I request) throws TaskResolverException;
}
