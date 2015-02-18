/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.homogeneous_environment.UniformRepairSelfRepairNetworkModel;

/**
 *
 * @author tokumitsu
 */
public class StrategicRepairSelfRepairNetworkModel extends UniformRepairSelfRepairNetworkModel {

    public StrategicRepairSelfRepairNetworkModel(String[] args) {
        super(args);

//        try {
//            System.setOut(new PrintStream("strategic_repair_in_homogeneous_environment.log"));
//        } catch(Exception e) {
//            e.printStackTrace();;
//        }
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

        number = map.get(SimulationParameters.REPAIR_STRATEGY_A);
        String strategyNumberA = Integer.toBinaryString(number.intValue());

        number = map.get(SimulationParameters.REPAIR_STRATEGY_B);
        String strategyNumberB = Integer.toBinaryString(number.intValue());

        String[] strategyNumbers = new String[2];
        
        strategyNumbers[0] = strategyNumberA;
        strategyNumbers[1] = strategyNumberB;

        AgentGameActionEnum[][] strategyCodes = new AgentGameActionEnum[2][neighborUnitCount];

        for (int i = 0; i < strategyCodes.length; i++) {
            int length = strategyNumbers[i].length();

            //0をパディングする
            for (int j = 0; j < neighborUnitCount - length; j++) {
                strategyCodes[i][j] = AgentGameActionEnum.DEFECTION;
            }

            for (int k = 0; k < length; k++) {
                int index = neighborUnitCount - length + k;

                if (strategyNumbers[i].charAt(k) == '0') {
                    strategyCodes[i][index] = AgentGameActionEnum.DEFECTION;
                } else {
                    strategyCodes[i][index] = AgentGameActionEnum.COOPERATION;
                }
            }
        }

        List<RepairStrategy> strategies = new ArrayList<RepairStrategy>();

        for(int i = 0; i < strategyCodes.length; i++) {
            strategies.add(new RepairStrategy(strategyCodes[i]));
        }

        Random random = new Random();

        for(Agent agent: list) {
            StrategicRepairAgent unit = (StrategicRepairAgent) agent;

            RepairStrategy strategy = null;

            int index = random.nextInt(strategies.size());
            strategy = strategies.get(index);

            AgentActionEnum action = null;
            
            if(random.nextBoolean()) {
                action = AgentActionEnum.REAPIR;
            } else {
                action = AgentActionEnum.NOT_REPAIR;
            }

            unit.setCurrentStrategy(strategy);
            unit.setPreviousStrategy(strategy);
            unit.setAction(action);
            unit.setPreviousAction(action);
            unit.setStrategyUpdateErrorRate(strategyUpdateErrorRate);
            unit.addAllStrategy(strategies);
        }
    }

    @Override
    public void postStep(long time) {
        super.postStep(time);
      
        List<Agent> agentList = getEnvironment().getAgentList();

        for(Agent agent: agentList) {
            StrategicRepairAgent unit = (StrategicRepairAgent) agent;

            unit.sumUpNeighborResource();
        }

        Map<String, Number> map = getSimulationParametersMap();

        Number strategyUpdateCycleNumber = map.get(SimulationParameters.STRATEGY_UPDATE_CYCLE);
        int strategyUpdateCycle = strategyUpdateCycleNumber.intValue();

        if((time % strategyUpdateCycle) == 0) {
            for(Agent agent: agentList) {
                StrategicRepairAgent unit = (StrategicRepairAgent) agent;

                RepairStrategy strategy = unit.getCurrentStrategy();
                unit.setPreviousStrategy(strategy);
            }

            for(Agent agent: agentList) {
                StrategicRepairAgent unit = (StrategicRepairAgent) agent;

                unit.updateStrategy();
            }

            for(Agent agent: agentList) {
                Agent unit = (Agent) agent;

                unit.setScore(0.0);
            }
        }
    }

