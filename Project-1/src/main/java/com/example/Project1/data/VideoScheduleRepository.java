package com.example.Project1.data;

import java.util.List;
import java.util.Optional;
import org.komamitsu.spring.data.sqlite.SqliteRepository;

// The main database repository interface

public interface VideoScheduleRepository extends SqliteRepository<VideoSchedule, Long> 
{
    // Function to retrieve the list of the user's scheduled videos
    List<VideoSchedule> findByUsernameOrderByStartTimeAsc(String username);
    
    Optional<VideoSchedule> findByIdAndUsername(Long id, String username);
}
