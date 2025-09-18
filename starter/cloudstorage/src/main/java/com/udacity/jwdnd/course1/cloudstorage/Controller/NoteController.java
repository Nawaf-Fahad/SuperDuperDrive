package com.udacity.jwdnd.course1.cloudstorage.Controller;

import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NoteController {

    private final NoteService noteService;
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Create/Update (the modal posts noteId if editing)
    @PostMapping("/note")
    public String upsert(Authentication auth,
                         @RequestParam(value = "noteId", required = false) Long noteId,
                         @RequestParam("noteTitle") String noteTitle,
                         @RequestParam("noteDescription") String noteDescription,
                         RedirectAttributes ra) {
        noteService.upsert(auth, noteId, noteTitle, noteDescription);
        ra.addFlashAttribute("message", noteId == null ? "Note created." : "Note updated.");
        return "redirect:/home#nav-notes";
    }

    // Delete
    @PostMapping("/note/{id}/delete")
    public String delete(Authentication auth,
                         @PathVariable("id") Long id,
                         RedirectAttributes ra) {
        noteService.deleteNote(auth, id);
        ra.addFlashAttribute("message", "Note deleted.");
        ra.addFlashAttribute("messageClass", "alert-danger");
        return "redirect:/home#nav-notes";
    }
}