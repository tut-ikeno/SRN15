/*
 * AsynchronousRepairUnit.java
 *
 * Created on October 29, 2007, 4:42 PM
 *
 */
package jp.ac.tut.tutkie.sys.srn.chain_repair;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;
//import static jp.ac.tut.tutkie.sys.srn.SelfRepairNetworkModel.REPAIRING_NEIGHBOR_UNIT_COUNT;
/**
 *
 * @author tokumitsu
 */
public class ChainRepairUnit extends Agent {

    private Random random = null;

    /** 修復用隣接ユニットリスト    */
    private LinkedList<Agent> targetNeighborUnitList = null;
    
    private Set<ChainRepairTokenEnum> chainRepairTokenSet = null;
    private Set<ChainRepairTokenEnum> receivedChainRepairTokenSet = null;
    private Map<ChainRepairTokenEnum, Agent> neighborUnitMap = null;

    /**
     * Creates a new instance of AsynchronousRepairUnit
     */
    public ChainRepairUnit(Environment environment) {
        super(environment);

        random = new Random();
        chainRepairTokenSet = new HashSet<ChainRepairTokenEnum>();
        receivedChainRepairTokenSet = new HashSet<ChainRepairTokenEnum>();
        neighborUnitMap = new HashMap<ChainRepairTokenEnum, Agent>();
    }

    @Override
    public void doTask() {
        AgentActionEnum action = getAction();

        if (action == AgentActionEnum.REAPIR) {
            Set<ChainRepairTokenEnum> list = getChainRepairTokenSet();
            Map<ChainRepairTokenEnum, Agent>  map = getNeighborUnitMap();
            
            for (ChainRepairTokenEnum token: list) {
                ChainRepairUnit unit = (ChainRepairUnit) map.get(token);
                
                repairNeighborUnit(unit);
                
                //System.out.println(getPoint() + " " + unit.getPoint() + " " + token);
                
                unit.passChainRepairToken(token);
            }
            
            chainRepairTokenSet.clear();
            /* ルールが複雑化したら、updateParametersに処理を書く */
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

                success = false;
            } else {
                unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        } else {
            double pra = getPra();

            if (random.nextDouble() < pra) {
                unit.setStatus(AgentStatusEnum.NORMAL);

                success = false;
            } else {
                unit.setStatus(AgentStatusEnum.ABNORMAL);
            }
        }

        return success;
    }

    @Override
    public void updateParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AgentActionEnum updateAction() {
        Set<ChainRepairTokenEnum> tokenSet = getChainRepairTokenSet();
        Set<ChainRepairTokenEnum> receivedTokenSet = getReceivedChainRepairTokenSet();

        tokenSet.addAll(receivedTokenSet);
        receivedTokenSet.clear();
        
        if (tokenSet.size() > 0) {
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

    public void passChainRepairToken(ChainRepairTokenEnum token) {
        receivedChainRepairTokenSet.add(token);
    }
    
    public Set<ChainRepairTokenEnum> getReceivedChainRepairTokenSet() {
        return receivedChainRepairTokenSet;
    }
    
    public Set<ChainRepairTokenEnum> getChainRepairTokenSet() {
        return chainRepairTokenSet;
    }
    
    public Map<ChainRepairTokenEnum, Agent> getNeighborUnitMap() {
        return neighborUnitMap;
    }

    @Override
    public void addNeighborUnit(Agent neighborUnit) {
        super.addNeighborUnit(neighborUnit);
        
        Map<ChainRepairTokenEnum, Agent> map = getNeighborUnitMap();
        
        Point neighborUnitPoint = neighborUnit.getPoint();
        Point myPoint = getPoint();

        if (Math.abs(myPoint.x - neighborUnitPoint.x) == 1.0) {
            if (myPoint.x < neighborUnitPoint.x) {
                map.put(ChainRepairTokenEnum.RIGHT, neighborUnit);
            } else {
                map.put(ChainRepairTokenEnum.LEFT, neighborUnit);
            }
        } else {       
            if(myPoint.x < neighborUnitPoint.x) { /* 左端と接続 */
                map.put(ChainRepairTokenEnum.LEFT, neighborUnit);
            } else { /* 右端と接続 */
                map.put(ChainRepairTokenEnum.RIGHT, neighborUnit);
            }
        }
    }
    public void setTargetNeighborUnitList(LinkedList<Agent> targetNeighborUnitList) {
        this.targetNeighborUnitList = targetNeighborUnitList;
    }

    public LinkedList<Agent> getTargetNeighborUnitList() {
        return targetNeighborUnitList;
    }
    
}
