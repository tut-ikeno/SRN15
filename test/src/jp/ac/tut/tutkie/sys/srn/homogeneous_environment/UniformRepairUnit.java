/*
 * StrategicRepairUnit.java
 *
 * Created on October 29, 2007, 4:42 PM
 *
 */
package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.image.Renderable;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 *
 * @author tokumitsu
 */
public class UniformRepairUnit extends Agent implements Renderable {

    private Random random = null;

    /** 修復試行確率 */
    private double pr = 0.0;

    private LinkedList<Agent> targetNeighborUnitList = null;

    public static final int GRID_SIZE = 10;


    /**
     * Creates a new instance of StrategicRepairUnit
     */
    public UniformRepairUnit(Environment environment) {
        super(environment);

        random = new Random();
        targetNeighborUnitList = new LinkedList<Agent>();
    }

    @Override
    public void doTask() {
        LinkedList<Agent> list = getTargetNeighborUnitList();

        //AgentActionEnum action = updateAction();
        AgentActionEnum action = getAction();
        AgentStatusEnum status = getStatus();

        double availableResource = getAvailableResource();

        while (!list.isEmpty()) {
            Agent unit = list.removeFirst();

            if (status == AgentStatusEnum.NORMAL) {
                double repairResource = getRepairResource();

                if ((action == AgentActionEnum.REAPIR) && ((availableResource - repairResource) >= 0.0)) {
                    repairNeighborUnit(unit);

                    availableResource -= repairResource;
                }
            } else {
                if (action == AgentActionEnum.REAPIR) {
                    repairNeighborUnit(unit);
                }

                availableResource = 0.0;
            }
        }

        setAvailableResource(availableResource);
        
        double score = getScore() + availableResource;
        setScore(score);
    }

    @Override
    public void init() {
    }

    /* Stateパターンで処理するのがいいかも */
    @Override
    public boolean repairNeighborUnit(Agent unit) {
        AgentStatusEnum status = getStatus();

        boolean success = false;

//        assert false; //CISSE 2007のモデルになっているため、戻す必要あり。

        if (status == AgentStatusEnum.NORMAL) {
            double prn = getPrn();

            if (random.nextDouble() < prn) {
                unit.setStatus(AgentStatusEnum.NORMAL);
                unit.clearDamageRate();

                success = true;
            } else {
      //          unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        } else {
            double pra = getPra();
            double damageRate = getDamageRate();

            if (random.nextDouble() < pra) {
       //         unit.setStatus(AgentStatusEnum.NORMAL);
                unit.damage(damageRate);

                success = true;
            } else {
       //         unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        }

        return success;
    }

    @Override
    public void updateParameters() {
        List<Agent> neighborUnitList = getNeighborUnitList();

        addTargetUnitList(neighborUnitList);

        setAvailableResource(getMaxResource());
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
        double failureRate = getFailureRate();

        if(random.nextDouble() < failureRate) {
            setStatus(AgentStatusEnum.ABNORMAL);
            setAvailableResource(0.0);
        }
    }

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

    public void addTargetUnitList(List<Agent> subjectUnitList) {
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

    public void render(Graphics2D g) {
        AgentStatusEnum status = getStatus();
        AgentActionEnum action = getAction();

        Color color = null;

        if(status == AgentStatusEnum.NORMAL) {
            if(action == AgentActionEnum.REAPIR) {
                color = Color.blue;
            } else {
                color = Color.green;
            }
        } else {
            if(action == AgentActionEnum.REAPIR) {
                color = Color.yellow;
            } else {
                color = Color.red;
            }
        }

        Point p = getPoint();

        int x = p.x * GRID_SIZE + (p.x + 1) * UniformRepairSelfRepairNetworkModel.GRID_SPACE;
        int y = p.y * GRID_SIZE + (p.y + 1) * UniformRepairSelfRepairNetworkModel.GRID_SPACE;

        g.setColor(color);
        g.fillRect(x, y, GRID_SIZE, GRID_SIZE);
    }

}
