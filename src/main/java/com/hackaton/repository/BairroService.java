package com.hackaton.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Bairro;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BairroService {
    @Autowired
    private BairroRepository bairroRepository;

    public Bairro findBairroByCodigo(Long codigoBairro) {
        return bairroRepository.findById(codigoBairro).orElseThrow(() -> new EntityNotFoundException("Bairro não encontrado"));
    }
}
