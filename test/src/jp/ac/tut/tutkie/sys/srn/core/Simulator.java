/*
 * Simulator.java
 *
 * Created on 2007/05/01, 22:22
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.util.List;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class Simulator {

    private SelfRepairNetworkModel model = null;
    private Environment environment = null;
    
    private long maxStepCount = 1L;
    private long currentStepCount = 1L;
    
    private Simulator() {
    }
    
    /** Creates a new instance of Simulator */
    public Simulator(SelfRepairNetworkModel model, long maxStepCount) {
        this.model = model;
        this.maxStepCount = maxStepCount;
    
        environment = new Environment();
    }

    
    public void prepare() {
        model.init(environment);
        model.prepare();

        List<Agent> agentList = environment.getAgentList();
        
        for(Agent agent: agentList) {
            agent.init();
        }
        
    }
    
//    public void start() {
//        while(!isTerminated()){
//            progress();
//        }
//    }
    
//    private void progressAgents() {
//        AgentAggregator aggregator = environment.getAgentAggregator();
//        Iterator<Agent> iterator = aggregator.iterator();
//
//        while(iterator.hasNext()) {
//            Agent agent = iterator.next();
//
//            agent.doTask();
//        }
//    }
    
    public void progress() {
        environment.setCurrentTime(currentStepCount);
        
        model.preStep(currentStepCount);
        model.step(currentStepCount);

        //progressAgents();
        
        model.postStep(currentStepCount);
        
        currentStepCount++;
    }
    
    public boolean isTerminated() {
        return !(currentStepCount <= maxStepCount);
    }

    public long getCurrentStepCount() {
        return currentStepCount;
    }
    
    public void restart() {
        model.init(environment);
    }
    
}
