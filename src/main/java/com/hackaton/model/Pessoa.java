package com.hackaton.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PESSOAS")
@Getter
@Setter
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO_PESSOA")
    private Long codigoPessoa;

    private String nome;
    private String sobrenome;
    private Long idade;
    private String login;
    private String senha;
    private Long status;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Endereco> enderecos;
}

    // "login": "marina.barbosa.dois",
    // "senha": "senha",
    // "status": 1,
    // "enderecos": [
    //     {
    //         "codigoEndereco": 41,
    //         "codigoPessoa": 15,
    //         "codigoBairro": 4,
    //         "nomeRua": "ADICIONADO",
    //         "numero": "456",
    //         "complemento": "COMPLEMENTO 1",
    //         "cep": "33333-680",
    //         "bairro": {
    //             "codigoBairro": 4,
    //             "codigoMunicipio": 2,
    //             "nome": "CENTRO DE VILA VELHA",
    //             "status": 1,
    //             "municipio": {
    //                 "codigoMunicipio": 2,
    //                 "codigoUF": 1,
    //                 "nome": "VILA VELHA",
    //                 "status": 1,
    //                 "uf": {
    //                     "codigoUF": 1,
    //                     "sigla": "ES",
    //                     "nome": "ESPÍRITO SANTO",
    //                     "status": 1
    //                 }
    //             }
    //         }
    //     },

