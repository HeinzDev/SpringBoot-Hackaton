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
    //TODO Levantar requisitos
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

    //Deve conseguir encontrar pelo parâmetro "codigoBairro"
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
    
    //Deve retornar um array com vários objetos ao fazer uma requisiçãoGet
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

    @Test
    public void testGetManyBairrosWithoutParams() throws Exception {
        Bairro bairro1 = new Bairro();
        bairro1.setCodigoBairro(1L);
        bairro1.setNome("Bairro 1");
        bairro1.setStatus(1L);

        Bairro bairro2 = new Bairro();
        bairro2.setCodigoBairro(2L);
        bairro2.setNome("Bairro 2");
        bairro2.setStatus(1L);

        List<Bairro> bairros = Arrays.asList(bairro1, bairro2);

        when(bairroRepository.findAll()).thenReturn(bairros);

        mockMvc.perform(get("/bairro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
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
        bairro1.setCodigoBairro(5L);  // O código do bairro é 5, conforme o retorno
        bairro1.setCodigoMunicipio(1L);
        bairro1.setNome("Asa Sul");
        bairro1.setStatus(1L);
    
        // Mock para salvar e retornar o bairro
        when(bairroRepository.save(any(Bairro.class))).thenReturn(bairro1);
        when(bairroRepository.findAll()).thenReturn(Collections.singletonList(bairro1));
    
        // Realiza o POST
        mockMvc.perform(post("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairro1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nome == 'Asa Sul')].nome", hasItem("Asa Sul"))) // Verifica se "Asa Sul" está presente no retorno e tem o nome correto
                .andExpect(jsonPath("$[?(@.codigoMunicipio == 1)].codigoMunicipio", hasItem(1)))
                .andExpect(jsonPath("$[?(@.status == 1)].status", hasItem(1)));
    }

    //Deve conseguir editar Bairro
    @Test
    public void testEditBairro() throws Exception {
        // Criando o bairro original para edição
        Bairro bairroOriginal = new Bairro();
        bairroOriginal.setCodigoBairro(1L);
        bairroOriginal.setCodigoMunicipio(1L);
        bairroOriginal.setNome("Rio de Janeiro");
        bairroOriginal.setStatus(1L);
    
        // Criando o bairro editado
        Bairro bairroEditado = new Bairro();
        bairroEditado.setCodigoBairro(1L);
        bairroEditado.setCodigoMunicipio(1L);
        bairroEditado.setNome("Rio de Janeiro - Editado");
        bairroEditado.setStatus(2L);
    
        // Simulando o retorno de todos os bairros
        when(bairroRepository.findAll()).thenReturn(Arrays.asList(bairroOriginal));
        when(bairroRepository.findByCodigoBairro(1L)).thenReturn(Collections.singletonList(bairroOriginal));
        when(bairroRepository.save(any(Bairro.class))).thenReturn(bairroEditado);
    
        // Realizando a requisição de edição
        mockMvc.perform(put("/bairro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bairroEditado)))
                .andExpect(status().isOk());
    
        // Verificando se o bairro foi editado corretamente na lista de bairros
        mockMvc.perform(get("/bairro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))  // Espera um único bairro na lista
                .andExpect(jsonPath("$[0].codigoBairro", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Rio de Janeiro - Editado")))
                .andExpect(jsonPath("$[0].status", is(2)));
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
