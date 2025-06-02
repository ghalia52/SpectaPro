package com.example.spectapro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ARTISTE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class artiste {

    @Id
    @Column(name = "IDART")
    private int idart;

    @Column(name = "NOMART")
    private String nomart;

    @Column(name = "PRENOMART")
    private String prenomart;

    @Column(name = "SPECIALITE")
    private String specialite;
}
