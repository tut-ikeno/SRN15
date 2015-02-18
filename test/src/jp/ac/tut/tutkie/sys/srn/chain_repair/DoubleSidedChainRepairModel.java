/*
 * AsynchronousStrategicRepairNetworkModel.java
 *
 * Created on October 29, 2007, 2:14 PM
 *
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import jp.ac.tut.tutkie.sys.srn.core.SelfRepairNetworkModel;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentFactory;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 * 右側のエージェントに連鎖的に修復するモデル
 * @author tokumitsu
 */
public class DoubleSidedChainRepairModel extends SelfRepairNetworkModel {

    private Map<SimulationParameterEnum, Number> map = null;
    private Dimension dimension = null;
    private String unitClassName = null;
    
    private Random random = null;
    private List<Agent> unitList = null;

    public DoubleSidedChainRepairModel(String[] args) {
        super(args);
        
        map = new HashMap<SimulationParameterEnum, Number>();
        random = new Random();
        unitList = new ArrayList<Agent>();
        
//        try {
//            System.setOut(new PrintStream("test.log"));
//        } catch(Exception e) {
//            e.printStackTrace();;
//        }
    }

    @Override
    public void init(Environment environment) {
        super.init(environment);
        
        List<Agent> list = getEnvironment().getAgentList();
        
        ChainRepairUnit repairStartUnit = null;
        
        int size = list.size();
        int center = size / 2;
        
        for(Agent agent: list) {
            ChainRepairUnit unit = (ChainRepairUnit) agent;
            
            if(unit.getPoint().getX() == center) {
                repairStartUnit = unit;
                break;
            }
        }
        
        Number number = map.get(SimulationParameterEnum.PROBABILITY_OF_REPAIRING_STARTS_FROM_NORMAL);
        double startingAgentNormalProbability = number.doubleValue();
        
        if(random.nextDouble() < startingAgentNormalProbability) {
            repairStartUnit.setStatus(AgentStatusEnum.NORMAL);
            repairStartUnit.setNextStatus(AgentStatusEnum.NORMAL);
        }
        
        repairStartUnit.passChainRepairToken(ChainRepairTokenEnum.RIGHT);
        repairStartUnit.passChainRepairToken(ChainRepairTokenEnum.LEFT);
    }

    @Override
    public void prepare() {
        postStep(0);  /* Dump initial configuration   */
    }

    @Override
    public void preStep(long time) {
        List<Agent> agentList =getEnvironment().getAgentList();

        for(Agent agent: agentList) {
            Agent unit = (Agent) agent;

            unit.updateAction();
        }
    }

    @Override
    public void step(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        for(Agent agent: agentList) {
            ChainRepairUnit unit = (ChainRepairUnit) agent;
            
            unit.doTask();
        }
    }
    
    @Override
    public void postStep(long time) {
        measureNetworkPerformance(time);
    }
    
    private void measureNetworkPerformance(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        char[] states = new char[agentList.size()];

        int count = 0;
  
        for(Agent agent: agentList) {
            Agent unit = (Agent) agent;
            
            AgentStatusEnum status = unit.getStatus();
            Point p = unit.getPoint();
            
            if(status.equals(AgentStatusEnum.NORMAL)) {
                states[p.x] = '0';
                count++;
            } else {
                states[p.x] = '1';
            }
            
            //if(unit.getAction() == AgentActionEnum.REAPIR) {
              //  states[p.x] = '#';
//                System.out.println(time + " " + p);
            //}
        }

        System.out.println(states);
        
        if(count == agentList.size()) {
            System.exit(0);
        }
        
//        for(int i = 0; i <= index; i++) {
//            System.out.print("#");
//        }
//        
//        System.out.println("");
        
    }
    
