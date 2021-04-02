package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.service.UserService;
import com.example.springboot.validator.NoteValidator;
import com.example.springboot.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Calendar;

@Controller
public class MainController {

    UserValidator userValidator;
    UserService userService;
    NoteValidator noteValidator;
    NoteService noteService;

    @Autowired
    public MainController(UserValidator userValidator,
                          UserService userService,
                          NoteValidator noteValidator,
                          NoteService noteService) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.noteService = noteService;
        this.noteValidator = noteValidator;
    }

    @GetMapping("/")
    public String indexPageGET(@AuthenticationPrincipal User user,
                               Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotes());
        return "index";
    }


    @GetMapping("/sign_up")
    public String signUpPageGET(@ModelAttribute("user") User user) {
        return "sign_up";
    }

    @PostMapping("/sign_up")
    public String signUpPagePOST(@ModelAttribute User user,
                                 BindingResult result) {
        userValidator.validate(user, result);
        if (result.hasErrors())
            return "sign_up";
        user.setRole(User.ROLE_USER);
        userService.addUser(user);
        return "redirect:/sign_in";
    }


    @GetMapping("/sign_in")
    public String signInPageGET(@RequestParam(value = "error", required = false) Boolean error,
                                Model model) {
        if (error != null && error)
            model.addAttribute("error", true);
        return "sign_in";
    }


    @GetMapping("/create_note")
    public String createNotePageGET(@ModelAttribute("note") Note note,
                                    @AuthenticationPrincipal User user,
                                    Model model) {
        model.addAttribute("currentUser", user);
        return "create_note";
    }

    @PostMapping("/create_note")
    public String createNotePagePOST(@ModelAttribute Note note,
                                     @AuthenticationPrincipal User user,
                                     BindingResult result) {
        noteValidator.validate(note, result);
        if (result.hasErrors())
            return "create_note";
        note.setIdUser(user.getId());
        note.setCreated(new Date());
        noteService.addNote(note);
        return "redirect:/my_notes";
    }


    @GetMapping("/my_notes")
    public String myNotesPageGET(@AuthenticationPrincipal User user,
                                 Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotesByUserId(user.getId()));
        return "my_notes";
    }


    @GetMapping("/delete_note")
    public String deleteNotePageGET(@AuthenticationPrincipal User user,
                                    @RequestParam(value = "id") int id) {
        Note note = noteService.getById(id);
        //Если такой записи нет или это не запись текущего пользователя
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        noteService.deleteNote(id);
        return "redirect:/my_notes";
    }


    @GetMapping("/edit_note")
    public String editNotePageGET(@AuthenticationPrincipal User user,
                                  @RequestParam(value = "id") int id,
                                  Model model) {
        Note note = noteService.getById(id);
        //Если такой записи нет или это не запись текущего пользователя
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("note", note);
        return "edit_note";
    }

    @PostMapping("/edit_note")
    public String editNotePagePOST(@AuthenticationPrincipal User user,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("id") int id) {
        Note note = noteService.getById(id);
        //Если такой записи нет или это не запись текущего пользователя
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        //Если хотя бы одно из полей пустое
        if (title == null || content == null || title.isEmpty() || content.isEmpty()) {
            return "redirect:/edit_note?id=" + id;
        }
        note.setTitle(title);
        note.setContent(content);
        noteService.updateNote(note);
        return "redirect:/my_notes";
    }
}
