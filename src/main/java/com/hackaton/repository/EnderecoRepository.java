package com.hackaton.repository;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.Endereco;

import java.util.List;

public interface EnderecoRepository extends CrudRepository<Endereco, Long> {
    List<Endereco> findByCodigoPessoa(Long codigoPessoa);

}

