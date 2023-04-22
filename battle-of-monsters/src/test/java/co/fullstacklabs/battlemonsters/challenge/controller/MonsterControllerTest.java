package co.fullstacklabs.battlemonsters.challenge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.fullstacklabs.battlemonsters.challenge.model.Monster;
import co.fullstacklabs.battlemonsters.challenge.testbuilders.MonsterTestBuilder;
import org.flywaydb.core.Flyway;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.fullstacklabs.battlemonsters.challenge.ApplicationConfig;
import co.fullstacklabs.battlemonsters.challenge.dto.MonsterDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author FullStack Labs
 * @version 1.0
 * @since 2022-10
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(ApplicationConfig.class)
public class MonsterControllerTest {
    private static final String MONSTER_PATH = "/monster";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void init(){
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldFetchAllMonsters() throws Exception {
        createMonster();

        this.mockMvc.perform(get(MONSTER_PATH)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Is.is(1)))
                .andExpect(jsonPath("$[0].name", Is.is("Monster 1")))
                .andExpect(jsonPath("$[0].attack", Is.is(50)))
                .andExpect(jsonPath("$[0].defense", Is.is(40)))
                .andExpect(jsonPath("$[0].hp", Is.is(30)))
                .andExpect(jsonPath("$[0].speed", Is.is(25)));
    }

    @Test
    void shouldGetMosterSuccessfully() throws Exception {
        createMonster();
        this.mockMvc.perform(get(MONSTER_PATH + "/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", Is.is("Monster 1")));
    }

    @Test
    void shoulGetMonsterNotExists() throws Exception {
        long id = 1538l;
        this.mockMvc.perform(get(MONSTER_PATH + "/{id}", id))
                    .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMonsterSuccessfully() throws Exception {
        createMonster();
        this.mockMvc.perform(delete(MONSTER_PATH + "/{id}", 1))
                    .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteMonsterNotFound() throws Exception {
        this.mockMvc.perform(delete(MONSTER_PATH + "/{id}", 5))
                    .andExpect(status().isNotFound());
    }

     @Test
     void testImportCsvSucessfully() throws Exception {
         String sample = "data/monsters-correct.csv";
         MockMultipartFile mockFile = getMockMultipartFile(sample);
         this.mockMvc.perform(multipart(MONSTER_PATH + "/import").file(mockFile))
                     .andExpect(status().isOk());
     }

    @Test
     void testImportCsvInexistenctColumns() throws Exception {
        String sample = "data/monsters-wrong-column.csv";
        MockMultipartFile mockFile = getMockMultipartFile(sample);
        this.mockMvc.perform(multipart(MONSTER_PATH + "/import").file(mockFile))
                    .andExpect(status().is5xxServerError());
     }

     @Test
     void testImportCsvInexistenctMonster () throws Exception {
         String sample = "data/monsters-empty-monster.csv";
         MockMultipartFile mockFile = getMockMultipartFile(sample);
         this.mockMvc.perform(multipart(MONSTER_PATH + "/import").file(mockFile))
                     .andExpect(status().is5xxServerError());
     }

    private void createMonster() throws Exception {
        Monster monster = MonsterTestBuilder.builder().build();
        MonsterDTO newMonster = objectMapper.convertValue(monster, MonsterDTO.class);

        this.mockMvc.perform(post(MONSTER_PATH).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMonster)));
    }

    private static MockMultipartFile getMockMultipartFile(String sample) throws IOException {
        Path filePath = Paths.get(sample);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "monsters.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );
        return mockFile;
    }
}
