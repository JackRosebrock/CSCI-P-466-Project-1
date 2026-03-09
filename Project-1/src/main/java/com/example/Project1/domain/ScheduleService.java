package com.example.Project1.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Project1.data.VideoSchedule;
import com.example.Project1.data.VideoScheduleRepository;


@Service
public class ScheduleService 
{
    private final VideoScheduleRepository repo;

    public ScheduleService(VideoScheduleRepository repo)
    {
        this.repo = repo;
    }

    // Returns a list of all a user's scheduled videos
    public List<VideoSchedule> list(String username)
    {
        return repo.findByUsernameOrderByStartTimeAsc(username);
    }


    // Get a specific video for a user
    public VideoSchedule getForUser(Long id, String username) 
    {
        return repo.findByIdAndUsername(id, username)
            .orElseThrow(() -> new IllegalArgumentException("Schedule could not be found."));
    }


    public void create(String username, String title, String videoUrl, LocalDateTime start, LocalDateTime end)
    {
        validateTimes(start, end);
        ensureNoOverlap(username, start, end, null);

        repo.save(new VideoSchedule(null, username, title, videoUrl, start, end));
    }


    // Updates the user's current 
    public void update(Long id, String username, String title, String videoUrl, LocalDateTime start, LocalDateTime end)
    {
        validateTimes(start, end);
        getForUser(id, username);
        ensureNoOverlap(username, start, end, id);

        repo.save(new VideoSchedule(id, username, title, videoUrl, start, end));
    }


    public void delete(Long id, String username)
    {
        VideoSchedule schedule = getForUser(id, username);

        repo.deleteById(id);
    }


    private void validateTimes(LocalDateTime start, LocalDateTime end)
    {
        if (start == null || end == null)
        {
            throw new IllegalArgumentException("Start time & End time must both be time variables.");
        }

        else if (!end.isAfter(start))
        {
            throw new IllegalArgumentException("End time must come after the start time.");
        }
    }


    private void ensureNoOverlap(String username, LocalDateTime start, LocalDateTime end, Long excludeId)
    {
        for (VideoSchedule entry : repo.findByUsernameOrderByStartTimeAsc(username))
        {
            if (excludeId != null && excludeId.equals(entry.id()))
                continue;

            boolean overlaps = start.isBefore(entry.endTime()) && end.isAfter(entry.startTime());

            if (overlaps)
            {
                throw new IllegalArgumentException("This video's time conflicts with another already in the list.");
            }
        }
    }
}