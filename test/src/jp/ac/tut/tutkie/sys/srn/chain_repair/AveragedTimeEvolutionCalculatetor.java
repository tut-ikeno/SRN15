/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author tokumitsu
 */
public class AveragedTimeEvolutionCalculatetor {
    
    private static final String distributionLogfile = "average_of_distribution_at_each_step.log";
    private static final String timeEvolutionLogFile = "average_of_time_evolution_at_each_step.log";
    
    public static void main(String[] args) throws Exception {
        int step = Integer.parseInt(args[0]);
        int nodeCount = Integer.parseInt(args[1]);
        
        double[][] distributions = new double[step + 1][nodeCount];

        Scanner scanner = new Scanner(new File(distributionLogfile));
        
        for(int i = 0; i <= step; i++) {
            for (int k = 0; k < nodeCount; k++) {
                if(scanner.hasNextDouble()) {
                    distributions[i][k] = scanner.nextDouble();
                }
            }
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(timeEvolutionLogFile));

        for(int i = 0; i <= step; i++) {
            double sum = 0.0;
            
            for(int j = 0; j < nodeCount; j++) {
                sum += distributions[i][j];
            }
            
            double average = sum / nodeCount;
            
            writer.write(String.valueOf(average));
            writer.newLine();;
        }
        
        writer.flush();
        writer.close();
    }
}