    @Override
    public void initAgents() {
        Map<SimulationParameterEnum, Number> map = getSimulationParametersMap();
        
        Number numberWidth = map.get(SimulationParameterEnum.SPACE_WIDTH);
        Number numberHeight = map.get(SimulationParameterEnum.SPACE_HEIGHT);
        
        int width = numberWidth.intValue();
        int height = numberHeight.intValue();

        Number number = map.get(SimulationParameterEnum.REPAIR_SUCCESS_RATE_BY_NORMAL);
        double prn = number.doubleValue();
        
        number = map.get(SimulationParameterEnum.REPAIR_SUCCESS_RATE_BY_ABNORMAL);
        double pra = number.doubleValue();

        AgentFactory factory = new AgentFactory(getEnvironment());
        String className = getUnitClassName();
        
        for(int x = 0; x < width; x++) {        
            for(int y = 0; y < height; y++) {
                Point point = new Point(x,y);
                
                ChainRepairUnit unit = null;
                
                try {
                    unit = (ChainRepairUnit) factory.create(className);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                unit.setPoint(point);
                unit.setPrn(prn);
                unit.setPra(pra);
                unit.setAction(AgentActionEnum.NOT_REPAIR);
                unit.setPreviousAction(AgentActionEnum.NOT_REPAIR);
                
                /* 修復に初期分布は無関係*/
                unit.setStatus(AgentStatusEnum.ABNORMAL);
                unit.setNextStatus(AgentStatusEnum.ABNORMAL);

                unitList.add(unit);
            }
        }
        
        setUnitList(unitList);
    }

    @Override
    public void initNetworkStructure() {
        assignPointToUnits();
        //shuffleUnitPoint();
    }
    
    private void assignPointToUnits() {
        List<Agent> agentList = getEnvironment().getAgentList();
        
        Dimension dimension = getDimension();
        int neighbors = 2;

        for (Agent agent : agentList) {
            Agent unit = (Agent) agent;

            Point point = unit.getPoint();
            
            Point p1 = new Point(point.x + 1, point.y);
            Point p2 = new Point(point.x - 1, point.y);

            p1.x = p1.x % dimension.width;
            p2.x = p2.x % dimension.width;
            
            /* マイナスの値に対する処理 */
            if(p2.x < 0) {
                p2.x = dimension.width - 1;
            }
            
            for (Agent neighborAgent : agentList) {
                Agent neighborUnit = (Agent) neighborAgent;
                Point neighborUnitPoint = neighborUnit.getPoint();

                if (neighborUnitPoint.equals(p1)
                        || neighborUnitPoint.equals(p2)) {
                    unit.addNeighborUnit(neighborUnit);
                    
                    if(unit.getNeighborUnitList().size() == neighbors) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void initSimulationParameters() {
        Map<SimulationParameterEnum, Number> map = getSimulationParametersMap();
        
        Properties properties = getProperties();
        Enumeration enumeration = getProperties().propertyNames();
        
        Class<SimulationParameterEnum> cClass = SimulationParameterEnum.class;
        
        while(enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String stringValue = (String) properties.get(key);
            
            SimulationParameterEnum name = Enum.valueOf(cClass, key);
            
            /*  苦肉の策    */
            if(name.equals(SimulationParameterEnum.UNIT_CLASS_NAME)) {
                setUnitClassName(stringValue);
                continue;
            }
            
            double value = 0.0;
            
            try {
                value = Double.parseDouble(stringValue);
            } catch(NumberFormatException e) {
                throw e;
            }
            
            map.put(name, value);
        }
        
        Number number = map.get(SimulationParameterEnum.SPACE_WIDTH);
        int width = number.intValue();
        
        number = map.get(SimulationParameterEnum.SPACE_HEIGHT);        
        int height = number.intValue();
        
        Dimension dimension = new Dimension(width, height);
        setDimension(dimension);
    }
    
    public void setSimulationParametersMap(Map<SimulationParameterEnum, Number> map) {
        this.map = map;
    }

    public Map<SimulationParameterEnum, Number> getSimulationParametersMap() {
        return map;
    }
    
    public String getUnitClassName() {
        return unitClassName;
    }
    
    public void setUnitClassName(String unitClassName) {
        this.unitClassName = unitClassName;
    }

    public Dimension getDimension() {
        return dimension;
    }
    
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public List<Agent> getUnitList() {
        return unitList;
    }
    
    public void setUnitList(List<Agent> unitList) {
        this.unitList = unitList;
    }
    
}
