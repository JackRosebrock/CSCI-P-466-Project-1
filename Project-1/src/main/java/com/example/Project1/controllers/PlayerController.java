package com.example.Project1.controllers;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Project1.data.VideoSchedule;
import com.example.Project1.domain.ScheduleService;
import com.example.Project1.util.YouTubeUtil;


@Controller
public class PlayerController 
{
    private final ScheduleService scheduleService;

    public PlayerController(ScheduleService scheduleService)
    {
        this.scheduleService = scheduleService;
    }


    @GetMapping("/player")
    public String player(@RequestParam(name = "id", required = false) Long id, Model model, Principal principal)
    {
        String username = principal.getName();
        LocalDateTime currentTime = LocalDateTime.now();

        // If an ID is given, play their current schedule, only if it is due
        Optional<VideoSchedule> scheduleOpt;
        if (id != null)
        {
            scheduleOpt = scheduleService.findPlayableById(username, id, currentTime);
        }

        else
        {
            scheduleOpt = scheduleService.findCurrentlyDue(username, currentTime);
        }

        // If no videos are currently due, show next scheduled video (if one exists)
        if (scheduleOpt.isEmpty())
        {
            model.addAttribute("next", scheduleService.findNextScheduled(username, currentTime).orElse(null));

            return "player";
        }

        VideoSchedule schedule = scheduleOpt.get();

        // Skip forward if the video is started late
        long offsetSeconds = Duration.between(schedule.startTime(), currentTime).getSeconds();

        if (offsetSeconds < 0)
        {
            offsetSeconds = 0;
        }


        // Set the offset time to the video's duration if the video's scheduled time was missed
        long videoSeconds = Duration.between(schedule.startTime(), schedule.endTime()).getSeconds();

        if (videoSeconds > 0 && offsetSeconds > videoSeconds)
        {
            offsetSeconds = videoSeconds;
        }

        String videoId = YouTubeUtil.extractVideoId(schedule.videoUrl());

        model.addAttribute("schedule", schedule);
        model.addAttribute("videoId", videoId);
        model.addAttribute("startSeconds", offsetSeconds);

        return "player";
    }
}
