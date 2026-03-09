package com.example.Project1.data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

// The main layout for the database (includes the schema, and variable types)

@Table(name = "video_schedule")
public record VideoSchedule
(
    @Id Long id,
    String username,
    String title,

    @Column("video_url") String videoUrl,
    @Column("start_time") LocalDateTime startTime,
    @Column("end_time") LocalDateTime endTime
) {}