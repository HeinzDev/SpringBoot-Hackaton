package com.hackaton.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Endereco;

@Service
public class EnderecoService {
    
    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Endereco> findByPessoaId(Long pessoaId) {
        return enderecoRepository.findByCodigoPessoa(pessoaId);
    }

    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }
}

