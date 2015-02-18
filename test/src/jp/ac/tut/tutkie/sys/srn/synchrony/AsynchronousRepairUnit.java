/*
 * AsynchronousRepairUnit.java
 *
 * Created on October 29, 2007, 4:42 PM
 *
 */
package jp.ac.tut.tutkie.sys.srn.synchrony;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;
import org.omg.PortableInterceptor.SUCCESSFUL;
//import static jp.ac.tut.tutkie.sys.srn.SelfRepairNetworkModel.REPAIRING_NEIGHBOR_UNIT_COUNT;

/**
 *
 * @author tokumitsu
 */
public class AsynchronousRepairUnit extends Agent {

    private Random random = null;

    /** 修復試行確率 */
    private double pr = 0.0;

    private LinkedList<Agent> targetNeighborUnitList = null;

    /**
     * Creates a new instance of AsynchronousRepairUnit
     */
    public AsynchronousRepairUnit(Environment environment) {
        super(environment);

        random = new Random();
        targetNeighborUnitList = new LinkedList<Agent>();
    }

    @Override
    public void doTask() {
        LinkedList<Agent> list = getTargetNeighborUnitList();

        AgentActionEnum action = updateAction();

        Agent unit = list.removeFirst();

        if (action == AgentActionEnum.REAPIR) {
            repairNeighborUnit(unit);
        }

    }

    @Override
    public void init() {
    }

    @Override
    public boolean repairNeighborUnit(Agent unit) {
        AgentStatusEnum status = getStatus();

        boolean success = false;

        if (status == AgentStatusEnum.NORMAL) {
            double prn = getPrn();

            if (random.nextDouble() < prn) {
                unit.setStatus(AgentStatusEnum.NORMAL);

                success = true;
            } else {
                unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        } else {
            double pra = getPra();

            if (random.nextDouble() < pra) {
                unit.setStatus(AgentStatusEnum.NORMAL);

                success = true;
            } else {
                unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        }

        return success;
    }

    @Override
    public void updateParameters() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        addTargetUnit(neighborUnitList);
    }

    @Override
    public void updateStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

    public void addTargetUnit(List<Agent> subjectUnitList) {
        Collections.shuffle(subjectUnitList);

        List<Agent> list = getTargetNeighborUnitList();

        list.addAll(subjectUnitList);
    }

    public LinkedList<Agent> getTargetNeighborUnitList() {
        return targetNeighborUnitList;
    }

    public void setTargetNeighborUnitList(LinkedList<Agent> list) {
        this.targetNeighborUnitList = list;
    }

}
