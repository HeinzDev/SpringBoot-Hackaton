package com.hackaton.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.Pessoa;

public interface PessoaRepository extends CrudRepository<Pessoa,Long> {
    List<Pessoa> findByCodigoPessoa(long id);
    List<Pessoa> findByNome(String nome);
    List<Pessoa> findByLogin(String nome);
    List<Pessoa> findByStatus(long id);
    void deleteByCodigoPessoa(long id);
    
}
