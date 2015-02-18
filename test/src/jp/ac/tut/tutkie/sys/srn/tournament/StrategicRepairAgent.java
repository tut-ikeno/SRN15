/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;
import jp.ac.tut.tutkie.sys.srn.homogeneous_environment.UniformRepairUnit;

/**
 *
 * @author tokumitsu
 */
public class StrategicRepairAgent extends UniformRepairUnit {

    private double strategyUpdateErrorRate_ = 0.0;

    private RepairStrategy previousStrategy_ = null;
    private RepairStrategy currentStrategy_ = null;
    private List<RepairStrategy> strategyList_ = null;

    private Random random = null;

    public StrategicRepairAgent(Environment environment) {
        super(environment);

        random = new Random();
        setStrategyList(new ArrayList<RepairStrategy>());
    }

    public void sumUpNeighborResource() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        double score = getScore();

        for (Agent unit: neighborUnitList) {
            score += unit.getAvailableResource();
        }

        setScore(score);
        setPreviousAction(getAction());
    }

    public void updateStrategy() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        Agent maxScoreUnit = this;

        for (Agent unit : neighborUnitList) {
            if (maxScoreUnit.getScore() <= unit.getScore()) {
                maxScoreUnit = unit;
            }
        }

        double strategyUpdateErrorRate = getStrategyUpdateErrorRate();

        RepairStrategy newRepairStrategy = null;

        if(random.nextDouble() < strategyUpdateErrorRate) {
            RepairStrategy previousRepairStrategy = getPreviousStrategy();
            List<RepairStrategy> strategyList = getStrategyList();

            strategyList.remove(previousRepairStrategy);

            int index = random.nextInt(strategyList.size());
            RepairStrategy strategy = strategyList.get(index);

            newRepairStrategy = new RepairStrategy(strategy.getStrategyCodeArray());

            strategyList.add(previousRepairStrategy);
        } else {
            RepairStrategy previousRepairStrategy = ((StrategicRepairAgent) maxScoreUnit).getPreviousStrategy();
            AgentGameActionEnum[] code = previousRepairStrategy.getStrategyCodeArray();

            newRepairStrategy = new RepairStrategy(code);
        }

        setCurrentStrategy(newRepairStrategy);
    }

    @Override
    public AgentActionEnum updateAction() {
        int defectionCount = 0;

        List<Agent> neighborUnitList = getNeighborUnitList();

        for(Agent unit: neighborUnitList) {
            AgentActionEnum action = unit.getPreviousAction();

            if(action.equals(AgentActionEnum.NOT_REPAIR)) {
                defectionCount++;
            }
        }

        RepairStrategy strategy = getCurrentStrategy();
        AgentGameActionEnum[] code = strategy.getStrategyCodeArray();

        if(code[defectionCount].equals(AgentGameActionEnum.COOPERATION)) {
            setAction(AgentActionEnum.REAPIR);
        } else if(code[defectionCount].equals(AgentGameActionEnum.DEFECTION)) {
            setAction(AgentActionEnum.NOT_REPAIR);
        } else if(code[defectionCount].equals(AgentGameActionEnum.REVERSE)) {
            AgentActionEnum previousAction = getPreviousAction();

            if(previousAction.equals(AgentGameActionEnum.COOPERATION)) {
                setAction(AgentActionEnum.NOT_REPAIR);
            } else {
                setAction(AgentActionEnum.REAPIR);
            }

            assert false; //使ってないから
        } else if(code[defectionCount].equals(AgentGameActionEnum.STAY)) {
            AgentActionEnum previousAction = getPreviousAction();

            setAction(previousAction);

            assert false; //使ってないから
        } else {
            System.err.println("No such strategy code.");
            System.exit(1);
        }

        return getAction();
    }

    /**
     * @return the strategyUpdateErrorRate_
     */
    public double getStrategyUpdateErrorRate() {
        return strategyUpdateErrorRate_;
    }

    /**
     * @param strategyUpdateErrorRate_ the strategyUpdateErrorRate_ to set
     */
    public void setStrategyUpdateErrorRate(double strategyUpdateErrorRate) {
        this.strategyUpdateErrorRate_ = strategyUpdateErrorRate;
    }

    /**
     * @return the previousStrategy_
     */
    public RepairStrategy getPreviousStrategy() {
        return previousStrategy_;
    }

    /**
     * @param previousStrategy_ the previousStrategy_ to set
     */
    public void setPreviousStrategy(RepairStrategy previousStrategy) {
        this.previousStrategy_ = previousStrategy;
    }

    /**
     * @return the currentStrategy_
     */
    public RepairStrategy getCurrentStrategy() {
        return currentStrategy_;
    }

    /**
     * @param currentStrategy_ the currentStrategy_ to set
     */
    public void setCurrentStrategy(RepairStrategy currentStrategy) {
        this.currentStrategy_ = currentStrategy;
    }

    /**
     * @return the strategies_
     */
    public List<RepairStrategy> getStrategyList() {
        return strategyList_;
    }

    /**
     * @param strategies_ the strategies_ to set
     */
    public void setStrategyList(List<RepairStrategy> strategyList) {
        this.strategyList_ = strategyList;
    }

    public void addAllStrategy(List<RepairStrategy> strategies) {
        List<RepairStrategy> strategyList = getStrategyList();

        strategyList.addAll(strategies);
    }

    public void addStrategy(RepairStrategy strategy) {
        List<RepairStrategy> strategyList = getStrategyList();

        strategyList.add(strategy);
    }

}
