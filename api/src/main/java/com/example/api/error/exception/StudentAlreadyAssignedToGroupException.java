package com.example.api.error.exception;

import com.example.api.group.model.Group;
import com.example.api.user.model.User;

public class StudentAlreadyAssignedToGroupException extends RequestValidationException{
    User student;
    Group group;

    public StudentAlreadyAssignedToGroupException(String message, User student, Group group){
        super(message);
        this.student = student;
        this.group = group;
    }

    @Override
    public String getLocalizedMessage() {
        return "Student " + student.getFirstName()  + " " +
                student.getLastName() + " is already assigned to this group " + group.getName();
    }
}
