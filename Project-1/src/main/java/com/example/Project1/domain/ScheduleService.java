package com.example.Project1.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    // Get a specific video for a user, by its video id
    public VideoSchedule getForUser(Long id, String username) 
    {
        return repo.findByIdAndUsername(id, username)
            .orElseThrow(() -> new IllegalArgumentException("Schedule could not be found."));
    }


    // Creates a new video for the user's schedule
    public void create(String username, String title, String videoUrl, LocalDateTime start, LocalDateTime end)
    {
        validateTimes(start, end);
        ensureNoOverlap(username, start, end, null);

        repo.save(new VideoSchedule(null, username, title, videoUrl, start, end));
    }


    // Updates the user's current schedule
    public void update(Long id, String username, String title, String videoUrl, LocalDateTime start, LocalDateTime end)
    {
        validateTimes(start, end);
        getForUser(id, username);
        ensureNoOverlap(username, start, end, id);

        repo.save(new VideoSchedule(id, username, title, videoUrl, start, end));
    }


    // Deletes a video from the user's schedule
    public void delete(Long id, String username)
    {
        VideoSchedule schedule = getForUser(id, username);

        repo.deleteById(id);
    }

    // Find the currently due video
    public Optional<VideoSchedule> findCurrentlyDue(String username, LocalDateTime now)
    {
        return repo.findByUsernameOrderByStartTimeAsc(username).stream()
            .filter(s -> !s.startTime().isAfter(now) && s.endTime().isAfter(now))
            .findFirst();
    }

    // Find the next scheduled video
    public Optional<VideoSchedule> findNextScheduled(String username, LocalDateTime now)
    {
        return repo.findByUsernameOrderByStartTimeAsc(username).stream()
            .filter(s -> s.startTime().isAfter(now))
            .findFirst();
    }

    // The video should only be playable if it belongs to the user AND is currently due

    public Optional<VideoSchedule> findPlayableById(String username, Long id, LocalDateTime now)
    {
        return repo.findByIdAndUsername(id, username)
            .filter(s -> !s.startTime().isAfter(now) && s.endTime().isAfter(now));
    }

    // Validates the start and end times don't overlap each other
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


    // Validates that the new video doesn't overlap with any of the user's scheduled videos
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