package com.chanchopeludo.ChanchoPeludoBot.repository;

import com.chanchopeludo.ChanchoPeludoBot.model.PlayListEntity;
import com.chanchopeludo.ChanchoPeludoBot.model.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayListEntity, Long> {

    Optional<PlayListEntity> findByNameAndServer(String name, ServerEntity server);
}
