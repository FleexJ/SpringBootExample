package com.example.springboot.controller;

import com.example.springboot.entity.Note;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final NoteService noteService;

    @Autowired
    public AdminController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @GetMapping("/users")
    public String adminUsers(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("users", userService.getAll());
        return "adminUsers";
    }

    @GetMapping("/users/delete")
    public String adminUsersDelete(@AuthenticationPrincipal User user,
                                   @RequestParam("id") int id) {
        User userId = userService.getById(id);
        if (userId == null)
            return "redirect:/admin/users";

        userService.deleteById(id);
        System.out.println("User: " + user.getEmail() + " deleted user: " + userId.getEmail());
        if (user.getId() == id) {
            return "redirect:/logout";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/note/delete")
    public String adminNoteDelete(@AuthenticationPrincipal User user,
                                  @RequestParam("id") int id) {
        Note note = noteService.getById(id);
        if (note == null)
            return "redirect:/";

        noteService.deleteNote(id);
        System.out.println("User: " + user.getEmail() + " deleted note id: " + id + " idUser: " + note.getIdUser());
        return "redirect:/";
    }

    @GetMapping("/users/do_admin")
    public String adminUsersDoAdmin(@AuthenticationPrincipal User user,
                                    @RequestParam("id") int id) {
        User userEdit = userService.getById(id);
        if (userEdit == null)
            return "redirect:/admin/users";

        userEdit.setRole(User.ROLE_ADMIN);
        userService.updateUser(user);
        System.out.println("User: " + user.getEmail() + " give role admin to user: " + userEdit.getEmail());
        return "redirect:/admin/users";
    }
}
