package com.hackaton.hackaton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.model.Pessoa;
import com.hackaton.repository.BairroService;
import com.hackaton.repository.EnderecoService;
import com.hackaton.repository.MunicipioService;
import com.hackaton.repository.PessoaRepository;
import com.hackaton.repository.UFService;

@SpringBootTest
@AutoConfigureMockMvc
public class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PessoaRepository action;
    
    @MockBean
    private EnderecoService enderecoService;
    
    @MockBean
    private BairroService bairroService;
    
    @MockBean
    private UFService ufService;
    
    @MockBean
    private MunicipioService municipioService;

    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        // Criando o Mock de Pessoa
        pessoa = new Pessoa();
        pessoa.setCodigoPessoa(1L);
        pessoa.setNome("Carlos");
        pessoa.setSobrenome("Almeida");
        pessoa.setIdade(30L);
        pessoa.setLogin("carlos.almeida");
        pessoa.setSenha("senha@123");
        pessoa.setStatus(1L);
    }

    // Testando o endpoint GET /pessoa com parametro de login
    @Test
    void testGetPessoaByLogin() throws Exception {
        when(action.findByLogin("carlos.almeida")).thenReturn(Collections.singletonList(pessoa));

        mockMvc.perform(get("/pessoa").param("login", "carlos.almeida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos")))
                .andExpect(jsonPath("$.sobrenome", is("Almeida")))
                .andExpect(jsonPath("$.status", is(1)));
    }

    // Testando o endpoint GET /pessoa com parametro de status
    @Test
    void testGetPessoaByStatus() throws Exception {
        when(action.findByStatus(1L)).thenReturn(Collections.singletonList(pessoa));

        mockMvc.perform(get("/pessoa").param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Carlos")))
                .andExpect(jsonPath("$[0].status", is(1)));
    }

    // Testando o endpoint GET /pessoa com parâmetros múltiplos
    @Test
    void testGetPessoaByMultipleParams() throws Exception {
        when(action.findByLogin("carlos.almeida")).thenReturn(Collections.singletonList(pessoa));
        when(action.findByStatus(1L)).thenReturn(Collections.singletonList(pessoa));

        mockMvc.perform(get("/pessoa")
                .param("login", "carlos.almeida")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos")))
                .andExpect(jsonPath("$.sobrenome", is("Almeida")))
                .andExpect(jsonPath("$.status", is(1)));
    }

    // Testando o endpoint POST /pessoa
    @Test
    void testCreatePessoa() throws Exception {
        // Mock para salvar pessoa
        when(action.save(any(Pessoa.class))).thenReturn(pessoa);

        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos")))
                .andExpect(jsonPath("$.sobrenome", is("Almeida")))
                .andExpect(jsonPath("$.status", is(1)));
    }

    // Testando o endpoint POST /pessoa com campos obrigatórios ausentes
    @Test
    void testCreatePessoaMissingRequiredFields() throws Exception {
        pessoa.setNome(null);  // Remover o nome para simular erro

        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("O campo nome é obrigatório.")));
    }


    // Testando o endpoint PUT /pessoa com códigoPessoa não encontrado
    @Test
    public void testEditPessoa() throws Exception {
        
        // Criando o mock de uma pessoa
        Pessoa mockPessoa = new Pessoa();
        mockPessoa.setCodigoPessoa(29L);
        mockPessoa.setNome("Carlos");
        mockPessoa.setSobrenome("Silva");
        mockPessoa.setIdade(25L);
        mockPessoa.setLogin("Carlos.silva");
        mockPessoa.setSenha("senha@321");
        mockPessoa.setStatus(1L);
        when(action.findByCodigoPessoa(29L)).thenReturn(Collections.singletonList(mockPessoa));
        when(action.save(any(Pessoa.class))).thenReturn(mockPessoa);
        
        // Dados de entrada para atualizar a pessoa
        String pessoaJson = "{"
            + "\"codigoPessoa\": 29,"
            + "\"nome\": \"Carlos Editado\","
            + "\"sobrenome\": \"Silva\","
            + "\"idade\": 25,"
            + "\"login\": \"Carlos.silva\","
            + "\"senha\": \"senha@321\","
            + "\"status\": 1,"
            + "\"enderecos\": null"
            + "}";
    
        when(action.findAll()).thenReturn(Collections.singletonList(mockPessoa));
    
        mockMvc.perform(put("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pessoaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome", is("Carlos Editado")));
    }
    
    // Testando o endpoint PUT /pessoa com campos obrigatórios ausentes
    @Test
    void testEditPessoaMissingFields() throws Exception {
        pessoa.setNome(null);

        mockMvc.perform(put("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("O campo nome é obrigatório")));
    }
}