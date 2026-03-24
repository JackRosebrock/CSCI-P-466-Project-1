package com.example.Project1.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WebPerformanceTests 
{
    @Autowired
    MockMvc mvc;

    private static final Duration MAX_PER_REQUEST = Duration.ofSeconds(2);

    // Response Time Test (< 2 seconds)
    @Test
    void homeResponseUnder2Seconds() throws Exception
    {
        mvc.perform(get("/home").with(user("student").roles("USER")))
            .andExpect(status().isOk());
        
        long buffer = System.nanoTime();
            
        mvc.perform(get("/home").with(user("student").roles("USER")))
            .andExpect(status().isOk());
        
        long final_time = Duration.ofNanos(System.nanoTime() - buffer).toMillis();

        assertThat(final_time).isLessThan(MAX_PER_REQUEST.toMillis());
    }


    // Load Time Test (< 2 seconds)
    @Test
    void schedulePageLoadUnder2Seconds() throws Exception
    {
        mvc.perform(get("/schedule").with(user("student").roles("USER")))
            .andExpect(status().isOk());
        
        long buffer = System.nanoTime();
        
        mvc.perform(get("/schedule").with(user("student").roles("USER")))
            .andExpect(status().isOk());
        
        long final_time = Duration.ofNanos(System.nanoTime() - buffer).toMillis();

        assertThat(final_time).isLessThan(MAX_PER_REQUEST.toMillis());
    }


    // 1000 Concurrent Users Test
    @Test
    @Timeout(30)
    void oneThousandConcurrentUsers() throws Exception
    {
        final int requests = 1000;
        final int threads = 1000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>(requests);

        for (int i = 0; i < requests; i++)
        {
            futures.add(pool.submit(() -> {
                startGate.await();
                mvc.perform(get("/home").with(user("student").roles("USER")))                    
                    .andExpect(status().isOk());
                
                return null;
            }));
        }

        long buffer = System.nanoTime();
        startGate.countDown();

        for (Future<Void> f : futures)
        {
            f.get(10, TimeUnit.SECONDS);
        }

        long final_time = Duration.ofNanos(System.nanoTime() - buffer).toMillis();
        
        pool.shutdownNow();

        assertThat(final_time).isLessThan(10000);
    }
}