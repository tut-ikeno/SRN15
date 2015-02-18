/*
 * Main.java
 *
 * Created on 2007/05/01, 22:09
 *
 */

package jp.ac.tut.tutkie.sys.srn.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import jp.ac.tut.tutkie.sys.srn.core.Environment;
import jp.ac.tut.tutkie.sys.srn.core.SelfRepairNetworkModel;
import jp.ac.tut.tutkie.sys.srn.core.SelfRepairNetworkModelFactory;
import jp.ac.tut.tutkie.sys.srn.core.Simulator;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class Main {

    private String configFile = "SimulatorConfiguration.properties";
    
    private Properties configuration = null;
    private SelfRepairNetworkModel model = null;
    private Simulator simulator = null;
    
    /** Creates a new instance of Main */
    public Main(String[] args) {
        if(args.length >= 2) {
            if(args[0].equals("-config")) {
                configFile = args[1];
            }
            
            String[] newArgs = new String[args.length - 2];

            for (int i = 2; i < args.length; i++) {
                newArgs[i - 2] = args[i];
            }
            
            args = newArgs;
        }
        
        readSimulationConfiguration();
        createSimulationModel(args);
        prepareSimulation();
    }
    
    public static void main(String[] args) {
        Main main = new Main(args);
        
        main.start();
    }
    
    public void start() {
        simulator.prepare();

        while(!simulator.isTerminated()) {
            simulator.progress();
        }
    }
    
    private void createSimulationModel(String[] args) {
        SelfRepairNetworkModelFactory factory = new SelfRepairNetworkModelFactory(args);
        
        String className = configuration.getProperty("ModelClassName");
        
        try {
            model = factory.create(className);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void prepareSimulation() {
        String maxStepString = configuration.getProperty("SimulationMaxStepCount");
        
        long maxStepCount = 0L;
        
        try {
            maxStepCount = Long.parseLong(maxStepString);
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        
        simulator = new Simulator(model, maxStepCount);
    }

    private void readSimulationConfiguration() {
        configuration = new Properties();
        
        try {
            configuration.load(new FileInputStream(configFile));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}
