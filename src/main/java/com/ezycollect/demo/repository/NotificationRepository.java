package com.ezycollect.demo.repository;

import com.ezycollect.demo.dto.NotificationDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<NotificationDTO, Integer> {
    @Query("SELECT n FROM Notification n WHERE n.id > ?1")
    List<NotificationDTO> findUnprocessed(int latestKnownId);
}
