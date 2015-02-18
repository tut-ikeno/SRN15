/*
 * AsynchronousRepairUnit.java
 *
 * Created on October 29, 2007, 4:42 PM
 *
 */
package jp.ac.tut.tutkie.sys.srn.synchrony;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.RepairHistory;
import jp.ac.tut.tutkie.sys.srn.core.RepairResultEnum;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;
//import static jp.ac.tut.tutkie.sys.srn.SelfRepairNetworkModel.REPAIRING_NEIGHBOR_UNIT_COUNT;
/**
 *
 * @author tokumitsu
 */
public class SynchronousRepairUnit extends Agent {

    private Random random = null;

    /** 修復試行確率 */
    private double pr = 0.0;

    private RepairHistory repairHistory = null;
    private List<RepairHistory> repairHistoryList = null;

    /**
     * Creates a new instance of AsynchronousRepairUnit
     */
    public SynchronousRepairUnit(Environment environment) {
        super(environment);

        random = new Random();

        repairHistory = new RepairHistory();
        repairHistoryList = new ArrayList<RepairHistory>();
    }

    @Override
    public void doTask() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        for (Agent unit : neighborUnitList) {
            AgentActionEnum action = updateAction();

            if (action == AgentActionEnum.REAPIR) {
                repairNeighborUnit(unit);
            }
        }
    }

    @Override
    public void init() {
    }

    @Override
    public boolean repairNeighborUnit(Agent unit) {
        AgentStatusEnum status = getStatus();

        RepairHistory history = repairHistory.createClone();
        
        history.setSourceUnitStatus(status);

        boolean success = false;

        if (status == AgentStatusEnum.NORMAL) {
            double prn = getPrn();

            if (random.nextDouble() < prn) {
                history.setRepairResult(RepairResultEnum.SUCCESSFUL);

                success = true;
            } else {
                history.setRepairResult(RepairResultEnum.FAILURE);
            }
        } else {
            double pra = getPra();

            if (random.nextDouble() < pra) {
                history.setRepairResult(RepairResultEnum.SUCCESSFUL);

                success = true;
            } else {
                history.setRepairResult(RepairResultEnum.FAILURE);
            }
        }

        ((SynchronousRepairUnit) unit).addRepairHistory(history);

        return success;
    }

    @Override
    public void updateParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateStatus() {
       if(repairHistoryList.isEmpty()) {
            return;
        }

        boolean success = true;
        
        for(RepairHistory history:repairHistoryList) {
            RepairResultEnum result = history.getRepairResult();

            success = success && result.equals(RepairResultEnum.SUCCESSFUL);

            if(!success) {
                break;
            }
        }

        if(success) {
            setStatus(AgentStatusEnum.NORMAL);
        } else {
            setStatus(AgentStatusEnum.ABNORMAL);
        }

        repairHistoryList.clear();
    }

    @Override
    public AgentActionEnum updateAction() {
        double pr = getPr();

        if(random.nextDouble() < pr) {
            setAction(AgentActionEnum.REAPIR);
        } else {
            setAction(AgentActionEnum.NOT_REPAIR);
        }

        return getAction();
    }

    @Override
    public void fail() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addRepairHistory(RepairHistory history) {
        repairHistoryList.add(history);
    }

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

}
