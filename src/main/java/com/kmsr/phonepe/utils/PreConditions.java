package com.kmsr.phonepe.utils;

import com.kmsr.phonepe.Exceptions.InvalidInputException;
import java.util.Collection;
import java.util.Objects;

public class PreConditions {

  public static <T> void nonNull(T obj) {
    if (Objects.isNull(obj)) {
      throw new InvalidInputException();
    }
  }

  public static void notEmpty(String s) {
    nonNull(s);
    if (s.length() == 0) {
      throw new InvalidInputException();
    }
  }

  public static void notEmpty(Collection<?> s) {
    nonNull(s);
    if (s.size() == 0) {
      throw new InvalidInputException();
    }
  }
}
