package com.example.api.util.csv;

import com.example.api.course.Course;
import com.example.api.user.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Converter<T> {
    byte[] convertToByteArray(Map<User, List<CSVTaskResult>> data, List<String> firstRow, Course course) throws IOException;
}
