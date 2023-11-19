package com.example.api.activity.task.filetask;

import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class FileTaskValidator {

    public void validateCreateFileTaskForm(CreateFileTaskForm form) throws RequestValidationException {
        if (Stream.of(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(),
                form.getRequiredKnowledge(), form.getMaxPoints()).anyMatch(Objects::isNull)) {
            log.info("All fields in CreateFileTaskForm should not be null");
            throw new RequestValidationException(ExceptionMessage.FORM_FIELDS_NOT_NULL);
        }
    }
}
