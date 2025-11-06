package com.chanchopeludo.ChanchoPeludoBot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id_user")
    private String idUser;

    @NotBlank
    private String username;

    @Column(name = "profile_image_url")
    private String profile_image_url;
}
