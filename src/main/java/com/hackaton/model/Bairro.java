package com.hackaton.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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

    @ManyToOne
    @JoinColumn(name = "codigoMunicipio", referencedColumnName = "codigoMunicipio", insertable = false, updatable = false)
    @JsonIgnore
    private Municipio municipio;

    // Relação com Endereco (comentada, mas é importante se for necessária)
    @OneToMany(mappedBy = "bairro")
    @JsonIgnore
    private List<Endereco> enderecos;
}

