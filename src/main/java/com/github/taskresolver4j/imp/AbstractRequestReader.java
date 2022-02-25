package com.github.taskresolver4j.imp;

import java.io.IOException;
import java.util.function.Function;

import com.github.taskresolver4j.IRequestReader;
import com.github.taskresolver4j.ITask;
import com.github.utils4j.ITextReader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.JsonTextReader;
import com.github.utils4j.imp.Params;

public abstract class AbstractRequestReader<P extends Params, Pojo> implements IRequestReader<P>{

  private ITextReader pojoReader;  
  
  public AbstractRequestReader(Class<?> jsonClass) {
    this(new JsonTextReader(jsonClass));
  }
  
  public AbstractRequestReader(ITextReader pojoReader) {
    this.pojoReader = Args.requireNonNull(pojoReader,  "pojoReader is null");    
  }

  @SuppressWarnings("unchecked")
  @Override
  public final P read(String text, P params, Function<?,?> wrapper) throws IOException {    
    ITask<?> task = createTask(params, (Pojo)wrapper.apply(pojoReader.read(text)));
    StringBuilder whyNot = new StringBuilder();
    if (!task.isValid(whyNot)) {
      throw new IOException("Unabled to create a valid task with parameter: " + text + " reason: " + whyNot);
    }
    return params;
  }
  
  protected abstract ITask<?> createTask(P output, Pojo pojo) throws IOException;
}
