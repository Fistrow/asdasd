package com.chanchopeludo.ChanchoPeludoBot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "servers")
public class ServerEntity {

    @Id
    @Column(name = "id_server")
    private String idServer;

    @NotBlank
    @Column(name = "guild_name")
    private String guild_name;

    @NotBlank
    @Size(max = 5, min = 2)
    private String prefix;

}
