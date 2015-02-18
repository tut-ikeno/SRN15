/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairStrategy;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentGameActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 *
 * @author tokumitsu
 */
public class StrategicRepairUnit extends UniformRepairUnit {

    private double strategyUpdateErrorRate_ = 0.0;

    private RepairStrategy previousStrategy_ = null;
    private RepairStrategy currentStrategy_ = null;

    private Random random = null;

    public StrategicRepairUnit(Environment environment) {
        super(environment);

        random = new Random();
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

        if (random.nextDouble() < strategyUpdateErrorRate) {
            RepairStrategy previousRepairStrategy = getPreviousStrategy();
            AgentGameActionEnum[] repairStrategyCode = previousRepairStrategy.getStrategyCodeArray();

            int length = repairStrategyCode.length;
            AgentGameActionEnum[] newRepairStrategyCode = new AgentGameActionEnum[length];

            AgentGameActionEnum action = null;

            if (repairStrategyCode[0].equals(AgentGameActionEnum.COOPERATION)) {
                action = AgentGameActionEnum.DEFECTION;
            } else {
                action = AgentGameActionEnum.COOPERATION;
            }

            for (int i = 0; i < newRepairStrategyCode.length; i++) {
                newRepairStrategyCode[i] = action;
            }

            RepairStrategy newRepairStrategy = new RepairStrategy(newRepairStrategyCode);
            setCurrentStrategy(newRepairStrategy);
        } else {
            RepairStrategy previousRepairStrategy = ((StrategicRepairUnit) maxScoreUnit).getPreviousStrategy();
            AgentGameActionEnum[] code = previousRepairStrategy.getStrategyCodeArray();

            RepairStrategy newRepairStrategy = new RepairStrategy(code);
            setCurrentStrategy(newRepairStrategy);
        }

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

            assert false;
        } else if(code[defectionCount].equals(AgentGameActionEnum.STAY)) {
            AgentActionEnum previousAction = getPreviousAction();

            setAction(previousAction);

            assert false;
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
    
}
