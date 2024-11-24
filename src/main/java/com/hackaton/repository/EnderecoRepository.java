package com.hackaton.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackaton.model.Endereco;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByCodigoPessoa(Long codigoPessoa);

}

