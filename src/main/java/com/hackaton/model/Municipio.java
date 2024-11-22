package com.hackaton.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="municipios")
@Getter
@Setter
public class Municipio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoMunicipio;
    private Long codigoUF;
    private String nome;
    private Long status;

    @ManyToOne
    @JoinColumn(name = "codigoUF", referencedColumnName = "codigoUF", insertable = false, updatable = false)
    private UF uf;
}
