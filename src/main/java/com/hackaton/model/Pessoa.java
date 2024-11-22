package com.hackaton.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pessoas")
@Getter
@Setter
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoPessoa;

    private String nome;
    private String sobrenome;
    private Long idade;
    private String login;
    private String senha;
    private Long status;

    @OneToMany
    private List<Endereco> enderecos;


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
}
