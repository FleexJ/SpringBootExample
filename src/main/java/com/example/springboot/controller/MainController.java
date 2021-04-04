package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.service.UserService;
import com.example.springboot.validator.NoteValidator;
import com.example.springboot.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class MainController {

    private final UserValidator userValidator;
    private final UserService userService;
    private final NoteValidator noteValidator;
    private final NoteService noteService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MainController(UserValidator userValidator,
                          UserService userService,
                          NoteValidator noteValidator,
                          NoteService noteService,
                          PasswordEncoder passwordEncoder) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.noteService = noteService;
        this.noteValidator = noteValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String indexGET(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotes());
        return "index";
    }


    @GetMapping("/sign_up")
    public String signUpGET(@ModelAttribute("user") User user) {
        return "sign_up";
    }

    @PostMapping("/sign_up")
    public String signUpPOST(@ModelAttribute User user,
                             BindingResult result) {
        userValidator.validate(user, result);
        if (result.hasErrors())
            return "sign_up";
        user.setRole(User.ROLE_USER);
        userService.addUser(user);
        return "redirect:/sign_in";
    }


    @GetMapping("/sign_in")
    public String signInGET(@RequestParam(value = "error", required = false) Boolean error,
                            Model model) {
        if (error != null && error)
            model.addAttribute("error", true);
        return "sign_in";
    }


    @GetMapping("/note/create_note")
    public String createNoteGET(@ModelAttribute("note") Note note,
                                @AuthenticationPrincipal User user,
                                Model model) {
        model.addAttribute("currentUser", user);
        return "create_note";
    }

    @PostMapping("/note/create_note")
    public String createNotePOST(@ModelAttribute Note note,
                                 @AuthenticationPrincipal User user,
                                 BindingResult result) {
        noteValidator.validate(note, result);
        if (result.hasErrors())
            return "create_note";
        note.setIdUser(user.getId());
        note.setCreated(new Date());
        noteService.addNote(note);
        return "redirect:/note/my_notes";
    }


    @GetMapping("/note/my_notes")
    public String myNotesGET(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotesByUserId(user.getId()));
        return "my_notes";
    }


    @GetMapping("/note/delete_note")
    public String deleteNoteGET(@AuthenticationPrincipal User user,
                                @RequestParam(value = "id") int id) {
        Note note = noteService.getById(id);
        //Если такой записи нет или это не запись текущего пользователя
        if (note == null || (note.getIdUser() != user.getId() && !user.isAdmin())) {
            return "redirect:/";
        }
        noteService.deleteNote(id);
        return "redirect:/note/my_notes";
    }


    @GetMapping("/note/edit_note")
    public String editNoteGET(@AuthenticationPrincipal User user,
                              @RequestParam(value = "id") int id,
                              @RequestParam(required = false) Boolean error,
                              Model model) {
        Note note = noteService.getById(id);
        //Если такой записи нет или это не запись текущего пользователя
        if (note == null || note.getIdUser() != user.getId()) {
            return "redirect:/";
        }
        if (error != null && error)
            model.addAttribute("error", true);
        model.addAttribute("currentUser", user);
        model.addAttribute("note", note);
        return "edit_note";
    }

    @PostMapping("/note/edit_note")
    public String editNotePOST(@AuthenticationPrincipal User user,
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
            return "redirect:/note/edit_note?id=" + id + "&error=true";
        }
        note.setTitle(title);
        note.setContent(content);
        noteService.updateNote(note);
        return "redirect:/note/my_notes";
    }


    @GetMapping("/my_profile")
    public String myProfileGET(@AuthenticationPrincipal User user,
                               Model model) {
        model.addAttribute("currentUser", user);
        return "my_profile";
    }

    @GetMapping("/my_profile/edit")
    public String editMyProfileGET(@AuthenticationPrincipal User user,
                                   @RequestParam(required = false, value = "errorEmail") Boolean errorEmail,
                                   @RequestParam(required = false, value = "errorName") Boolean errorName,
                                   Model model) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (errorEmail != null && errorEmail)
            model.addAttribute("errorEmail", true);
        if (errorName != null && errorName)
            model.addAttribute("errorName", true);
        model.addAttribute("currentUser", user);
        return "editMyProfile";
    }

    @PostMapping("/my_profile/edit")
    public String editMyProfilePOST(@AuthenticationPrincipal User user,
                                    @RequestParam(value = "email") String email,
                                    @RequestParam(value = "name") String name) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        User userEmail = userService.getUserByEmail(email);
        String errorEmail = "";
        if (!email.matches(User.emailRegex) || (userEmail != null && user.getId() != userEmail.getId())) {
            errorEmail += "&errorEmail=" + true;
        }
        String errorName = "";
        if (name.isEmpty()) {
            errorName += "&errorName=" + true;
        }
        if (!errorEmail.isEmpty() || !errorName.isEmpty())
            return "redirect:/my_profile/edit?" + errorEmail + errorName;

        user.setEmail(email);
        user.setName(name);
        userService.updateUser(user);
        return "redirect:/my_profile";
    }


    @GetMapping("/my_profile/editPassword")
    public String editPasswordGET(@AuthenticationPrincipal User user,
                                  @RequestParam(required = false, value = "errorCur") Boolean errorCur,
                                  @RequestParam(required = false, value = "errorNew") Boolean errorNew,
                                  Model model) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (errorCur != null && errorCur)
            model.addAttribute("errorCur", true);
        if (errorNew != null && errorNew)
            model.addAttribute("errorNew", true);
        model.addAttribute("currentUser", user);
        return "editPassword";
    }

    @PostMapping("/my_profile/editPassword")
    public String editPasswordPOST(@AuthenticationPrincipal User user,
                                   @RequestParam("curPassword") String curPassword,
                                   @RequestParam("newPassword") String newPassword,
                                   @RequestParam("repPassword") String repPassword) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (!passwordEncoder.matches(curPassword, user.getPassword()))
            return "redirect:/my_profile/editPassword?errorCur=" + true;
        if (newPassword.length() > 30 || newPassword.length() < 5 || !newPassword.equals(repPassword))
            return "redirect:/my_profile/editPassword?errorNew=" + true;
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );
        userService.updateUser(user);
        return "redirect:/my_profile";
    }
}
