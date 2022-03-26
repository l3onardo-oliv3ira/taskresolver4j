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
