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
import java.util.ArrayList;
import java.util.List;

import com.github.taskresolver4j.ITaskResponse;

public class TaskResponses<T> implements ITaskResponse<T> {
  
  private List<ITaskResponse<T>> responses = new ArrayList<>(4);

  @Override
  public boolean isSuccess() {
    return true;
  }

  public TaskResponses<T> add(ITaskResponse<T> response) {
    if (response != null) {
      responses.add(response);
    }
    return this;
  }


  @Override
  public void processResponse(T response) throws IOException {
    for(ITaskResponse<T> r: responses) {
      r.processResponse(response);
    }
  }
}
