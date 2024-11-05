package com.hackaton.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.UF;

public interface UFRepository extends CrudRepository<UF, Long> {
    Iterable<UF> findByStatus(Long status);
    List<UF> findByCodigoUF(long codigoUF);
    List<UF> findByNome(String nome);
    List<UF> findBySigla(String sigla);
}
