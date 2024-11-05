package com.hackaton.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.Bairro;

public interface BairroRepository extends CrudRepository<Bairro, Long>{
    
    List<Bairro> findByCodigoBairro(Long codigoBairro);
    List<Bairro> findByCodigoMunicipio(Long codigoMunicipio);
    List<Bairro> findByNome(String nome);
    List<Bairro> findByStatus(Long status);
}
