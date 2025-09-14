package com.udacity.jwdnd.course1.cloudstorage.Controller;

import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupRequest;
import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupResponse;
import com.udacity.jwdnd.course1.cloudstorage.services.AuthService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.exceptions.UsernameAlreadyTakenException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewController {
    
    private final AuthService authService;
    private final FileService fileService;
    
    public ViewController(AuthService authService, FileService fileService) {
        this.authService = authService;
        this.fileService = fileService;
    }

    @GetMapping("/signup")
    public String signupView() {
        return "signup";
    }
    
    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupRequest") SignupRequest signupRequest, 
                        RedirectAttributes redirectAttributes, 
                        Model model) {
        try {
            SignupResponse response = authService.signup(signupRequest);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/signup";
        } catch (UsernameAlreadyTakenException e) {
            model.addAttribute("errorMessage", "Username already exists!");
            return "signup";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "There was an error signing you up. Please try again.");
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }
    
    @GetMapping("/")
    public String rootView() {
        // Redirect to home for authenticated users
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String homeView(Authentication authentication, Model model) {
        model.addAttribute("files", fileService.getFiles(authentication));
        return "home";
    }
    
    @GetMapping("/result")
    public String resultView() {
        return "result";
    }
}
