package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final NoteService noteService;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public AdminController(UserService userService, NoteService noteService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.noteService = noteService;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/users")
    public String adminUsers(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("users", userService.getAll());
        return "/admin/adminUsers";
    }

//    @GetMapping("/users/delete")
//    public String adminUsersDelete(@AuthenticationPrincipal User user,
//                                   @RequestParam("id") int id) {
//        User userId = userService.getById(id);
//        if (userId == null || userId.isAdmin())
//            return "redirect:/admin/users";
//
//        try {
//            userService.deleteById(id);
//            //todo delete session
//        } catch (Exception ignored) {}
//        if (user.getId() == id) {
//            return "redirect:/logout";
//        }
//        return "redirect:/admin/users";
//    }

    @GetMapping("/note/delete")
    public String adminNoteDelete(@RequestParam("id") int id) {
        Note note = noteService.getById(id);
        if (note == null)
            return "redirect:/";

        try {
            noteService.deleteNote(id);
        } catch (Exception ignored) {}
        return "redirect:/";
    }

    @GetMapping("/users/do_admin")
    public String adminUsersDoAdmin(@RequestParam("id") int id) {
        User userEdit = userService.getById(id);
        if (userEdit == null)
            return "redirect:/admin/users";

        userEdit.setRole(User.ROLE_ADMIN);
        userService.updateUser(userEdit);
        //todo update role in his session
        return "redirect:/admin/users";
    }

    @GetMapping("/users/profile")
    public String usersProfile(@AuthenticationPrincipal User user,
                               @RequestParam("id") int id,
                               Model model) {
        User userId = userService.getById(id);
        if (userId == null)
            return "redirect:/admin/users";

        model.addAttribute("currentUser", user);
        model.addAttribute("user", userId);

        return "admin/profile";
    }

    @GetMapping("/note/edit")
    public String noteEdit(@AuthenticationPrincipal User user,
                           @RequestParam("id") int id,
                           Model model) {
        Note note = noteService.getById(id);
        if (note == null)
            return "redirect:/";
        model.addAttribute("currentUser", user);
        model.addAttribute("note", note);
        return "admin/adminEditNote";
    }

    @PatchMapping("/note/edit")
    public String noteEditPATCH(@AuthenticationPrincipal User user,
                                @RequestParam("title") String title,
                                @RequestParam("content") String content,
                                @RequestParam("id") int id) {
        Note note = noteService.getById(id);
        if (note == null)
            return "redirect:/";

        note.setTitle(title);
        note.setContent(content);
        try {
            noteService.updateNote(note);
        } catch (Exception ignored) {}
        return "redirect:/";
    }
}
