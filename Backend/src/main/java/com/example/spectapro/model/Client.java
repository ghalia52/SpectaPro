package com.example.spectapro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq_gen")
    @SequenceGenerator(name = "client_seq_gen", sequenceName = "SEQ_CLIENT", allocationSize = 1)
    @Column(name = "IDCLT", nullable = false)
    private Long idclt;

    @Column(name = "NOMCLT", nullable = false, length = 255)
    private String nomclt;

    @Column(name = "PRENOMCLT", nullable = false, length = 255)
    private String prenomclt;

    @Column(name = "TEL", nullable = false, length = 255)
    private String tel;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "MOTP", nullable = false, length = 255)
    private String motP;
}
