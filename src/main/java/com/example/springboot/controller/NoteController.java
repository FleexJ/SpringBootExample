package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.validator.NoteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/note")
public class NoteController {

    private final NoteValidator noteValidator;
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteValidator noteValidator, NoteService noteService) {
        this.noteValidator = noteValidator;
        this.noteService = noteService;
    }

    @GetMapping("/create_note")
    public String createNoteGET(@ModelAttribute("note") Note note,
                                @AuthenticationPrincipal User user,
                                Model model) {
        model.addAttribute("currentUser", user);
        return "/note/create_note";
    }

    @PostMapping("/create_note")
    public String createNotePOST(@ModelAttribute Note note,
                                 @AuthenticationPrincipal User user,
                                 BindingResult result) {
        noteValidator.validate(note, result);
        if (result.hasErrors())
            return "/note/create_note";
        note.setIdUser(user.getId());
        note.setCreated(new Date());
        noteService.addNote(note);
        return "redirect:/note/my_notes";
    }


    @GetMapping("/my_notes")
    public String myNotesGET(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotesByUserId(user.getId()));
        return "/note/my_notes";
    }


    @GetMapping("/delete_note")
    public String deleteNoteGET(@AuthenticationPrincipal User user,
                                @RequestParam(value = "id") int id) {
        Note note = noteService.getById(id);
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        try {
            noteService.deleteNote(id);
        } catch (Exception ignored) {}
        return "redirect:/note/my_notes";
    }


    @GetMapping("/edit_note")
    public String editNoteGET(@AuthenticationPrincipal User user,
                              @RequestParam(value = "id") int id,
                              @RequestParam(required = false) Boolean error,
                              Model model) {
        Note note = noteService.getById(id);
        if (note == null || note.getIdUser() != user.getId() && !user.isAdmin()) {
            return "redirect:/";
        }
        if (error != null && error)
            model.addAttribute("error", true);
        model.addAttribute("currentUser", user);
        model.addAttribute("note", note);
        return "/note/edit_note";
    }

    @PatchMapping("/edit_note")
    public String editNotePOST(@AuthenticationPrincipal User user,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("id") int id) {
        Note note = noteService.getById(id);
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        //Если хотя бы одно из полей пустое
        if (title == null || content == null || title.isEmpty() || content.isEmpty()) {
            return "redirect:/note/edit_note?id=" + id + "&error=true";
        }
        note.setTitle(title);
        note.setContent(content);
        try {
            noteService.updateNote(note);
        } catch (Exception ignored) {}
        return "redirect:/note/my_notes";
    }
}
