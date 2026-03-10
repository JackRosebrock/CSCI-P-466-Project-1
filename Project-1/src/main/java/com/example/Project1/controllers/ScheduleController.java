package com.example.Project1.controllers;

import java.security.Principal;

import com.example.Project1.domain.ScheduleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/schedule")
public class ScheduleController 
{
    private final ScheduleService service;

    public ScheduleController(ScheduleService service)
    {
        this.service = service;
    }


    @GetMapping
    public String list(Model model, Principal principal)
    {
        model.addAttribute("schedules", service.list(principal.getName()));

        return "schedule";
    }


    @GetMapping("/new")
    public String newForm(Model model)
    {
        model.addAttribute("form", new ScheduleForm());

        return "schedule_form";
    }


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


    @PostMapping("/{id}/delete")
    public String Delete(@PathVariable Long id, Principal principal)
    {
        service.delete(id, principal.getName());

        return "redirect:/schedule";
    }

    
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
