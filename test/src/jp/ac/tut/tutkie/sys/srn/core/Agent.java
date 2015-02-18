/*
 * Agent.java
 *
 * Created on 2007/06/28, 16:19
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Masahiro Tokumitsu
 */
public abstract class Agent {
    
    /** ユニットの状態 */
    private AgentStatusEnum status_ = AgentStatusEnum.NORMAL;
    
    /** ユニットの状態 */
    private AgentStatusEnum nextStatus_ = null;
    
    /** ユニットの行動 */
    private AgentActionEnum action_ = null;

    /** 近傍のユニット一覧 */
    private List<Agent> neighborUnitList_ = null;
   
    /** 空間における座標  */
    private Point point_ = null;
    
    /** 故障率関数*/
    private FailureRateFunction function_ = null;

    /** 最大リソース量   */
    private double maxResource_ = 0.0;
    
    /** 修復リソース量   */
    private double repairResource_ = 0.0;
    
    /** ユニットの前回の行動   */
    private AgentActionEnum previousAction_ = null;

    /** スコア */
    private double score_ = 0.0;
    
    /** 利用可能リソース量   */
    private double availableResource_ = 0.0;

    /** 正常エージェントの修復成功確率  */
    private double prn_ = 0.0;
    
    /** 異常エージェントの修復成功確率  */
    private double pra_ = 0.0;

    /** 悪影響量    */
    private double damageRate_ = 0.0;
    
    /** 蓄積悪影響量  */
    private double infectedDamageRate_ = 0.0;

    private Environment environment_ = null;

    /** Creates a new instance of Agent */
    public Agent(Environment environment) {
        setEnvironment(environment);
        
        neighborUnitList_ = new ArrayList<Agent>();
    }
    
    public void damage(double damageRate) {
        double infectedDamageRate = getInfectedDamageRate();
        
        infectedDamageRate += damageRate;
        
        setInfectedDamageRate(infectedDamageRate);
    }

    public abstract void init();
    public abstract void fail();
    public abstract void doTask();
    public abstract AgentActionEnum updateAction();
    public abstract void updateStatus();
    public abstract boolean repairNeighborUnit(Agent unit);
    public abstract void updateParameters();
    
    public void clearDamageRate() {
        setInfectedDamageRate(0.0);
    }

    public AgentStatusEnum getStatus() {
        return status_;
    }

    public void setStatus(AgentStatusEnum status) {
        this.status_ = status;
    }

    public void setNextStatus(AgentStatusEnum nextStatus) {
        this.nextStatus_ = nextStatus;
    }
    
    public AgentStatusEnum getNextStatus() {
        return nextStatus_;
    }

    public List<Agent> getNeighborUnitList() {
        return neighborUnitList_;
    }
    
    public void addNeighborUnit(Agent neighborUnit) {
        neighborUnitList_.add(neighborUnit);
    }

    public void setNeighborUnitList(List<Agent> neighborUnitList) {
        this.neighborUnitList_ = neighborUnitList;
    }

    public Point getPoint() {
        return point_;
    }

    public void setPoint(Point point) {
        this.point_ = point;
    }

    public FailureRateFunction getFailureRateFunction() {
        return function_;
    }
    
    public void setFailureRateFunction(FailureRateFunction function) {
        this.function_ = function;
    }
    
    public void setAction(AgentActionEnum action) {
        this.action_ = action;
    }
    
    public AgentActionEnum getAction() {
        return action_;
    }
    
    public void setPreviousAction(AgentActionEnum action) {
        this.previousAction_ = action;
    }
    
    public AgentActionEnum getPreviousAction() {
        return previousAction_;
    }
    
    public void setMaxResource(double value) {
        this.maxResource_ = value;
    }
    
    public double getMaxResource() {
        return maxResource_;
    }
    
    public void setRepairResource(double value) {
        this.repairResource_ = value;
    }
    
    public double getRepairResource() {
        return repairResource_;
    }
       
    public void setScore(double score) {
        this.score_ = score;
    }
    
    public double getScore() {
        return score_;
    }
    
    public double getAvailableResource() {
        return availableResource_;
    }
    
    public void setAvailableResource(double value) {
        this.availableResource_ = value;
    }

    public FailureRateFunction getFunction() {
        return function_;
    }

    public double getPrn() {
        return prn_;
    }
    
    public void setPrn(double prn) {
        this.prn_ = prn;
    }
    
    public double getPra() {
        return pra_;
    }
    
    public void setPra(double pra) {
        this.pra_ = pra;
    }
    
    public double getDamageRate() {
        return damageRate_;
    }
    
    public void setDamageRate(double damageRate) {
        this.damageRate_ = damageRate;
    }
    
    public double getInfectedDamageRate() {
        return infectedDamageRate_;
    }
    
    public void setInfectedDamageRate(double infectedDamageRate) {
        this.infectedDamageRate_ = infectedDamageRate;
    }
    
    public double getFailureRate() {
        long time = getEnvironment().getCurrentTime();
        
        FailureRateFunction function = getFailureRateFunction();
        
        double infectedDamageRate = getInfectedDamageRate();
        double failureRate = function.getValue(time);
        
        failureRate += infectedDamageRate;
        
        if(failureRate > 1.0) {
            failureRate = 1.0;
        }
        
        return failureRate;
    }
    
    public String toString() {
        AgentStatusEnum status = getStatus();
        AgentActionEnum action = getAction();
        
        String cell = null;
        
        if(status.equals(AgentStatusEnum.NORMAL)
           && action.equals(AgentActionEnum.REAPIR)) {
            cell = "NR";
        } else if(status.equals(AgentStatusEnum.NORMAL)
           && action.equals(AgentActionEnum.NOT_REPAIR)) {
            cell = "NS";
        } else if(status.equals(AgentStatusEnum.ABNORMAL)
           && action.equals(AgentActionEnum.REAPIR)) {
            cell = "AR";
        } else {
            cell = "AS";
        }

        return cell;
    }

    public Environment getEnvironment() {
        return environment_;
    }

    public void setEnvironment(Environment environment) {
        this.environment_ = environment;
    }
    
}
