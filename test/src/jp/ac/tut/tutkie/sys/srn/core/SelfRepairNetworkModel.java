/*
 * SelfRepairNetworkModel.java
 *
 * Created on 2007/06/28, 16:21
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Masahiro Tokumitsu
 */
public abstract class SelfRepairNetworkModel {
    
    private Environment environment = null;
    private Properties properties = null;

    public SelfRepairNetworkModel(String[] args) {
        loadSimulationConfiguration(args);  
    }

    private void loadSimulationConfiguration(String[] args) {
        String filename = args[0];
        
        properties = new Properties();
        
        try {
            getProperties().load(new FileInputStream(filename));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void init(Environment environment) {
        this.environment = environment;
        
        initSimulationParameters();
        initAgents();
        initNetworkStructure();
    }

    public abstract void postStep(long time);
    public abstract void preStep(long time);
    public abstract void prepare();
    public abstract void step(long time);
    
    public abstract void initSimulationParameters();
    public abstract void initNetworkStructure();
    public abstract void initAgents();

    public Environment getEnvironment() {
        return environment;
    }

    public Properties getProperties() {
        return properties;
    }
    
}