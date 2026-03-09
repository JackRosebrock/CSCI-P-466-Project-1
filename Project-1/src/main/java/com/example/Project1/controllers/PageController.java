package com.example.Project1.controllers;


import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController 
{
    
    @GetMapping("/")
    public String root(Principal principal)
    {
        return (principal != null) ? "redirect:/home" : "redirect:/login";
    }


    @GetMapping("/login")
    public String login()
    {
        return "login";
    }


    @GetMapping("/home")
    public String home(Model model, Principal principal)
    {
        model.addAttribute("username", principal.getName());
        return "home";
    }

    @GetMapping("/schedule")
    public String schedule()
    {
        return "schedule";
    }

    @GetMapping("/videoButton")
    public String videoButton()
    {
        return "videoButton";
    }
}
