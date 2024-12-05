package com.hackaton.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Endereco;
import com.hackaton.model.Pessoa;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EnderecoService enderecoService;

    public Pessoa save(Pessoa pessoa) {
        //Controller tava dando problema no inner join as vezes
        for (Endereco endereco : pessoa.getEnderecos()) {
            if (enderecoService.findBairroByCodigo(endereco.getCodigoBairro()).isEmpty()) {
                throw new EntityNotFoundException("Bairro não encontrado para o código " + endereco.getCodigoBairro());
            }
        }
        return pessoaRepository.save(pessoa);
    }

    public Pessoa findById(Long id) {
        return pessoaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
    }

    public List<Pessoa> findAll() {
        return pessoaRepository.findAll();
    }

    public void delete(Pessoa pessoa) {
        pessoaRepository.deleteById(pessoa.getCodigoPessoa());
    }
}