    /**
     * 正常エージェントの割合
     * 修復エージェントの割合
     * 利用可能リソース
     * 戦略Aのコード
     * 戦略Aの割合
     * 戦略Aの正常エージェントの割合
     * 戦略Aの修復エージェントの割合
     * 戦略Aの利用可能リソース
     * 戦略Bのコード
     * 戦略Bの割合
     * 戦略Bの正常エージェントの割合
     * 戦略Bの修復エージェントの割合
     * 戦略Bの利用可能リソース
     * @param time
     */
    @Override
    public void measureNetworkPerformance(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        Map<RepairStrategy, Integer> normalAgentMap = new HashMap<RepairStrategy, Integer>();
        Map<RepairStrategy, Integer> repairAgentMap = new HashMap<RepairStrategy, Integer>();
        Map<RepairStrategy, Integer> strategyMap = new HashMap<RepairStrategy, Integer>();
        Map<RepairStrategy, Double> availableResourceMap = new HashMap<RepairStrategy, Double>();

        int normalAgentCount = 0;
        int repairAgentCount = 0;
        int resources = 0;

        Agent initialAgent = agentList.get(0);
        List<RepairStrategy> strategyList = ((StrategicRepairAgent) initialAgent).getStrategyList();

        for(RepairStrategy strategy : strategyList) {
            normalAgentMap.put(strategy, 0);
            repairAgentMap.put(strategy, 0);
            strategyMap.put(strategy, 0);
            availableResourceMap.put(strategy, 0.0);
        }

        for (Agent agent : agentList) {
            RepairStrategy strategy = ((StrategicRepairAgent) agent).getCurrentStrategy();

            AgentStatusEnum status = agent.getStatus();

            if(status == AgentStatusEnum.NORMAL) {
                int value = normalAgentMap.get(strategy);

                value++;
                normalAgentCount++;

                normalAgentMap.put(strategy, value);
            }

            AgentActionEnum action = agent.getAction();

            if(action == AgentActionEnum.REAPIR) {
                int value = repairAgentMap.get(strategy);

                value++;
                repairAgentCount++;

                repairAgentMap.put(strategy, value);
            }

            double resource = availableResourceMap.get(strategy);
            resource += agent.getAvailableResource();
            availableResourceMap.put(strategy, resource);

            resources += agent.getAvailableResource();

            int distribution = strategyMap.get(strategy);
            distribution++;
            strategyMap.put(strategy, distribution);
        }

        double size = (double) agentList.size();

        double frequencyNormalAgent = ((double) normalAgentCount) / size;
        double frequencyRepairAgent = ((double) repairAgentCount) / size;
        double averagedAvailabeResource = ((double) resources) / size;
        
        StringBuilder builder = new StringBuilder();

        builder.append(frequencyNormalAgent + " ");
        builder.append(frequencyRepairAgent + " ");
        builder.append(averagedAvailabeResource + " ");

        for (Map.Entry<RepairStrategy, Integer> entry : strategyMap.entrySet()) {
            RepairStrategy key = entry.getKey();

            builder.append(key.toString() + " ");

            double distribtution = (double) entry.getValue();

            int strategyAgentCount = entry.getValue();
            double frequencyStrategyAgentValue = ((double) strategyAgentCount) / size;

            builder.append(frequencyStrategyAgentValue + " ");

            int strategyNormalAgentCount = normalAgentMap.get(key);
            double frequencyNormalAgentValue = ((double) strategyNormalAgentCount) / distribtution;

            builder.append(frequencyNormalAgentValue + " ");

            int strategyRepairAgentCount = repairAgentMap.get(key);
            double frequencyRepairAgentValue = ((double) strategyRepairAgentCount) / distribtution;

            builder.append(frequencyRepairAgentValue + " ");

            double strategyAvailableResource = availableResourceMap.get(key);
            double averagedStrategyAvailableResource = strategyAvailableResource / distribtution;

            builder.append(averagedStrategyAvailableResource + " ");
            
        }

        System.out.println(builder.toString());
     }

}
