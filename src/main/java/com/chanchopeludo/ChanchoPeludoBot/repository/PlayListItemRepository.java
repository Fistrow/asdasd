package com.chanchopeludo.ChanchoPeludoBot.repository;

import com.chanchopeludo.ChanchoPeludoBot.model.PlayListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayListItemRepository extends JpaRepository<PlayListItemEntity, Long> {
}
