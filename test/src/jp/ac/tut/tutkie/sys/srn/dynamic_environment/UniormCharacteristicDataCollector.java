/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.dynamic_environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * ls -1 |grep strategy_update_cycle. > list
 * @author tokumitsu
 */
public class UniormCharacteristicDataCollector {

    public static final String TO = "../characteristic";

    private List<Map<String, Number>> dataList = null;

    public static void main(String[] args) throws Exception {
        StrategicCharacteristicDataCollector collector = new StrategicCharacteristicDataCollector();

        collector.collect(args);
        collector.dumpFailureRate();
        collector.dumpFailureRateCycle();
        //collector.dumpStrategyUpdateCycle();
    }


    /**
     * 故障率の周期が一定で、故障率が変化した場合
     */
    public void dumpFailureRate() throws Exception {
        for(Map<String, Number> data: dataList) {
            int stratgyUpdateCycle = data.get("STRATEGY_UPDATE_CYCLE").intValue();
            int failureRateCycle = data.get("FAILURE_RATE_CYCLE").intValue();

            String directoryName = TO + "/strategy_udpate_cycle." + stratgyUpdateCycle
                    + ".failure_rate_cycle." + failureRateCycle;

            File directory = new File(directoryName);

            if(!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directoryName + "/failure_rate.log");
            
            if(!file.exists()) {
                file.createNewFile();
            }

            double failureRate = data.get("FAILURE_RATE").doubleValue();
            double fractionOfNormalAgents = data.get("FractionOfNormalAgents").doubleValue();
            double fractionOfRepairAgents = data.get("FractionOfRepairAgents").doubleValue();
            double averagedAvailableResource = data.get("AveragedAvailabeResource").doubleValue();

            StringBuilder result = new StringBuilder();

            result.append(failureRate + " ");
            result.append(fractionOfNormalAgents + " ");
            result.append(fractionOfRepairAgents + " ");
            result.append(averagedAvailableResource);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.append(result.toString());
            writer.newLine();
            
            writer.flush();
            writer.close();
        }
    }

    /**
     * 故障率が一定で、故障率の周期が変化した場合
     */
    public void dumpFailureRateCycle() throws IOException {
        for (Map<String, Number> data : dataList) {
            int stratgyUpdateCycle = data.get("STRATEGY_UPDATE_CYCLE").intValue();
            double failureRate = data.get("FAILURE_RATE").doubleValue();

            String directoryName = TO + "/strategy_udpate_cycle." + stratgyUpdateCycle + ".failure_rate." + failureRate;

            File directory = new File(directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directoryName + "/failure_rate_cycle.log");

            if (!file.exists()) {
                file.createNewFile();
            }

            int failureRateCycle = data.get("FAILURE_RATE_CYCLE").intValue();
            double fractionOfNormalAgents = data.get("FractionOfNormalAgents").doubleValue();
            double fractionOfRepairAgents = data.get("FractionOfRepairAgents").doubleValue();
            double averagedAvailableResource = data.get("AveragedAvailabeResource").doubleValue();

            StringBuilder result = new StringBuilder();

            result.append(failureRateCycle + " ");
            result.append(fractionOfNormalAgents + " ");
            result.append(fractionOfRepairAgents + " ");
            result.append(averagedAvailableResource);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.append(result.toString());
            writer.newLine();

            writer.flush();
            writer.close();
        }
    }

    /**
     * 故障率が一定で、故障率の周期が変化した場合
     */
    public void dumpStrategyUpdateCycle() throws IOException {
      for (Map<String, Number> data : dataList) {
            double failureRateCycle = data.get("FAILURE_RATE_CYCLE").doubleValue();
            double failureRate = data.get("FAILURE_RATE").doubleValue();

            String directoryName = TO + "/failure_rate_cycle." + failureRateCycle + ".failure_rate." + failureRate;

            File directory = new File(directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directoryName + "/strategy_update_cycle.log");

            if (!file.exists()) {
                file.createNewFile();
            }

            int strategyUpdateCycle = data.get("STRATEGY_UPDATE_CYCLE").intValue();
            double fractionOfNormalAgents = data.get("FractionOfNormalAgents").doubleValue();
            double fractionOfRepairAgents = data.get("FractionOfRepairAgents").doubleValue();
            double averagedAvailableResource = data.get("AveragedAvailabeResource").doubleValue();

            StringBuilder result = new StringBuilder();

            result.append(strategyUpdateCycle + " ");
            result.append(fractionOfNormalAgents + " ");
            result.append(fractionOfRepairAgents + " ");
            result.append(averagedAvailableResource);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.append(result.toString());
            writer.newLine();

            writer.flush();
            writer.close();
        }
    }

    public void collect(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File(args[0]));

        dataList = new ArrayList<Map<String, Number>>();

        while(scanner.hasNext()) {
            String directory = scanner.next();

            System.out.println(directory);

            Map<String, Number> result = load(directory);

            dataList.add(result);
        }
    }

    public Map<String, Number> load(String directory) throws Exception {
        Properties properties = new Properties();

        properties.load(new FileInputStream(new File(directory + "/simulation_parameters.list")));

        Map<String, Number> data = new HashMap<String, Number>();

        double strategyUpdateCycle = Double.parseDouble(properties.getProperty("STRATEGY_UPDATE_CYCLE"));
        double failureRate = Double.parseDouble(properties.getProperty("FAILURE_RATE"));
        double failureRateCycle = Double.parseDouble(properties.getProperty("FAILURE_RATE_CYCLE"));

        Scanner scanner = new Scanner(new File(directory + "/average_of_time_evolution.log"));

        double fractionOfNormalAgents = scanner.nextDouble();
        double fractionOfRepairAgents = scanner.nextDouble();
        double averagedAvailableResource = scanner.nextDouble();

        data.put("STRATEGY_UPDATE_CYCLE", strategyUpdateCycle);
        data.put("FAILURE_RATE", failureRate);
        data.put("FAILURE_RATE_CYCLE", failureRateCycle);

        data.put("FractionOfNormalAgents", fractionOfNormalAgents);
        data.put("FractionOfRepairAgents", fractionOfRepairAgents);
        data.put("AveragedAvailabeResource", averagedAvailableResource);

        return data;
    }

}
