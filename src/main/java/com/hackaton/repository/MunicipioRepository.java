package com.hackaton.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.Municipio;

public interface MunicipioRepository extends CrudRepository<Municipio, Long> {
    List<Municipio> findByCodigoMunicipio(Long codigoMunicipio);
    List<Municipio> findByNome(String nome);
    List<Municipio> findByCodigoUF(Long codigoUF);
    List<Municipio> findByStatus(Long status);
}
