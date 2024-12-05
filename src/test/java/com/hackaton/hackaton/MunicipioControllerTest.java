package com.hackaton.hackaton;

import static org.mockito.ArgumentMatchers.any;
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
import com.hackaton.model.Municipio;
import com.hackaton.repository.MunicipioRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MunicipioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    //Repositorio "Falso"
    @MockBean
    private MunicipioRepository municipioRepository;

    private Municipio municipio;

    @BeforeEach
    void setUp() {
        //criando o Mock
        municipio = new Municipio();
        municipio.setCodigoMunicipio(1L);
        municipio.setNome("São Paulo");
        municipio.setStatus(1L);
    }

    //Deve retornar todos os Municipios
    @Test
    void testGetAllMunicipios() throws Exception {
        Mockito.when(municipioRepository.findAll()).thenReturn(Collections.singletonList(municipio));

        mockMvc.perform(get("/municipio/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].codigoMunicipio").value(municipio.getCodigoMunicipio()))
                .andExpect(jsonPath("$[0].nome").value(municipio.getNome()))
                .andExpect(jsonPath("$[0].status").value(municipio.getStatus()));
    }

    //Deve conseguir encontrar pelo parâmetro "codigoMunicipio"
    @Test
    void testGetMunicipioByParams() throws Exception {
        Mockito.when(municipioRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.singletonList(municipio));

        mockMvc.perform(get("/municipio").param("codigoMunicipio", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoMunicipio").value(municipio.getCodigoMunicipio()))
                .andExpect(jsonPath("$.nome").value(municipio.getNome()))
                .andExpect(jsonPath("$.status").value(municipio.getStatus()));
    }

    //Deve retornar um array com vários objetos ao fazer uma requisição codigoUF
    @Test
    public void testGetMunicipioListByCodigoUF() throws Exception {
        Municipio municipio1 = new Municipio();
        municipio1.setCodigoMunicipio(1L);
        municipio1.setCodigoUF(1L);
        municipio1.setNome("São Paulo");
        municipio1.setStatus(1L);

        Municipio municipio2 = new Municipio();
        municipio2.setCodigoMunicipio(2L);
        municipio1.setCodigoUF(1L);
        municipio2.setNome("Rio de Janeiro");
        municipio2.setStatus(1L);

        List<Municipio> municipios = Arrays.asList(municipio1, municipio2);

        when(municipioRepository.findByCodigoUF(1L)).thenReturn(municipios);

        mockMvc.perform(get("/municipio")
                .param("codigoUF", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigoMunicipio", is(1)))
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[0].status", is(1)))

                .andExpect(jsonPath("$[1].codigoMunicipio", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Rio de Janeiro")))
                .andExpect(jsonPath("$[1].status", is(1)));
    }

    // Deve retornar um objeto isolado ao pesquisar múltiplos parâmetros
    @Test
    public void testGetMunicipioByMultipleParams() throws Exception {
        Municipio municipio = new Municipio();
        municipio.setCodigoMunicipio(1L);
        municipio.setNome("São Paulo");
        municipio.setStatus(1L);

        when(municipioRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.singletonList(municipio));

        mockMvc.perform(get("/municipio")
                .param("nome", "São Paulo")
                .param("status", "1")
                .param("codigoMunicipio", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[0].status", is(1)))
                .andExpect(jsonPath("$[0].codigoMunicipio", is(1)));
    }
    
    //Deve retornar um array com vários objetos ao fazer uma requisição STATUS
    @Test
    public void testGetMunicipioListByStatus() throws Exception {
        Municipio municipio1 = new Municipio();
        municipio1.setCodigoMunicipio(1L);
        municipio1.setNome("São Paulo");
        municipio1.setStatus(1L);

        Municipio municipio2 = new Municipio();
        municipio2.setCodigoMunicipio(2L);
        municipio2.setNome("Rio de Janeiro");
        municipio2.setStatus(1L);

        List<Municipio> municipios = Arrays.asList(municipio1, municipio2);

        when(municipioRepository.findByStatus(1L)).thenReturn(municipios);

        mockMvc.perform(get("/municipio")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigoMunicipio", is(1)))
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[0].status", is(1)))

                .andExpect(jsonPath("$[1].codigoMunicipio", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Rio de Janeiro")))
                .andExpect(jsonPath("$[1].status", is(1)));
    }

    //Deve entregar um Array vazio ao não encontrar os paâmetros
    @Test
    public void testGetMunicipioEmptyWhithoutParams() throws Exception {
        when(municipioRepository.findByStatus(3L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/municipio")
                .param("status", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    //Deve exibir uma mensagem de erro ao tentar fazer uma pesquisa inválida por codigoMunicipio
    @Test
    public void testGetMunicipioByParamsInvalidCodigoMunicipio() throws Exception {
        mockMvc.perform(get("/municipio")
                .param("codigoMunicipio", "invalid")
                .param("nome", "Sao Paulo"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para código município não é um número válido.")));
    }

    //Deve exibir uma mensagem de erro ao tentar fazer uma pesquisa inválida por codigoMunicipio
    @Test
    public void testGetMunicipioByParamsInvalidStatus() throws Exception {
        mockMvc.perform(get("/municipio")
                .param("status", "notANumber") 
                .param("nome", "Sao Paulo"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para status não é um número válido.")));
    }

    //Deve criar um objeto e retornar um array com todos os outros municipios
    @Test
    public void testCreateMunicipio() throws Exception {
        Municipio municipio = new Municipio();
        municipio.setCodigoMunicipio(1L);
        municipio.setCodigoUF(1L);
        municipio.setNome("São Paulo");
        municipio.setStatus(1L);

        List<Municipio> allMunicipios = Arrays.asList(municipio);
    
        when(municipioRepository.save(any(Municipio.class))).thenReturn(municipio);
        when(municipioRepository.findAll()).thenReturn(allMunicipios);

        mockMvc.perform(post("/municipio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(municipio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codigoUF", is(1)))
                .andExpect(jsonPath("$.[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$.[0].status", is(1)));
    }

    // Teste deve dar erro ao fazer post

    // Teste de edição de município
    @Test
    public void testEditMunicipio() throws Exception {
        Municipio municipio = new Municipio();
        municipio.setCodigoMunicipio(1L);
        municipio.setCodigoUF(1L);
        municipio.setNome("São Paulo");
        municipio.setStatus(1L);
    
        Municipio updatedMunicipio = new Municipio();
        updatedMunicipio.setCodigoMunicipio(1L);
        updatedMunicipio.setCodigoUF(2L);
        updatedMunicipio.setNome("Rio de Janeiro");
        updatedMunicipio.setStatus(1L);
    
        when(municipioRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.singletonList(municipio));
        when(municipioRepository.save(any(Municipio.class))).thenReturn(updatedMunicipio);
        when(municipioRepository.findAll()).thenReturn(Collections.singletonList(updatedMunicipio));
    
        mockMvc.perform(put("/municipio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMunicipio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].codigoMunicipio", is(1)))
                .andExpect(jsonPath("$.[0].nome", is("Rio de Janeiro")))
                .andExpect(jsonPath("$.[0].status", is(1)));
    }
    

    //Deve exibir uma mensagem de erro ao inserir ID inválido
    @Test
    public void testEditMunicipioErrorID() throws Exception {
        Municipio municipio = new Municipio();
        municipio.setCodigoMunicipio(1L);
        municipio.setCodigoUF(1L);
        municipio.setNome("Rio de Janeiro");
        municipio.setStatus(1L);
    
        when(municipioRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.emptyList());
    
        mockMvc.perform(put("/municipio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(municipio)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.mensagem", is("Não foi encontrado nenhum município com esse código.")));
    }
    


    //Deve conseguir apagar Municipio
    @Test
    void testDeleteMunicipio() throws Exception {
        Mockito.when(municipioRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.singletonList(municipio));
        municipio.setStatus(2L);
        Mockito.when(municipioRepository.save(Mockito.any(Municipio.class))).thenReturn(municipio);

        mockMvc.perform(delete("/municipio").param("code", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoMunicipio").value(municipio.getCodigoMunicipio()))
                .andExpect(jsonPath("$.status").value(2L));
    }
}
