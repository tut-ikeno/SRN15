/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;

/**
 *
 * @author tokumitsu
 */
public class StrategicRepairSelfRepairNetworkModel extends UniformRepairSelfRepairNetworkModel {

    public StrategicRepairSelfRepairNetworkModel(String[] args) {
        super(args);

        try {
            System.setOut(new PrintStream("strategic_repair_in_homogeneous_environment.log"));
        } catch(Exception e) {
            e.printStackTrace();;
        }
    }

    @Override
    public void initAgents() {
        super.initAgents();

        List<Agent> list = getEnvironment().getAgentList();
        Collections.shuffle(list);

        Map<String, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameters.STRATEGY_UPDATE_ERROR_RATE);
        double strategyUpdateErrorRate = number.doubleValue();

        number = map.get(SimulationParameters.NEIGHBOR_UNIT_COUNT);
        int neighborUnitCount = number.intValue() + 1; //空間的戦略のために+1

        Random random = new Random();

        AgentGameActionEnum[] allCStrategy = new AgentGameActionEnum[neighborUnitCount];
        AgentGameActionEnum[] allDStrategy = new AgentGameActionEnum[neighborUnitCount];

        for(int i = 0; i < neighborUnitCount; i++) {
            allCStrategy[i] = AgentGameActionEnum.COOPERATION;
            allDStrategy[i] = AgentGameActionEnum.DEFECTION;
        }

        for(Agent agent: list) {
            StrategicRepairUnit unit = (StrategicRepairUnit) agent;

            AgentActionEnum action = null;
            RepairStrategy strategy = null;

            if(random.nextInt(2) == 0) {
                strategy = new RepairStrategy(allDStrategy);
                action = AgentActionEnum.NOT_REPAIR;
            } else {
                strategy = new RepairStrategy(allCStrategy);
                action = AgentActionEnum.REAPIR;
            }

            unit.setCurrentStrategy(strategy);
            unit.setPreviousStrategy(strategy);
            unit.setAction(action);
            unit.setPreviousAction(action);
            unit.setStrategyUpdateErrorRate(strategyUpdateErrorRate);
        }
    }

    @Override
    public void postStep(long time) {
        super.postStep(time);
      
        List<Agent> agentList = getEnvironment().getAgentList();

        for(Agent agent: agentList) {
            StrategicRepairUnit unit = (StrategicRepairUnit) agent;

            unit.sumUpNeighborResource();
        }

        Map<String, Number> map = getSimulationParametersMap();

        Number strategyUpdateCycleNumber = map.get(SimulationParameters.STRATEGY_UPDATE_CYCLE);
        int strategyUpdateCycle = strategyUpdateCycleNumber.intValue();

        if((time % strategyUpdateCycle) == 0) {
            for(Agent agent: agentList) {
                StrategicRepairUnit unit = (StrategicRepairUnit) agent;

                RepairStrategy strategy = unit.getCurrentStrategy();
                unit.setPreviousStrategy(strategy);
            }

            for(Agent agent: agentList) {
                StrategicRepairUnit unit = (StrategicRepairUnit) agent;

                unit.updateStrategy();
            }

            for(Agent agent: agentList) {
                Agent unit = (Agent) agent;

                unit.setScore(0.0);
            }
        }
    }
    
}
