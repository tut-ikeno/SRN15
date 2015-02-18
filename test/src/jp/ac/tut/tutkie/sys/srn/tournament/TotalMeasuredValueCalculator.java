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
public class TotalMeasuredValueCalculator {

    private static final String timeEvolutionLogFile = "average_of_time_evolution_at_each_step.log";
    private static final String meanValueLogFile = "total_of_time_evolution.log";

    public static void main(String[] args) throws Exception {
       int step = Integer.parseInt(args[0]);

        int strategyCount = 2;
        int distributionRowCount = 3;

        double[] evolutions = new double[distributionRowCount];
        Map<String, Map<DataFieldName, Double>> strategyMeasuredValues = new HashMap<String, Map<DataFieldName, Double>>();

        Scanner scanner = new Scanner(new File(timeEvolutionLogFile));

        for (int j = 0; j <= step; j++) {
            for (int k = 0; k < distributionRowCount; k++) {
                if (scanner.hasNextDouble()) {
                    evolutions[k] += scanner.nextDouble();
                } else {
                    assert false;
                }
            }

            for (int k = 0; k < strategyCount; k++) {
                String strategy = scanner.next();

                if (j == 0) { //ステップが0の場合
                    Map<DataFieldName, Double> measuredValues = new HashMap<DataFieldName, Double>();

                    measuredValues.put(DataFieldName.DISTRIBUTION, 0.0);
                    measuredValues.put(DataFieldName.NORMAL_AGETNTS, 0.0);
                    measuredValues.put(DataFieldName.COOPERATOR_AGENTS, 0.0);
                    measuredValues.put(DataFieldName.AVAILABLE_RESOURCE, 0.0);

                    for (Map.Entry<DataFieldName, Double> entry : measuredValues.entrySet()) {
                        DataFieldName name = entry.getKey();

                        measuredValues.put(name, 0.0);
                    }

                    strategyMeasuredValues.put(strategy, measuredValues);
                }

                Map<DataFieldName, Double> measuredValues = strategyMeasuredValues.get(strategy);

                double distribution = measuredValues.get(DataFieldName.DISTRIBUTION);
                double measuredDistribution = scanner.nextDouble() + distribution;

                measuredValues.put(DataFieldName.DISTRIBUTION, measuredDistribution);

                double normalAgentsFraction = measuredValues.get(DataFieldName.NORMAL_AGETNTS);
                double measuredNormalAgentsFraction = scanner.nextDouble() + normalAgentsFraction;

                measuredValues.put(DataFieldName.NORMAL_AGETNTS, measuredNormalAgentsFraction);

                double cooperatorAgentsFraction = measuredValues.get(DataFieldName.COOPERATOR_AGENTS);
                double measuredCooperatorAgentsFraction = scanner.nextDouble() + cooperatorAgentsFraction;

                measuredValues.put(DataFieldName.COOPERATOR_AGENTS, measuredCooperatorAgentsFraction);

                double availableResource = measuredValues.get(DataFieldName.AVAILABLE_RESOURCE);
                double measuredAvailableResource = scanner.nextDouble() + availableResource;

                measuredValues.put(DataFieldName.AVAILABLE_RESOURCE, measuredAvailableResource);
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(meanValueLogFile));

        List<String> strategies = new ArrayList<String>();
        strategies.addAll(strategyMeasuredValues.keySet());

        List<DataFieldName> dataFieldNames = new ArrayList<DataFieldName>();

        dataFieldNames.add(DataFieldName.DISTRIBUTION);
        dataFieldNames.add(DataFieldName.NORMAL_AGETNTS);
        dataFieldNames.add(DataFieldName.COOPERATOR_AGENTS);
        dataFieldNames.add(DataFieldName.AVAILABLE_RESOURCE);
       
        for (int i = 0; i < evolutions.length; i++) {
            writer.write(evolutions[i] + " ");
        }

        for (String strategy : strategies) {
            Map<DataFieldName, Double> measuredValues = strategyMeasuredValues.get(strategy);

            writer.write(strategy + " ");

            for (DataFieldName name : dataFieldNames) {
                double measuredValue = measuredValues.get(name);

                writer.write(measuredValue + " ");
            }
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
