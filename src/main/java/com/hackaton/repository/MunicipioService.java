package com.hackaton.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Municipio;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MunicipioService {

    @Autowired
    private MunicipioRepository municipioRepository;

    public Municipio findMunicipioByCodigo(Long codigoMunicipio) {
        return municipioRepository.findById(codigoMunicipio).orElseThrow(() -> new EntityNotFoundException("Bairro não encontrado"));
    }
}