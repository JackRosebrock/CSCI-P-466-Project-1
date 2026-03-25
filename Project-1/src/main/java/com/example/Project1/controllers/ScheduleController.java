package com.example.Project1.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Project1.domain.ScheduleService;

// Controller for handling web requests for the schedule page
@Controller
@RequestMapping("/schedule")
public class ScheduleController 
{
    private final ScheduleService service;

    public ScheduleController(ScheduleService service)
    {
        this.service = service;
    }


    // Returns a list of the user's scheduled videos
    @GetMapping
    public String list(Model model, Principal principal)
    {
        model.addAttribute("schedules", service.list(principal.getName()));

        return "schedule";
    }


    // Returns the form for scheduling a new video
    @GetMapping("/new")
    public String newForm(Model model)
    {
        model.addAttribute("form", new ScheduleForm());

        return "schedule_form";
    }


    // Handles submitting the form for scheduling a new video
    @PostMapping
    public String create(@ModelAttribute("form") ScheduleForm form, Model model, Principal principal)
    {
        try 
        {
            service.create(principal.getName(), form.getTitle(), form.getVideoUrl(), form.getStartTime(), form.getEndTime());

            return "redirect:/schedule";
        } 

        catch (IllegalArgumentException e) 
        {
            model.addAttribute("error", e.getMessage());
            
            return "schedule_form";
        }
    }


    // Handles deleting a video from the user's schedule
    @PostMapping("/{id}/delete")
    public String Delete(@PathVariable Long id, Principal principal)
    {
        service.delete(id, principal.getName());

        return "redirect:/schedule";
    }


    // Returns the form for editing a scheduled video
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal)
    {
        var s = service.getForUser(id, principal.getName());

        ScheduleForm form = new ScheduleForm();

        form.setId(s.id());
        form.setTitle(s.title());
        form.setVideoUrl(s.videoUrl());
        form.setStartTime(s.startTime());
        form.setEndTime(s.endTime());

        model.addAttribute("form", form);

        return "schedule_form";
    }

    
    // Handles submitting the form for editing a scheduled video
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("form") ScheduleForm form, Model model, Principal principal)
    {
        try
        {
            service.update(id, principal.getName(), form.getTitle(), form.getVideoUrl(), form.getStartTime(), form.getEndTime());

            return "redirect:/schedule";
        }

        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
            form.setId(id);

            return "schedule_form";
        }
    }
}
