/*
 * Environment.java
 *
 * Created on 2007/05/02, 18:56
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class Environment {

    private long currentTime_ = 0L;
    private List<Agent> agentList = null;
    
    public Environment() {
        agentList = new ArrayList<Agent>();
    }

    protected void setCurrentTime(long currentTime) {
        this.currentTime_ = currentTime;
    }
    
    public long getCurrentTime() {
        return currentTime_;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }

    protected void addAgent(Agent agent) {
        if(agent != null) {
            List<Agent> list = getAgentList();

            list.add(agent);
        }
    }
    
}
