/*
 * AsynchronousStrategicRepairNetworkModel.java
 *
 * Created on October 29, 2007, 2:14 PM
 *
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import jp.ac.tut.tutkie.sys.srn.core.ConstantFailureRateFunction;
import jp.ac.tut.tutkie.sys.srn.core.FailureRateFunction;
import jp.ac.tut.tutkie.sys.srn.core.NetworkCreator;
import jp.ac.tut.tutkie.sys.srn.core.SelfRepairNetworkModel;
import jp.ac.tut.tutkie.sys.srn.core.SquareLatticeNetworkCreator;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.core.AgentFactory;
import jp.ac.tut.tutkie.sys.srn.core.AgentActionEnum;
import jp.ac.tut.tutkie.sys.srn.core.AgentStatusEnum;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 * 同期的な修復モデル
 * @author tokumitsu
 */
public class UniformRepairSelfRepairNetworkModel extends SelfRepairNetworkModel {

    private Map<String, Number> map = null;
    private Dimension dimension = null;
    private String unitClassName = null;
    
    private Random random = null;
    private List<Agent> unitList = null;

    public static final int GRID_SPACE = 2;

    public UniformRepairSelfRepairNetworkModel(String[] args) {
        super(args);
        
        map = new HashMap<String, Number>();
        random = new Random();
        unitList = new ArrayList<Agent>();
        
//        try {
//            System.setOut(new PrintStream("uniform_repair_in_homogeneous_environment.log"));
//        } catch(Exception e) {
//            e.printStackTrace();;
//        }
    }

    @Override
    public void init(Environment environment) {
        super.init(environment);

        Map<String, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameters.NORMAL_PROBABILITY_AT_INITIAL);
        double normalProbability = number.doubleValue();

        List<Agent> list = getUnitList();

        Collections.shuffle(list);

        int abnormalCount = (int) (list.size() * normalProbability);

