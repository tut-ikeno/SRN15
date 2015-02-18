/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 *
 * @author tokumitsu
 */
public class KCStrategicRepairSelfRepairNetworkModel extends StrategicRepairSelfRepairNetworkModel {

    private RepairStrategy kcStrategies[] = null;

    public KCStrategicRepairSelfRepairNetworkModel(String[] args) {
        super(args);

//        try {
//            System.setOut(new PrintStream("kcstrategic_repair_in_homogeneous_environment.log"));
//        } catch(Exception e) {
//            e.printStackTrace();;
//        }
    }

    @Override
    public void measureNetworkPerformance(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        int[] normalUnitCount = new int[kcStrategies.length]; //kC戦略とAll-D
        int[] repairUnitCount = new int[kcStrategies.length];
        double[] resource = new double[kcStrategies.length];

        int[] kcStrategiesCount = new int[kcStrategies.length];

        for(Agent agent: agentList) {
            StrategicRepairUnit unit = (StrategicRepairUnit) agent;

            RepairStrategy strategy = unit.getCurrentStrategy();

            for(int i = 0; i < kcStrategies.length; i++) {
                if(strategy.isEquivalent(kcStrategies[i])) {
                    kcStrategiesCount[i]++;

                    AgentStatusEnum status = unit.getStatus();

                    if (status == AgentStatusEnum.NORMAL) {
                        normalUnitCount[i]++;
                    }

                    AgentActionEnum action = unit.getAction();

                    if (action == AgentActionEnum.REAPIR) {
                        repairUnitCount[i]++;
                    }

                    resource[i] += unit.getAvailableResource();
                } 
            }
        }

        double size = (double) agentList.size();

        int summedNormalUnit = 0;
        int summedRepairUnit = 0;
        double summedResource = 0.0;

        StringBuilder builder = new StringBuilder();

        //全体の平均
        for(int i = 0; i < kcStrategies.length; i++) {
            summedNormalUnit += normalUnitCount[i];
        }
        
        double normalUnitFrequency = summedNormalUnit / size;

        builder.append(normalUnitFrequency);

        for (int i = 0; i < kcStrategies.length; i++) {
            summedRepairUnit += repairUnitCount[i];
        }

        double repairUnitFrequency = summedRepairUnit / size;

        builder.append(" " + repairUnitFrequency);

        for (int i = 0; i < kcStrategies.length; i++) {
            summedResource += resource[i];
        }

        double averagedResource = summedResource / size;

        builder.append(" " + averagedResource);

        //戦略の人口構成
        for(int i = 0; i < kcStrategiesCount.length; i++) {
            double frequency = ((double) kcStrategiesCount[i]) /((double) size);

            builder.append(" " + frequency);
        }

//        //戦略ごとの修復エージェント
//        for(int i = 0; i < kcStrategiesCount.length; i++) {
//            double frequency = ((double) repairUnitCount[i]) /((double) size);
//
//            builder.append(" " + frequency);
//        }
//
//        //戦略ごとの利用可能リソース
//        for(int i = 0; i < kcStrategiesCount.length; i++) {
//            double frequency = 0.0;
//
//            if (kcStrategiesCount[i] !=  0) {
//                frequency = ((double) resource[i]) / ((double) kcStrategiesCount[i]);
//            }
//
//            //builder.append(" " + frequency);
//        }
        
        System.out.println(builder.toString());
  
    }

    @Override
    public void init(Environment environment) {
        super.init(environment);
        initKCStrategies();
        assignStrategy();
    }

    private void assignStrategy() {
        List<Agent> agentList = getEnvironment().getAgentList();

        int allAgentCount = agentList.size();

        LinkedList<Agent> list = new LinkedList<Agent>(agentList);
        
        //1戦略あたりの初期個体数
        int count = allAgentCount / kcStrategies.length;

        for(int k = 0; k < kcStrategies.length; k++) {
            for(int i = 0; i < count; i++) {
                StrategicRepairUnit unit = (StrategicRepairUnit) list.removeFirst();

                unit.setPreviousStrategy(kcStrategies[k]);
                unit.setCurrentStrategy(kcStrategies[k]);
            }
        }
    }

    private void initKCStrategies() {
        Map<String, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameters.NEIGHBOR_UNIT_COUNT);
        int neighborUnitCount = number.intValue() + 1; //空間的戦略のために+1

        kcStrategies = new RepairStrategy[neighborUnitCount + 1]; //All-D(9C)も含める

        for(int k = 0; k < kcStrategies.length; k++) {
            kcStrategies[k] = createKCStrategy(k);
        }
    }

    public RepairStrategy createKCStrategy(int k) {
        Map<String, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameters.NEIGHBOR_UNIT_COUNT);
        int neighborUnitCount = number.intValue() + 1; //空間的戦略のために+1

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
