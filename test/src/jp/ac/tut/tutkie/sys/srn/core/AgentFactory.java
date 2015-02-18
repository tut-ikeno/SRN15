/*
 * AgentFactory.java
 *
 * Created on 2007/05/02, 19:28
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class AgentFactory {
    
    private Environment environment = null;
    private Class constructorParameters = Environment.class;
    
    /** Creates a new instance of AgentFactory */
    public AgentFactory(Environment environment) {
        this.environment = environment;
    }
    
    public Agent create(String className) 
        throws ClassNotFoundException {
        if(className == null) {
            throw new NullPointerException();
        }
        
        Class agentClass = Class.forName(className);
        Constructor constructor = null;
        
        try {
            constructor = agentClass.getConstructor(constructorParameters);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        Agent agent = null;
        
        try { 
            agent = (Agent) constructor.newInstance(environment);
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        }

        environment.addAgent(agent);
        
        return agent;
    }
    
}
