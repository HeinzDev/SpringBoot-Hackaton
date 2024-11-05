package com.hackaton.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bairros")
@Getter
@Setter
public class Bairro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoBairro;
    private Long codigoMunicipio;
    private String nome;
    private Long status;
}
