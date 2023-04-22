package co.fullstacklabs.battlemonsters.challenge.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import co.fullstacklabs.battlemonsters.challenge.dto.MonsterDTO;
import co.fullstacklabs.battlemonsters.challenge.model.Monster;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.fullstacklabs.battlemonsters.challenge.dto.BattleDTO;
import co.fullstacklabs.battlemonsters.challenge.model.Battle;
import co.fullstacklabs.battlemonsters.challenge.repository.BattleRepository;
import co.fullstacklabs.battlemonsters.challenge.service.BattleService;

/**
 * @author FullStack Labs
 * @version 1.0
 * @since 2022-10
 */
@Service
public class BattleServiceImpl implements BattleService {

    private static final Logger logger = LoggerFactory.getLogger(BattleServiceImpl.class);

    private BattleRepository battleRepository;
    private ModelMapper modelMapper;

    @Autowired
    public BattleServiceImpl(BattleRepository battleRepository, ModelMapper modelMapper) {
        this.battleRepository = battleRepository;
        this.modelMapper = modelMapper;    
    }

    /**
     * List all existence battles
     */
    @Override
    public List<BattleDTO> getAll() {
        List<Battle> battles = battleRepository.findAll();
        return battles.stream().map(battle -> modelMapper.map(battle, BattleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BattleDTO create(MonsterDTO monsterA,
                            MonsterDTO monsterB) {
        Battle battle = new Battle();
        battle.setMonsterA(modelMapper.map(monsterA, Monster.class));
        battle.setMonsterB(modelMapper.map(monsterB, Monster.class));

        Monster winner = getWinner(monsterA, monsterB);
        battle.setWinner(modelMapper.map(winner, Monster.class));

        battle = battleRepository.save(battle);

        BattleDTO battleDTO = modelMapper.map(battle, BattleDTO.class);

        return battleDTO;
    }

    private Monster getWinner(MonsterDTO monsterA,
                              MonsterDTO monsterB) {
        MonsterDTO defencer = monsterA;
        MonsterDTO attacker = monsterB;

        if ((monsterA.getSpeed() > monsterB.getSpeed()) ||
                ((monsterA.getSpeed().equals(monsterB.getSpeed())) &&
                        (monsterA.getAttack() > monsterB.getAttack()))) {
            attacker = monsterA;
            defencer = monsterB;
        }

        MonsterDTO winner = attacker;
        while (defencer.getHp() == 0 || attacker.getHp() == 0) {
            Integer attack = attacker.getAttack();
            Integer defense = defencer.getDefense();
            Integer damage = 1;

            if (attack > defense) {
                damage = attack - defense;
            }

            defencer.setHp(defencer.getHp() - damage);

            if (defencer.getHp() > attacker.getHp())
                winner = defencer;
        }

        return modelMapper.map(winner, Monster.class);
    }


}