        for(int i = 0; i < abnormalCount; i++) {
            Agent unit = list.get(i);

            unit.setAvailableResource(0.0);
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
            unit.fail();
            unit.updateAction();
        }

    }

    @Override
    public void step(long time) {
      //  Map<SimulationParameters, Number> map = getSimulationParametersMap();

     //   Number number = map.get(SimulationParameters.NEIGHBOR_UNIT_COUNT);
    //    int neighborUnitCount = number.intValue();

        //for (int i = 1; i <= neighborUnitCount; i++) {
            List<Agent> agentList = getEnvironment().getAgentList();

            for (Agent agent : agentList) {
                Agent unit = (Agent) agent;

                unit.doTask();
            }
      //  }
    }
    
    @Override
    public void postStep(long time) {
        measureNetworkPerformance(time);

        Map<String, Number> map = getSimulationParametersMap();

        Number number = map.get(SimulationParameters.DUMP_NETWORK_CONFIGURATION);
        int dumpNetworkConfiguration = number.intValue();

        if(dumpNetworkConfiguration > 0) {
            dumpNetworkConfiguration(time);
        }
    }

    public void dumpNetworkConfiguration(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        Dimension d = getDimension();

        int width = d.width * UniformRepairUnit.GRID_SIZE + (d.width + 1) * GRID_SPACE;
        int height = d.width * UniformRepairUnit.GRID_SIZE + (d.width + 1) * GRID_SPACE;

        Dimension size = new Dimension(width, height);
        BufferedImage configuration = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g = configuration.createGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, size.width, size.height);

        //エージェントの状態を描画
        for(Agent agent: agentList) {
            UniformRepairUnit unit = (UniformRepairUnit) agent;

            unit.render(g);
        }

        try {
            ImageIO.write(configuration, "PNG", new File(Long.toString(time) + ".png"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
//    public void measureNetworkPerformance(long time) {
//        List<Agent> agentList = getEnvironment().getAgentList();
//
//        int normalUnitCount = 0;
//        double resource = 0.0;
//        int repairUnitCount = 0;
//
//        for(Agent agent: agentList) {
//            Agent unit = (Agent) agent;
//
//            AgentStatusEnum status = unit.getStatus();
//
//            if(status == AgentStatusEnum.NORMAL) {
//                normalUnitCount++;
//            }
//
//            AgentActionEnum action = unit.getAction();
//
//            if(action == AgentActionEnum.REAPIR) {
//                repairUnitCount++;
//            }
//
//            resource += unit.getAvailableResource();
//        }
//
//        int size = agentList.size();
//
//        double normalUnitFrequency = ((double) normalUnitCount) / ((double) size);
//        double averagedResource = resource / size;
//        double repairUnitFrequency = ((double) repairUnitCount) / ((double) size);
//
//        System.out.println(normalUnitFrequency + " " + repairUnitFrequency + " " + averagedResource);
//    }

    /**
     * Web情報処理工学特論向けの情報出力
     * @param time
     */
    public void measureNetworkPerformance(long time) {
        List<Agent> agentList = getEnvironment().getAgentList();

        int normalUnitCount = 0;
        double resource = 0.0;
        int repairUnitCount = 0;

        String[][] configuration = new String[100][100];

        for(Agent agent: agentList) {
            Agent unit = (Agent) agent;

            AgentStatusEnum status = unit.getStatus();

            if(status == AgentStatusEnum.NORMAL) {
                normalUnitCount++;
            }

            AgentActionEnum action = unit.getAction();

            if(action == AgentActionEnum.REAPIR) {
                repairUnitCount++;
            }

            resource += unit.getAvailableResource();

            String cell = null;

            if((status == AgentStatusEnum.NORMAL) && (action == AgentActionEnum.REAPIR)) {
                cell = "NC";
            } else if((status == AgentStatusEnum.NORMAL) && (action == AgentActionEnum.NOT_REPAIR)) {
                cell = "ND";
            } else if((status == AgentStatusEnum.ABNORMAL) && (action == AgentActionEnum.REAPIR)) {
                cell = "AC";
            } else {
                cell = "AD";
            }
            
            Point p = agent.getPoint();

            configuration[p.x][p.y] = cell;
        }

        int size = agentList.size();

        double normalUnitFrequency = ((double) normalUnitCount) / ((double) size);
        double averagedResource = resource / size;
        double repairUnitFrequency = ((double) repairUnitCount) / ((double) size);

        System.out.println(normalUnitFrequency + " " + repairUnitFrequency + " " + averagedResource);

        for (int i = 0; i < configuration.length; i++) {
            for (int j = 0; j < configuration[i].length; j++) {
                System.out.print(configuration[i][j] + " ");
            }

            System.out.println("");
        }
        
    }
    
    @Override
    public void initAgents() {
        Map<String, Number> map = getSimulationParametersMap();
        
        Number numberWidth = map.get(SimulationParameters.SPACE_WIDTH);
        Number numberHeight = map.get(SimulationParameters.SPACE_HEIGHT);
        
        int width = numberWidth.intValue();
        int height = numberHeight.intValue();

        Number number = map.get(SimulationParameters.REPAIR_SUCCESS_RATE_BY_NORMAL);
        double prn = number.doubleValue();
        
        number = map.get(SimulationParameters.REPAIR_SUCCESS_RATE_BY_ABNORMAL);
        double pra = number.doubleValue();

        number = map.get(SimulationParameters.REPAIR_TRIAL_RATE);
        double pr = number.doubleValue();

        number = map.get(SimulationParameters.FAILURE_RATE);
        double failureRate = number.doubleValue();

        number = map.get(SimulationParameters.DAMAGE_RATE);
        double damageRate = number.doubleValue();

        number = map.get(SimulationParameters.MAX_RESOURCE);
        double maxResource = number.doubleValue();

        number = map.get(SimulationParameters.REPAIR_RESOURCE);
        double repairResource = number.doubleValue();

        AgentFactory factory = new AgentFactory(getEnvironment());
        String className = getUnitClassName();

        FailureRateFunction function = new ConstantFailureRateFunction(failureRate);
        
        for(int x = 0; x < width; x++) {        
            for(int y = 0; y < height; y++) {
                Point point = new Point(x,y);
                
                UniformRepairUnit unit = null;
                
                try {
                    unit = (UniformRepairUnit) factory.create(className);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                unit.setPoint(point);
                unit.setPr(pr);
                unit.setPrn(prn);
                unit.setPra(pra);
                unit.setAction(AgentActionEnum.NOT_REPAIR);
                unit.setStatus(AgentStatusEnum.NORMAL);
                unit.setAvailableResource(maxResource);
                unit.setMaxResource(maxResource);
                unit.setRepairResource(repairResource);
                unit.setFailureRateFunction(function);
                unit.setDamageRate(damageRate);

                unitList.add(unit);
            }
        }
        
        setUnitList(unitList);
    }

    @Override
    public void initNetworkStructure() {
        List<Agent> list = getUnitList();
        Dimension d = getDimension();

        Map<String, Number> map = getSimulationParametersMap();

        Number numberRadius = map.get(SimulationParameters.NEIGHBOR_RADIUS);
        int radius = numberRadius.intValue();

        NetworkCreator creator = new SquareLatticeNetworkCreator(unitList, dimension, radius);

        creator.create();

        for(Agent unit: list) {
            List<Agent> neighborUnitList = unit.getNeighborUnitList();

            ((UniformRepairUnit) unit).addTargetUnitList(neighborUnitList);
        }
    }

    @Override
    public void initSimulationParameters() {
        Map<String, Number> map = getSimulationParametersMap();
        
        Properties properties = getProperties();
        Enumeration enumeration = getProperties().propertyNames();
        
        Class<SimulationParameters> cClass = SimulationParameters.class;
        
        while(enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String stringValue = (String) properties.get(key);
            
            /*  苦肉の策    */
            if(key.equals(SimulationParameters.UNIT_CLASS_NAME)) {
                setUnitClassName(stringValue);
                continue;
            }
            
            double value = 0.0;
            
            try {
                value = Double.parseDouble(stringValue);
            } catch(NumberFormatException e) {
                throw e;
            }
            
            map.put(key, value);
        }
        
        Number number = map.get(SimulationParameters.SPACE_WIDTH);
        int width = number.intValue();
        
        number = map.get(SimulationParameters.SPACE_HEIGHT);
        int height = number.intValue();
        
        Dimension d = new Dimension(width, height);
        setDimension(d);
    }

    public void setSimulationParametersMap(Map<String, Number> map) {
        this.map = map;
    }

    public Map<String, Number> getSimulationParametersMap() {
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
