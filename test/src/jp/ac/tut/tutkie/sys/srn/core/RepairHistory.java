/*
 * RepairHistory.java
 *
 * Created on November 21, 2007, 11:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author tokumitsu
 */
public class RepairHistory implements Cloneable {
    
    private AgentStatusEnum sourceUnitStatus = null;
    private RepairResultEnum repairResult = null;
    
    public RepairHistory() {}
    
    public RepairHistory(AgentStatusEnum repairingUnitStatus,
                  RepairResultEnum repairResult) {
        this.sourceUnitStatus = repairingUnitStatus;
        this.repairResult = repairResult;
    }
    
    public AgentStatusEnum getSourceUnitStatus() {
        return sourceUnitStatus;
    }
    
    public RepairResultEnum getRepairResult() {
        return repairResult;
    }
    
    public void setSourceUnitStatus(AgentStatusEnum status) {
        this.sourceUnitStatus = status;
    }
    
    public void setRepairResult(RepairResultEnum result) {
        this.repairResult = result;
    }
    
    public RepairHistory createClone() {
        RepairHistory history = null;
        
        try {
            history = (RepairHistory) clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        return history;
    }

    public String toString() {
        return sourceUnitStatus.toString() + "/" + repairResult.toString();
    }
    
}
