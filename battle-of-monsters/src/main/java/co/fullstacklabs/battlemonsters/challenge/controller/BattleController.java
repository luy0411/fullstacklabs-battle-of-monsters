package co.fullstacklabs.battlemonsters.challenge.controller;

import java.util.List;

import co.fullstacklabs.battlemonsters.challenge.dto.MonsterDTO;
import co.fullstacklabs.battlemonsters.challenge.service.MonsterService;
import org.springframework.web.bind.annotation.*;

import co.fullstacklabs.battlemonsters.challenge.dto.BattleDTO;
import co.fullstacklabs.battlemonsters.challenge.service.BattleService;

/**
 * @author FullStack Labs
 * @version 1.0
 * @since 2022-10
 */
@RestController
@RequestMapping("/battle")
public class BattleController {

    private BattleService battleService;
    private MonsterService monsterService;

    public BattleController(BattleService battleService,
                            MonsterService monsterService) {
        this.battleService = battleService;
        this.monsterService = monsterService;
    }

    @GetMapping
    public List<BattleDTO> getAll() {
        return battleService.getAll();
    }

    @PatchMapping("/monster/{monsterA}/vs/{monsterB}")
    public BattleDTO startBattle(@PathVariable("monsterA") int monsterA,
                                 @PathVariable("monsterB") int monsterB) {
        MonsterDTO monsterADTO = monsterService.findById(monsterA);
        MonsterDTO monsterBDTO = monsterService.findById(monsterB);

        return battleService.create(monsterADTO, monsterBDTO);
    }
}
