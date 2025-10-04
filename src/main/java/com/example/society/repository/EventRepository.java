package com.example.society.repository;

import com.example.society.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;



import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByEventDateAsc();
    List<Event> findAllByEventDateBetween(LocalDateTime start, LocalDateTime end);
    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
}
