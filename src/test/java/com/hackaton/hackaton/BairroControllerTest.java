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
import com.hackaton.model.Bairro;
import com.hackaton.repository.BairroRepository;


@SpringBootTest
@AutoConfigureMockMvc
public class BairroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    //Repositorio "Falso"
    @MockBean
    private BairroRepository bairroRepository;

    private Bairro bairro;

    @BeforeEach
    void setUp() {
        //criando o Mock
        bairro = new Bairro();
        bairro.setCodigoMunicipio(1L);
        bairro.setNome("Liberdade");
        bairro.setStatus(1L);
    }

    //Deve retornar todas os Bairros
    @Test
    void testGetAllBairros() throws Exception {
        Mockito.when(bairroRepository.findAll()).thenReturn(Collections.singletonList(bairro));

        mockMvc.perform(get("/bairro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    //Deve retornar apenas um objeto pelo parâmetro "codigoBairro"
    @Test
    void testGetBairroByParams() throws Exception {
        Mockito.when(bairroRepository.findByCodigoBairro(1L)).thenReturn(Collections.singletonList(bairro));

        mockMvc.perform(get("/bairro").param("codigoBairro", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoMunicipio").value(bairro.getCodigoMunicipio()))
                .andExpect(jsonPath("$.nome").value(bairro.getNome()))
                .andExpect(jsonPath("$.status").value(bairro.getStatus()));
    }

    //Deve retornar um array com vários objetos ao pesquisar nome
    @Test
    public void testGetBairroListByNome() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setCodigoBairro(1L);
        bairro1.setCodigoMunicipio(1L);
        bairro1.setNome("Liberdade");
        bairro1.setStatus(1L);

        Bairro bairro2 = new Bairro();
        bairro2.setCodigoBairro(2L);
        bairro2.setCodigoMunicipio(2L);
        bairro2.setNome("Liberdade");
        bairro2.setStatus(1L);

        List<Bairro> bairros = Arrays.asList(bairro1, bairro2);

        when(bairroRepository.findByNome("Liberdade")).thenReturn(bairros);

        mockMvc.perform(get("/bairro")
                .param("nome", "Liberdade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    //Deve retornar um array com vários objetos ao pesquisar codigoMunicipio
    @Test
    public void testGetBairroListByCodigoMunicipio() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setCodigoBairro(1L);
        bairro1.setCodigoMunicipio(1L);
        bairro1.setNome("Liberdade");
        bairro1.setStatus(1L);

        Bairro bairro2 = new Bairro();
        bairro2.setCodigoBairro(2L);
        bairro2.setCodigoMunicipio(1L);
        bairro2.setNome("Copacabana");
        bairro2.setStatus(1L);

        List<Bairro> bairros = Arrays.asList(bairro1, bairro2);

        when(bairroRepository.findByCodigoMunicipio(1L)).thenReturn(bairros);

        mockMvc.perform(get("/bairro")
                .param("codigoMunicipio", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }


    //Deve retornar um array com vários objetos ao pesquisar status
    @Test
    public void testGetBairroListByStatus() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setCodigoBairro(1L);
        bairro1.setNome("Liberdade");
        bairro1.setStatus(1L);

        Bairro bairro2 = new Bairro();
        bairro2.setCodigoBairro(2L);
        bairro2.setNome("Copacabana");
        bairro2.setStatus(1L);

        List<Bairro> bairros = Arrays.asList(bairro1, bairro2);

        when(bairroRepository.findByStatus(1L)).thenReturn(bairros);

        mockMvc.perform(get("/bairro")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    //Deve retornar um objeto isolado ao pesquisar multiplos parâmetros
    @Test
    public void testGetBairroByMultipleParams() throws Exception {
        Bairro bairro = new Bairro();
        bairro.setCodigoBairro(1L);
        bairro.setCodigoMunicipio(1L);
        bairro.setNome("Liberdade");
        bairro.setStatus(1L);
    
        // Mock dos métodos de busca
        when(bairroRepository.findByNome("Liberdade")).thenReturn(Collections.singletonList(bairro));
        when(bairroRepository.findByStatus(1L)).thenReturn(Collections.singletonList(bairro));
        when(bairroRepository.findByCodigoMunicipio(1L)).thenReturn(Collections.singletonList(bairro));
        
        mockMvc.perform(get("/bairro")
                .param("nome", "Liberdade")
                .param("status", "1")
                .param("codigoBairro", "1")
                .param("sigla", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoMunicipio", is(1)))
                .andExpect(jsonPath("$.nome", is("Liberdade")))
                .andExpect(jsonPath("$.status", is(1)));
    }
    

    @Test
    public void testGetEmptyArrayWithInvalidParams() throws Exception {
        when(bairroRepository.findByCodigoBairro(0L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bairro")
                 .param("codigoBairro", "1"))
                 .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetBairroByParams_invalidCodigoBairro() throws Exception {
        mockMvc.perform(get("/bairro")
                .param("codigoMunicipio", "invalid") // Parâmetro não numérico
                .param("nome", "Liberdade"))
                .andExpect(jsonPath("$.status", is(400))) // Espera o status 400
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para codigoMunicipio não é um número válido."))); // Verifica a mensagem de erro
    }

@Test
    public void testGetBairroByParams_invalidStatus() throws Exception {
        mockMvc.perform(get("/bairro")
                .param("status", "notANumber") // Parâmetro não numérico
                .param("nome", "Liberdade"))
                .andExpect(jsonPath("$.status", is(400))) // Espera o status 400
                .andExpect(jsonPath("$.mensagem", is("O valor inserido para status não é um número válido."))); // Verifica a mensagem de erro
    }


    //Deve Devolver um array com TODOS os objetos e entre eles o criado agora
    @Test
    public void testCreateBairro() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setCodigoBairro(5L);
        bairro1.setCodigoMunicipio(1L);
        bairro1.setNome("Asa Sul");
        bairro1.setStatus(1L);
    
        when(bairroRepository.save(any(Bairro.class))).thenReturn(bairro1);
        when(bairroRepository.findAll()).thenReturn(Collections.singletonList(bairro1));
    
        mockMvc.perform(post("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairro1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nome == 'Asa Sul')].nome", hasItem("Asa Sul"))) //workaround
                .andExpect(jsonPath("$[?(@.codigoMunicipio == 1)].codigoMunicipio", hasItem(1)))
                .andExpect(jsonPath("$[?(@.status == 1)].status", hasItem(1)));
    }

    @Test
    public void testCreateBairroInvalidParams() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setNome("Asa Sul");
        bairro1.setStatus(1L);
    
        mockMvc.perform(post("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairro1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O campo municipio é obrigatório.")));
    }
    
    //Deve conseguir editar Bairro
    @Test
    public void testEditBairro() throws Exception {
        Bairro bairroOriginal = new Bairro();
        bairroOriginal.setCodigoBairro(1L);
        bairroOriginal.setCodigoMunicipio(1L);
        bairroOriginal.setNome("Rio de Janeiro");
        bairroOriginal.setStatus(1L);
    
        Bairro bairroEditado = new Bairro();
        bairroEditado.setCodigoBairro(1L);
        bairroEditado.setCodigoMunicipio(1L);
        bairroEditado.setNome("Rio de Janeiro - Editado");
        bairroEditado.setStatus(2L);
    
        when(bairroRepository.findAll()).thenReturn(Arrays.asList(bairroOriginal));
        when(bairroRepository.findByCodigoBairro(1L)).thenReturn(Collections.singletonList(bairroOriginal));
        when(bairroRepository.save(any(Bairro.class))).thenReturn(bairroEditado);
    
        mockMvc.perform(put("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairroEditado)))
                .andExpect(status().isOk());
    
        mockMvc.perform(get("/bairro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigoBairro", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Rio de Janeiro - Editado")))
                .andExpect(jsonPath("$[0].status", is(2)));
    }
    
    //Deve retornar erro quando não informar algum dos parâmetros
    @Test
    public void testEditBairroInvalidParams() throws Exception {
        bairro.setCodigoMunicipio(1L);
        bairro.setNome("Asa Sul");
        bairro.setStatus(1L);
    
        mockMvc.perform(put("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensagem", is("O campo codigoBairro é obrigatório")));
    }
    

    //Deve informar quando não encontrar o ID do bairro
    @Test
    public void testEditaBairro() throws Exception {
        Bairro bairro = new Bairro();
        bairro.setCodigoBairro(5L);
        bairro.setCodigoMunicipio(1L);
        bairro.setNome("Asa Sul");
        bairro.setStatus(1L);
    
    
        when(bairroRepository.findAll()).thenReturn(Arrays.asList(bairro));
        when(bairroRepository.findByCodigoBairro(1L)).thenReturn(Collections.singletonList(bairro));
        when(bairroRepository.save(any(Bairro.class))).thenReturn(bairro);
    
        mockMvc.perform(put("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairro)))
                .andExpect(status().isBadRequest());

    }

    //Deve conseguir apagar Bairro
    @Test
    void testDeleteBairro() throws Exception {
        Mockito.when(bairroRepository.findByCodigoBairro(1L)).thenReturn(Collections.singletonList(bairro));
        bairro.setStatus(2L);
        Mockito.when(bairroRepository.save(Mockito.any(Bairro.class))).thenReturn(bairro);

        mockMvc.perform(delete("/bairro").param("code", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(2L));
    }
}
