package com.example.springboot.validator;

import com.example.springboot.entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NoteValidator implements Validator {

    private final MessageSource messageSource;

    @Autowired
    public NoteValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Note.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Note note = (Note) o;

        if (note.getTitle().isEmpty())
            errors.rejectValue("title", "", messageSource.getMessage("noteValidator.title.required", null, LocaleContextHolder.getLocale()));

        if (note.getContent().isEmpty())
            errors.rejectValue("content", "", messageSource.getMessage("noteValidator.content.required", null, LocaleContextHolder.getLocale()));
    }
}
