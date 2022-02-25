package com.github.taskresolver4j;

import java.io.IOException;
import java.util.function.Function;

import com.github.utils4j.imp.Params;

public interface IRequestReader<T extends Params> {  
  
  default T read(String text, T params) throws IOException {
    return read(text, params, o -> o);
  }
  
  T read(String text, T params, Function<?, ?> wrapper) throws IOException;
}
