package com.example.springboot.service;

import com.example.springboot.entity.Note;
import com.example.springboot.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public void addNote(Note note) {
        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(int id) {
        try {
            noteRepository.deleteById(id);
        } catch (Exception ignored) {}
    }

    @Transactional
    public void updateNote(Note note) {
        try {
            noteRepository.save(note);
        } catch (Exception ignored) {}
    }

    public Note getById(int id) {
        Note note = null;
        try {
            note = noteRepository.getOne(id);
        } catch (Exception ignored) {}
        return note;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll(Sort.by("created").descending());
    }

    public List<Note> getAllNotesByUserId(int idUser) {
        //Сортируем по дате создания
        Comparator<Note> comparator = (o1, o2) -> o2.getCreated().compareTo(o1.getCreated());
        List<Note> result = noteRepository.getAllByIdUser(idUser);
        result.sort(comparator);
        return result;
    }
}
