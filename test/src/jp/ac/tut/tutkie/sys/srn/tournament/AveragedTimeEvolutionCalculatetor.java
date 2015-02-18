/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author tokumitsu
 */
public class AveragedTimeEvolutionCalculatetor {
    
    private static final String timeEvolutionLogFile = "average_of_time_evolution_at_each_step.log";
    
    public static void main(String[] args) throws Exception {
        int step = Integer.parseInt(args[0]);
        int trialCount = Integer.parseInt(args[1]);

        int strategyCount = 2;
        int distributionRowCount = 3;

        double[][] evolutions = new double[step + 1][distributionRowCount];
        Map<String, Map<DataFieldName, List<Double>>> strategyMeasuredValues = new HashMap<String, Map<DataFieldName, List<Double>>>();

        for (int i = 1; i <= trialCount; i++) {
            Scanner scanner = new Scanner(new File(i + ".log"));

            for (int j = 0; j <= step; j++) {
                for (int k = 0; k < distributionRowCount; k++) {
                    if (scanner.hasNextDouble()) {
                       evolutions[j][k] += scanner.nextDouble();
                    } else {
                        assert false;
                    }
                }

                for(int k = 0; k < strategyCount; k++) {
                    String strategy = scanner.next();

                    if((i == 1) && (j == 0)) { //試行回数が1回目でステップが0の場合
                        Map<DataFieldName, List<Double>> measuredValues = new HashMap<DataFieldName, List<Double>>();

                        measuredValues.put(DataFieldName.DISTRIBUTION, new ArrayList<Double>());
                        measuredValues.put(DataFieldName.NORMAL_AGETNTS, new ArrayList<Double>());
                        measuredValues.put(DataFieldName.COOPERATOR_AGENTS, new ArrayList<Double>());
                        measuredValues.put(DataFieldName.AVAILABLE_RESOURCE, new ArrayList<Double>());

                        strategyMeasuredValues.put(strategy, measuredValues);
                    }

                    if(i == 1) { //試行回数が1回目の場合
                        Map<DataFieldName, List<Double>> measuredValues = strategyMeasuredValues.get(strategy);

                        for (Map.Entry<DataFieldName, List<Double>> entry: measuredValues.entrySet()) {
                            List<Double> list = entry.getValue();

                            list.add(0.0);
                        }
                    }

                    Map<DataFieldName, List<Double>> measuredValues = strategyMeasuredValues.get(strategy);

                    List<Double> distributionList = measuredValues.get(DataFieldName.DISTRIBUTION);

                    double measuredDistribution = scanner.nextDouble() + distributionList.get(j);
                    distributionList.set(j, measuredDistribution);

                    List<Double> normalAgentsFractionList = measuredValues.get(DataFieldName.NORMAL_AGETNTS);

                    double measuredNormalAgentsFraction = scanner.nextDouble() + normalAgentsFractionList.get(j);
                    normalAgentsFractionList.set(j, measuredNormalAgentsFraction);

                    List<Double> cooperatorAgentsFractionList = measuredValues.get(DataFieldName.COOPERATOR_AGENTS);

                    double measuredCooperatorAgentsFraction = scanner.nextDouble() + cooperatorAgentsFractionList.get(j);
                    cooperatorAgentsFractionList.set(j, measuredCooperatorAgentsFraction);

                    List<Double> availableResourceList = measuredValues.get(DataFieldName.AVAILABLE_RESOURCE);

                    double measuredAvailableResource = scanner.nextDouble() + availableResourceList.get(j);
                    availableResourceList.set(j, measuredAvailableResource);
                }
            }
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(timeEvolutionLogFile));

        List<String> strategies = new ArrayList<String>();
        strategies.addAll(strategyMeasuredValues.keySet());

        List<DataFieldName> dataFieldNames = new ArrayList<DataFieldName>();

        dataFieldNames.add(DataFieldName.DISTRIBUTION);
        dataFieldNames.add(DataFieldName.NORMAL_AGETNTS);
        dataFieldNames.add(DataFieldName.COOPERATOR_AGENTS);
        dataFieldNames.add(DataFieldName.AVAILABLE_RESOURCE);

        for(int i = 0; i < evolutions.length; i++) {
            for(int j = 0; j < distributionRowCount; j++) {
                double value = evolutions[i][j] / trialCount;
                
                writer.write(value + " ");
            }
            
            for (String strategy : strategies) {
                Map<DataFieldName, List<Double>> measuredValues = strategyMeasuredValues.get(strategy);

                writer.write(strategy + " ");

                for (DataFieldName name : dataFieldNames) {
                    List<Double> list = measuredValues.get(name);

                    double value = list.get(i) / trialCount;

                    writer.write(value + " ");
                }
            }

            writer.newLine();
        }
        
        writer.flush();
        writer.close();
    }
 
    private enum DataFieldName {
        DISTRIBUTION,
        NORMAL_AGETNTS,
        COOPERATOR_AGENTS,
        AVAILABLE_RESOURCE;
    }

}
