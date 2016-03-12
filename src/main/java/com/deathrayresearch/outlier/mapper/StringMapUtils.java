package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 */
public interface StringMapUtils extends Column {

  String next();

  // Reset the pointer after running a scan so that the next scan succeeds
  void reset();

  default TextColumn upperCase() {
    TextColumn newColumn = TextColumn.create(this.name() + "[ucase]");
    TextColumn thisColumn = (TextColumn) this;
    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      if (value == null) {
        newColumn.set(r, null);
      } else {
        newColumn.set(r, value.toUpperCase());
      }
    }
    reset();
    return newColumn;
  }

  default TextColumn lowerCase() {

    TextColumn newColumn = TextColumn.create(name() + "[lcase]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.toLowerCase());
    }
    reset();
    return newColumn;
  }

  default TextColumn trim() {

    TextColumn newColumn = TextColumn.create(name() + "[trim]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.trim());
    }
    reset();
    return newColumn;
  }

  default TextColumn replaceAll(String regex, String replacement) {

    TextColumn newColumn = TextColumn.create(name() + "[repl]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.replaceAll(regex, replacement));
    }
    reset();
    return newColumn;
  }

  default TextColumn replaceFirst(String regex, String replacement) {

    TextColumn newColumn = TextColumn.create(name() + "[repl]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.replaceFirst(regex, replacement));
    }
    reset();
    return newColumn;
  }

  default TextColumn substring(int start, int end) {

    TextColumn newColumn = TextColumn.create(name() + "[sub]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.substring(start, end));
    }
    reset();
    return newColumn;
  }


  default TextColumn substring(int start) {

    TextColumn newColumn = TextColumn.create(name() + "[sub]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.substring(start));
    }
    reset();
    return newColumn;
  }

  default TextColumn abbreviate(int maxWidth) {

    TextColumn newColumn = TextColumn.create(name() + "[abbr]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, StringUtils.abbreviate(value, maxWidth));
    }
    reset();
    return newColumn;
  }

  default TextColumn padEnd(int minLength, char padChar) {

    TextColumn newColumn = TextColumn.create(name() + "[pad]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, Strings.padEnd(value, minLength, padChar));
    }
    reset();
    return newColumn;
  }

  default TextColumn padStart(int minLength, char padChar) {

    TextColumn newColumn = TextColumn.create(name() + "[pad]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, Strings.padStart(value, minLength, padChar));
    }
    reset();
    return newColumn;
  }

  default TextColumn commonPrefix(TextColumn column2) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[prefix]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, Strings.commonPrefix(value1, value2));
    }
    reset();
    return newColumn;
  }

  default TextColumn commonSuffix(TextColumn column2) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[suffix]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, Strings.commonSuffix(value1, value2));
    }
    reset();
    return newColumn;
  }

  /**
   * Returns a column containing the levenshtein distance between the two given string columns
   */
  default FloatColumn distance(TextColumn column2) {

    FloatColumn newColumn = FloatColumn.create(name() + column2.name() + "[distance]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, StringUtils.getLevenshteinDistance(value1, value2));
    }
    reset();
    return newColumn;
  }

  default TextColumn join(TextColumn column2, String delimiter) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[join]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String[] values = new String[2];
      values[0] = thisColumn.get(r);
      values[1] = column2.get(r);
      newColumn.set(r, StringUtils.join(values, delimiter));
    }
    reset();
    return newColumn;
  }

  /**
   * Returns an IntColumn, the elements of which represent the sum of number of times each input String appears in each
   * element of the receiver
   */
  default IntColumn countOccurrences(String ... value) {
    Preconditions.checkArgument(value.length == 0, "Parameter array must not be empty");
    IntColumn intColumn = IntColumn.create("Occurances of " + value[0]);

    while (hasNext()) {
      String str = next();
      int count = 0;
      for (String findStr : value) {
        int lastIndex = 0;

        while (lastIndex != -1) {

          lastIndex = str.indexOf(findStr, lastIndex);

          if (lastIndex != -1) {
            count++;
            lastIndex += findStr.length();
          }
        }
      }
      intColumn.add(count);
    }
    reset();
    return intColumn;
  }

  default CategoryColumn extractFirstMatch(String regex) {
    CategoryColumn column = CategoryColumn.create(name() + " matches of \"" + regex + "\"");
    Pattern pattern = Pattern.compile(regex);

    while (hasNext()) {
      String mydata = next();
      Matcher matcher = pattern.matcher(mydata);
      if (matcher.find()) {
        column.add(matcher.group(0));
      }
    }
    reset();
    return column;
  }
}
