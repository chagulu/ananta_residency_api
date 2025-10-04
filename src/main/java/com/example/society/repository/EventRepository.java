package com.example.society.repository;

import com.example.society.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // All events ordered by date
    List<Event> findAllByOrderByEventDateAsc();

    // Events in a specific date range
    List<Event> findAllByEventDateBetween(LocalDateTime start, LocalDateTime end);
    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end);

    // Top 2 upcoming events overall
    List<Event> findTop2ByEventDateAfterOrderByEventDateAsc(LocalDateTime now);

    // ✅ Top 2 upcoming events for a specific building (including events for all buildings)
    @Query("SELECT e FROM Event e WHERE e.eventDate > :now AND (e.buildingNumber = :buildingNumber OR e.buildingNumber IS NULL) ORDER BY e.eventDate ASC")
    List<Event> findTop2UpcomingEventsForBuilding(@Param("now") LocalDateTime now, @Param("buildingNumber") String buildingNumber);
}
