package com.hackaton.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enderecos")
@Getter
@Setter
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO_ENDERECO")
    private Long codigoEndereco;

    @Column(name = "CODIGO_PESSOA")
    private Long codigoPessoa;

    @Column(name = "CODIGO_BAIRRO")
    private Long codigoBairro;

    @Column(name = "NOME_RUA")
    private String nomeRua;

    @Column(name = "NUMERO")
    private Long numero;

    @Column(name = "COMPLEMENTO")
    private String complemento;

    @Column(name = "CEP")
    private String cep;

    @ManyToOne
    @JoinColumn(name = "CODIGO_PESSOA", referencedColumnName = "CODIGO_PESSOA", insertable = false, updatable = false)
    @JsonBackReference
    private Pessoa pessoa;

    //REMOVI POR QUE TA DANDO PROBLEMA
    @ManyToOne(fetch = FetchType.LAZY)  // Carregar Bairro junto com o Endereco
    @JoinColumn(name = "CODIGO_BAIRRO", insertable=false, updatable=false)
    private Bairro bairro;
    
}
    //         "codigoEndereco": 41,
    //         "codigoPessoa": 15,
    //         "codigoBairro": 4,
    //         "nomeRua": "ADICIONADO",
    //         "numero": "456",
    //         "complemento": "COMPLEMENTO 1",
    //         "cep": "33333-680",

