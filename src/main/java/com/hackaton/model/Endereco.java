package com.hackaton.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long cep;

    //REMOVI POR QUE TA DANDO PROBLEMA
    // @ManyToOne
    // @JoinColumn(name = "codigo_bairro", referencedColumnName = "codigo_bairro", insertable = false, updatable = false)
    // private Bairro bairro;
    
}
    //         "codigoEndereco": 41,
    //         "codigoPessoa": 15,
    //         "codigoBairro": 4,
    //         "nomeRua": "ADICIONADO",
    //         "numero": "456",
    //         "complemento": "COMPLEMENTO 1",
    //         "cep": "33333-680",

