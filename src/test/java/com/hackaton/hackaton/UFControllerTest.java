package com.hackaton.hackaton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.model.UF;
import com.hackaton.repository.UFRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UFControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private UFRepository ufRepository;

    private UF uf;

    @BeforeEach
    void setUp() {
        uf = new UF();
        uf.setCodigoUF(1L);
        uf.setNome("São Paulo");
        uf.setSigla("SP");
        uf.setStatus(1L);
    }

    //Deve retornar um objeto isolado pelo parâmetro "codigoUF"
    @Test
    void testGetUFByParams() throws Exception {
        Mockito.when(ufRepository.findByCodigoUF(1L)).thenReturn(Collections.singletonList(uf));

        mockMvc.perform(get("/uf").param("codigoUF", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoUF").value(uf.getCodigoUF()))
                .andExpect(jsonPath("$.nome").value(uf.getNome()))
                .andExpect(jsonPath("$.sigla").value(uf.getSigla()))
                .andExpect(jsonPath("$.status").value(uf.getStatus()));
    }

    //Deve retornar um objeto isolado ao pesquisar multiplos parâmetros
    @Test
    public void testGetUFByMultipleParams() throws Exception {
        UF uf = new UF();
        uf.setCodigoUF(1L);
        uf.setSigla("SP");
        uf.setNome("São Paulo");
        uf.setStatus(1L);
    
        when(ufRepository.findByNome("São Paulo")).thenReturn(Collections.singletonList(uf));
        when(ufRepository.findByStatus(1L)).thenReturn(Collections.singletonList(uf));
        when(ufRepository.findByCodigoUF(1L)).thenReturn(Collections.singletonList(uf));
        when(ufRepository.findBySigla("SP")).thenReturn(Collections.singletonList(uf));
    
        mockMvc.perform(get("/uf")
                .param("nome", "São Paulo")
                .param("status", "1")
                .param("codigoUF", "1")
                .param("sigla", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoUF", is(1)))
                .andExpect(jsonPath("$.sigla", is("SP")))
                .andExpect(jsonPath("$.nome", is("São Paulo")))
                .andExpect(jsonPath("$.status", is(1)));
    }
    
    //Deve retornar um array com vários objetos ao fazer uma requisição por status
    @Test
    public void testGetUFListByStatus() throws Exception {
        UF uf1 = new UF();
        uf1.setCodigoUF(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");
        uf1.setStatus(1L);

        UF uf2 = new UF();
        uf2.setCodigoUF(2L);
        uf2.setSigla("RJ");
        uf2.setNome("Rio de Janeiro");
        uf2.setStatus(1L);

        List<UF> ufs = Arrays.asList(uf1, uf2);

        when(ufRepository.findByStatus(1L)).thenReturn(ufs);

        mockMvc.perform(get("/uf")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigoUF", is(1)))
                .andExpect(jsonPath("$[0].sigla", is("SP")))
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[0].status", is(1)))

                .andExpect(jsonPath("$[1].codigoUF", is(2)))
                .andExpect(jsonPath("$[1].sigla", is("RJ")))
                .andExpect(jsonPath("$[1].nome", is("Rio de Janeiro")))
                .andExpect(jsonPath("$[1].status", is(1)));
    }


    //Deve retornar um array vazio caso não tenha parâmetros
    @Test
    public void testGetUFEmptyWhithoutParams() throws Exception {
        when(ufRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/uf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetUFByParams_invalidCodigoUF() throws Exception {
        mockMvc.perform(get("/uf")
                .param("codigoUF", "invalid")
                .param("nome", "Sao Paulo"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para codigoUF não é um número válido.")));
    }

    @Test
    public void testGetUFByParams_invalidStatus() throws Exception {
        mockMvc.perform(get("/uf")
                .param("status", "notANumber")
                .param("nome", "Sao Paulo"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para status não é um número válido.")));
    }


    //Deve Criar uma nova UF
    @Test
    public void testCreateUF() throws Exception {
        UF uf = new UF();
        uf.setCodigoUF(1L);
        uf.setSigla("SP");
        uf.setNome("São Paulo");
        uf.setStatus(1L);

        when(ufRepository.save(any(UF.class))).thenReturn(uf);
        when(ufRepository.findAll()).thenReturn(Collections.singletonList(uf));

        mockMvc.perform(post("/uf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoUF", is(1)))
                .andExpect(jsonPath("$[0].sigla", is("SP")))
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[0].status", is(1)));
    }

    // Deve retornar erro quando criar uma UF com parâmetros insuficientes
    @Test
    public void testPostUFByInvalidParams() throws Exception {
        UF uf = new UF();
        uf.setNome("São Paulo");
        uf.setStatus(1L);
    
        mockMvc.perform(post("/uf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uf)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("Campo sigla é obrigatório")));
    }

    //Deve retornar Erro ao repetir Nome
    @Test
    public void testPostUFByInvalidNome() throws Exception {
        UF uf1 = new UF();
        uf1.setCodigoUF(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");
        uf1.setStatus(1L);

        when(ufRepository.findByNome(anyString())).thenReturn(Collections.emptyList());
        when(ufRepository.findByNome("São Paulo")).thenReturn(Collections.singletonList(uf1));

        UF uf2 = new UF();
        uf2.setCodigoUF(2L);
        uf2.setSigla("RJ");
        uf2.setNome("São Paulo");
        uf2.setStatus(1L);

        mockMvc.perform(post("/uf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uf2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("Já existe uma UF com esse nome no banco de dados.")));
    }

    

    //Deve retornar Erro ao repetir Sigla
    @Test
    public void testPostUFByInvalidSigla() throws Exception {
        UF uf1 = new UF();
        uf1.setCodigoUF(1L);
        uf1.setSigla("SP");
        uf1.setNome("São Paulo");
        uf1.setStatus(1L);

        // Simulando a existência de um UF com a mesma sigla
        when(ufRepository.findByNome(anyString())).thenReturn(Collections.emptyList());
        when(ufRepository.findBySigla("SP")).thenReturn(Collections.singletonList(uf1));

        UF uf2 = new UF();
        uf2.setCodigoUF(2L);
        uf2.setSigla("SP");
        uf2.setNome("Rio de Janeiro");
        uf2.setStatus(1L);

        mockMvc.perform(post("/uf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uf2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("Já existe uma UF com essa sigla no banco de dados.")));
    }

    //Deve conseguir editar UF e retornar um Array com todos os estados.
    @Test
    public void testEditUF() throws Exception {
        UF uf = new UF();
        uf.setCodigoUF(1L);
        uf.setSigla("RJ");
        uf.setNome("Rio de Janeiro");
        uf.setStatus(1L);

        when(ufRepository.save(any(UF.class))).thenReturn(uf);
        when(ufRepository.findByCodigoUF(1L)).thenReturn(Collections.singletonList(uf));
        when(ufRepository.findAll()).thenReturn(Collections.singletonList(uf));

        mockMvc.perform(put("/uf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoUF", is(1)))
                .andExpect(jsonPath("$[0].sigla", is("RJ")))
                .andExpect(jsonPath("$[0].nome", is("Rio de Janeiro")))
                .andExpect(jsonPath("$[0].status", is(1)));
    }

    //Deve conseguir apagar UF
    @Test
    void testDeleteUF() throws Exception {
        Mockito.when(ufRepository.findByCodigoUF(1L)).thenReturn(Collections.singletonList(uf));
        uf.setStatus(2L);
        Mockito.when(ufRepository.save(Mockito.any(UF.class))).thenReturn(uf);

        mockMvc.perform(delete("/uf").param("code", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoUF").value(uf.getCodigoUF()))
                .andExpect(jsonPath("$.status").value(2L));
    }
}
