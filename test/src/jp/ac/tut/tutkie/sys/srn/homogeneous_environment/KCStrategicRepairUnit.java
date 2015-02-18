/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 *
 * @author tokumitsu
 */
public class KCStrategicRepairUnit extends StrategicRepairUnit {

    private Random random = new Random();

    public KCStrategicRepairUnit(Environment environment) {
        super(environment);
    }

    @Override
    public void updateStrategy() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        Agent maxScoreUnit = this;

        for (Agent unit : neighborUnitList) {
            if (maxScoreUnit.getScore() <= unit.getScore()) {
                maxScoreUnit = unit;
            }
        }

        double strategyUpdateErrorRate = getStrategyUpdateErrorRate();

        if (random.nextDouble() < strategyUpdateErrorRate) {
            int neighborUnitCount = neighborUnitList.size() + 2; //空間的戦略のため + All-D(9C)も含める
            int k = random.nextInt(neighborUnitCount + 1);

            RepairStrategy newRepairStrategy = createKCStrategy(k);
            setCurrentStrategy(newRepairStrategy);
        } else {
            RepairStrategy previousRepairStrategy = ((StrategicRepairUnit) maxScoreUnit).getPreviousStrategy();
            AgentGameActionEnum[] code = previousRepairStrategy.getStrategyCodeArray();

            RepairStrategy newRepairStrategy = new RepairStrategy(code);
            setCurrentStrategy(newRepairStrategy);
        }
    }

    public RepairStrategy createKCStrategy(int k) {
        List<Agent> list = getNeighborUnitList();
        int size = list.size();

        int neighborUnitCount = size + 2; //空間的戦略のために+All-D(9C)も含めるため

        AgentGameActionEnum[] code = new AgentGameActionEnum[neighborUnitCount];

        for(int i = 0; i < k; i++) {
            code[i] = AgentGameActionEnum.DEFECTION;
        }

        for(int i = k; i < neighborUnitCount; i++) {
            code[i] = AgentGameActionEnum.COOPERATION;
        }

        RepairStrategy strategy = new RepairStrategy(code);

        return strategy;
    }
}
