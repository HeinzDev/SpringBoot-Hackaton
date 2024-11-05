package com.hackaton.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.UF;

public interface UFRepository extends CrudRepository<UF, Long> {
    Iterable<UF> findByStatus(int status);
    List<UF> findByCodigoUF(long codigoUF);
    List<UF> findByNome(String nome);
    List<UF> findBySigla(String sigla);
    List<UF> findByNomeAndStatus(String nome, int status);
    List<UF> findByNomeAndCodigoUF(String nome, Long codigoUF);
    List<UF> findByStatusAndCodigoUF(int status, Long codigoUF);
    List<UF> findByNomeAndStatusAndCodigoUF(String nome, int status, Long codigoUF);
    Optional<UF> findByNomeAndStatusAndCodigoUFAndSigla(String nome, int status, Long codigoUF, String sigla);
}
