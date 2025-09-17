package com.udacity.jwdnd.course1.cloudstorage.Controller;

import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    // Create/Update (the modal posts credentialId if editing)
    @PostMapping("/credential")
    public String upsert(Authentication auth,
                         @RequestParam(value = "credentialId", required = false) Long credentialId,
                         @RequestParam("url") String url,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password,
                         RedirectAttributes ra) {
        credentialService.upsert(auth, credentialId, url, username, password);
        ra.addFlashAttribute("message", credentialId == null ? "Credential created." : "Credential updated.");
        return "redirect:/home#nav-credentials";
    }

    // Delete
    @PostMapping("/credential/{id}/delete")
    public String delete(Authentication auth,
                         @PathVariable("id") Long id,
                         RedirectAttributes ra) {
        credentialService.delete(auth, id);
        ra.addFlashAttribute("message", "Credential deleted.");
        return "redirect:/home#nav-credentials";
    }

    // Fetch decrypted password for editing (owner-only)
    @GetMapping("/credential/{id}/decrypted")
    @ResponseBody
    public String getDecrypted(Authentication auth, @PathVariable("id") Long id) {
        return credentialService.getDecryptedPassword(auth, id);
    }
}
