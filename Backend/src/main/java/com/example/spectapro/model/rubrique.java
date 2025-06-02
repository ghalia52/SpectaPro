package com.example.spectapro.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "RUBRIQUE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class rubrique {

    @Id
    @Column(name = "IDRUB")
    private Long idrub;  // Change to Long for better compatibility

    @ManyToOne
    @JoinColumn(name = "IDSPEC")
    private Spectacle spectacle;

    @ManyToOne
    @JoinColumn(name = "IDART")
    private artiste artiste;

    @Column(name = "H_DEBUTR")
    private Double hDebutr;  // Consider using Double for more precision

    @Column(name = "DUREERUB")
    private Double dureerub;  // Consider using Double for more precision

    @Column(name = "TYPE")
    private String type;
}
