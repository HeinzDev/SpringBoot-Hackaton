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
import com.hackaton.model.Bairro;
import com.hackaton.model.Endereco;
import com.hackaton.model.Pessoa;
import com.hackaton.repository.BairroService;
import com.hackaton.repository.EnderecoService;
import com.hackaton.repository.MunicipioService;
import com.hackaton.repository.PessoaRepository;
import com.hackaton.repository.PessoaService;
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
    
    @MockBean PessoaService pessoaService;
    
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
        pessoa.setNome(null);  // simular erro

        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("O campo nome é obrigatório.")));
    }


    // Testando o endpoint PUT /pessoa com códigoPessoa não encontrado
    @Test
    public void testEditPessoa() throws Exception {

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

    // Erro inserir login que ja existe
    @Test
    void testTwoLogins() throws Exception {
        // Simular pessoa já existente com mesmo login
        when(action.findByLogin("carlos.almeida")).thenReturn(Collections.singletonList(pessoa));

        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("Já existe uma pessoa com esse login no banco de dados.")));
    }

    // Testando Retornar erro ao passar codigoEndereço na Req
    @Test
    void testCreatePessoaWithCodigoEndereco() throws Exception {
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome("Dante");
        novaPessoa.setSobrenome("Sparda");
        novaPessoa.setIdade(35L);
        novaPessoa.setLogin("dantesparda");
        novaPessoa.setSenha("dmc123123");
        novaPessoa.setStatus(1L);

        Endereco endereco = new Endereco();
        endereco.setCodigoEndereco(1L);
        endereco.setCodigoBairro(68L);
        endereco.setNomeRua("Deruvall");
        endereco.setNumero(13L);
        endereco.setComplemento("office");
        endereco.setCep("65432-987");

        novaPessoa.setEnderecos(Collections.singletonList(endereco));

        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("Não é permitido passar o código de endereço ao criar um novo endereço.")));
    }

    // Create Pessoa
    @Test
    public void testCreatePessoaSuccess() throws Exception {
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome("Dante");
        novaPessoa.setSobrenome("Sparda");
        novaPessoa.setIdade(35L);
        novaPessoa.setLogin("dantesparda");
        novaPessoa.setSenha("dmc123123");
        novaPessoa.setStatus(1L);
    
        Endereco endereco = new Endereco();
        endereco.setCodigoBairro(68L);
        endereco.setNomeRua("Deruvall");
        endereco.setNumero(13L);
        endereco.setComplemento("office");
        endereco.setCep("65432-987");
    
        novaPessoa.setEnderecos(Collections.singletonList(endereco));
    
        Bairro bairroMock = new Bairro();
        bairroMock.setNome("Bairro Teste");
        bairroMock.setCodigoBairro(68L);
    
        when(bairroService.findBairroByCodigo(68L)).thenReturn(bairroMock); // Mock da lista
    
        when(pessoaService.save(any(Pessoa.class))).thenReturn(novaPessoa);
        when(enderecoService.save(any(Endereco.class))).thenReturn(endereco);
    
        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPessoa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dante")))
                .andExpect(jsonPath("$.sobrenome", is("Sparda")))
                .andExpect(jsonPath("$.enderecos[0].nomeRua", is("Deruvall")));
    }
    
    // Not Found
    @Test
    public void testCreatePessoaButBairroEmpty() throws Exception {
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome("Dante");
        novaPessoa.setSobrenome("Sparda");
        novaPessoa.setIdade(35L);
        novaPessoa.setLogin("dantesparda");
        novaPessoa.setSenha("dmc123123");
        novaPessoa.setStatus(1L);

        Endereco endereco = new Endereco();
        endereco.setCodigoBairro(999L);
        endereco.setNomeRua("Deruvall");
        endereco.setNumero(13L);
        endereco.setComplemento("office");
        endereco.setCep("65432-987");

        novaPessoa.setEnderecos(Collections.singletonList(endereco));

        when(enderecoService.findBairroByCodigo(999L)).thenReturn(Collections.emptyList()); // < Lista vazia


        mockMvc.perform(post("/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPessoa)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("Bairro não encontrado para o código 999")));
    }
}