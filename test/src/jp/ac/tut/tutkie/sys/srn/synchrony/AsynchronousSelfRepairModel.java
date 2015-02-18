/*
 * AsynchronousStrategicRepairNetworkModel.java
 *
 * Created on October 29, 2007, 2:14 PM
 *
 */

package jp.ac.tut.tutkie.sys.srn.synchrony;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
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
 * 同期的な修復モデル
 * @author tokumitsu
 */
public class AsynchronousSelfRepairModel extends SelfRepairNetworkModel {

    private Map<SimulationParameterEnum, Number> map = null;
    private Dimension dimension = null;
    private String unitClassName = null;
    
    private Random random = null;
    private List<Agent> unitList = null;

    public AsynchronousSelfRepairModel(String[] args) {
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

        Map<SimulationParameterEnum, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameterEnum.NORMAL_PROBABILITY_AT_INITIAL);
        double normalProbability = number.doubleValue();

        List<Agent> list = getUnitList();

        Collections.shuffle(list);

        int abnormalCount = (int) (list.size() * normalProbability);

        for(int i = 0; i < abnormalCount; i++) {
            Agent unit = list.get(i);

            unit.setStatus(AgentStatusEnum.ABNORMAL);
        }
    }

    @Override
    public void prepare() {
        postStep(0);  /* Dump initial configuration   */
    }

    @Override
    public void preStep(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        for(Agent agent: agentList) {
            Agent unit = (Agent) agent;

            unit.updateParameters();
        }

    }

    @Override
    public void step(long time) {
        Map<SimulationParameterEnum, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameterEnum.NEIGHBOR_UNIT_COUNT);
        int neighborUnitCount = number.intValue();

        for (int i = 1; i <= neighborUnitCount; i++) {
            List<Agent> agentList = getEnvironment().getAgentList();

            Collections.shuffle(agentList);

            for (Agent agent : agentList) {
                Agent unit = (Agent) agent;

                unit.doTask();
            }
        }

    }
    
    @Override
    public void postStep(long time) {
        measureNetworkPerformance(time);
    }
    
    private void measureNetworkPerformance(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        int count = 0;
  
        for(Agent agent: agentList) {
            Agent unit = (Agent) agent;
            
            AgentStatusEnum status = unit.getStatus();

            if(status.equals(AgentStatusEnum.NORMAL)) {
                count++;
            }
        }

        System.out.println(count);

        if(count == agentList.size()) {
            System.exit(0);
        }

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

        number = map.get(SimulationParameterEnum.REPAIR_TRIAL_RATE);
        double pr = number.doubleValue();

        AgentFactory factory = new AgentFactory(getEnvironment());
        String className = getUnitClassName();
        
        for(int x = 0; x < width; x++) {        
            for(int y = 0; y < height; y++) {
                Point point = new Point(x,y);
                
                AsynchronousRepairUnit unit = null;
                
                try {
                    unit = (AsynchronousRepairUnit) factory.create(className);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                unit.setPoint(point);
                unit.setPr(pr);
                unit.setPrn(prn);
                unit.setPra(pra);
                unit.setAction(AgentActionEnum.NOT_REPAIR);
                unit.setPreviousAction(AgentActionEnum.NOT_REPAIR);
                unit.setStatus(AgentStatusEnum.NORMAL);

                unitList.add(unit);
            }
        }
        
        setUnitList(unitList);
    }

    @Override
    public void initNetworkStructure() {
        List<Agent> agentList = getEnvironment().getAgentList();

        Dimension d = getDimension();

        for (Agent agent : agentList) {
            Agent unit = (Agent) agent;

            Point point = unit.getPoint();

            Point p1 = new Point(point.x + 1, point.y);
            Point p2 = new Point(point.x - 1, point.y);

            if(p1.x < 0) {
                p1.x = d.width - 1;
            }
            
            if(p2.x < 0) {
                p2.x = d.width - 1;
            }

            p1.x = p1.x % d.width;
            p2.x = p2.x % d.width;

            for (Agent neighborAgent : agentList) {
                Agent neighborUnit = (Agent) neighborAgent;
                Point neighborUnitPoint = neighborUnit.getPoint();

                if ((p1 != null) && neighborUnitPoint.equals(p1)) {
                    unit.addNeighborUnit(neighborUnit);
                    p1 = null;
                }
                if((p2 != null) && neighborUnitPoint.equals(p2)) {
                    unit.addNeighborUnit(neighborUnit);
                    p2 = null;
                }

                if((p1 == null) && (p2 == null)) {
                    break;
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
        
        Dimension d = new Dimension(width, height);
        setDimension(d);
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
