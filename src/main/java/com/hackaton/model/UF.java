package com.hackaton.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "uf")
@Getter
@Setter
public class UF {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoUF;
    private String sigla;
    private String nome;
    private Long status;
}
