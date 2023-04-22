package co.fullstacklabs.battlemonsters.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import co.fullstacklabs.battlemonsters.challenge.exceptions.UnprocessableFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import co.fullstacklabs.battlemonsters.challenge.dto.MonsterDTO;
import co.fullstacklabs.battlemonsters.challenge.exceptions.ResourceNotFoundException;
import co.fullstacklabs.battlemonsters.challenge.model.Monster;
import co.fullstacklabs.battlemonsters.challenge.repository.MonsterRepository;
import co.fullstacklabs.battlemonsters.challenge.service.impl.MonsterServiceImpl;
import co.fullstacklabs.battlemonsters.challenge.testbuilders.MonsterTestBuilder;
import org.springframework.core.annotation.Order;

/**
 * @author FullStack Labs
 * @version 1.0
 * @since 2022-10
 */
@ExtendWith(MockitoExtension.class)
@Order(1)
public class MonsterServiceTest {
    @InjectMocks
    public MonsterServiceImpl monsterService;

    @Mock
    public MonsterRepository monsterRepository;

    @Mock
    private ModelMapper mapper;

   @Test
   public void testGetAll() {
       String monsterName1 = "Monster 1";
       String monsterName2 = "Monster 2";
       Monster monster1 = MonsterTestBuilder.builder().id(1)
               .name(monsterName1).attack(30).defense(20).hp(21).speed(15)
               .imageURL("imageUrl1").build();
   
       Monster monster2 = MonsterTestBuilder.builder().id(2)
               .name(monsterName2).attack(30).defense(20).hp(21).speed(15)
               .imageURL("imageUrl1").build();
   
       List<Monster> monsterList = Arrays.asList(new Monster[]{monster1, monster2});
       Mockito.when(monsterRepository.findAll()).thenReturn(monsterList);
       
       monsterService.getAll();
   
       Mockito.verify(monsterRepository).findAll();
       Mockito.verify(mapper).map(monsterList.get(0), MonsterDTO.class);
       Mockito.verify(mapper).map(monsterList.get(1), MonsterDTO.class);        
   }

    @Test
    public void testGetMonsterByIdSuccessfully() throws Exception {
        int id = 1;
        Monster monster1 = MonsterTestBuilder.builder().build();
        Mockito.when(monsterRepository.findById(id)).thenReturn(Optional.of(monster1));
        monsterService.findById(id);
        Mockito.verify(monsterRepository).findById(id);
        Mockito.verify(mapper).map(monster1, MonsterDTO.class);
    }

    @Test
    public void testGetMonsterByIdNotExists() throws Exception {
        int id = 1;        
        Mockito.when(monsterRepository.findById(id)).thenReturn(Optional.empty());                
        Assertions.assertThrows(ResourceNotFoundException.class, 
                                    () -> monsterService.findById(id));
    }

    @Test
    public void testDeleteMonsterSuccessfully() throws Exception {
        int id = 1;
        Monster monster1 = MonsterTestBuilder.builder().build();
        Mockito.when(monsterRepository.findById(id)).thenReturn(Optional.of(monster1));
        Mockito.doNothing().when(monsterRepository).delete(monster1);

        monsterService.delete(id);

        Mockito.verify(monsterRepository).findById(id);        
        Mockito.verify(monsterRepository).delete(monster1);
    }

     @Test
     void testImportCsvSucessfully() throws Exception {
         String sample = "data/monsters-correct.csv";
         Path filePath = Paths.get(sample);
         InputStream reader = Files.newInputStream(filePath);
         int numberOfLinesWithoutHeader = Files.readAllLines(filePath).size() - 1;

         monsterService.importFromInputStream(reader);

         Mockito.verify(monsterRepository, Mockito.times(numberOfLinesWithoutHeader)).save(Mockito.any());
     }

     @Test
     void testImportCsvInexistenctColumns() throws Exception {
         String sample = "data/monsters-wrong-column.csv";
         Path filePath = Paths.get(sample);
         InputStream reader = Files.newInputStream(filePath);

         Assertions.assertThrows(UnprocessableFileException.class,
                 () -> monsterService.importFromInputStream(reader));
     }
     
     @Test
     void testImportCsvInexistenctMonster () throws Exception {
         String sample = "data/monsters-empty-monster.csv";
         Path filePath = Paths.get(sample);
         InputStream reader = Files.newInputStream(filePath);

         Assertions.assertThrows(UnprocessableFileException.class,
                 () -> monsterService.importFromInputStream(reader));
     } 

}
