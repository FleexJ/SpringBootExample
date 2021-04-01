package com.example.springboot.validator;

import com.example.springboot.entity.Note;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NoteValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Note.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Note note = (Note) o;

        if (note.getTitle().isEmpty())
            errors.rejectValue("title", "", "Title is required");

        if (note.getContent().isEmpty())
            errors.rejectValue("content", "", "Content is required");
    }
}
