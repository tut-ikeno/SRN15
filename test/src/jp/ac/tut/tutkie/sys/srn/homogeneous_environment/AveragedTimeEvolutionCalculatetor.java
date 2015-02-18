/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.homogeneous_environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        int rows = Integer.parseInt(args[2]);
        
        double[][] evolutions = new double[step + 1][rows];

        for (int i = 1; i <= trialCount; i++) {
            Scanner scanner = new Scanner(new File(i + ".log"));

            for (int j = 0; j <= step; j++) {
                for (int k = 0; k < rows; k++) {
                    if (scanner.hasNextDouble()) {
                        evolutions[j][k] += scanner.nextDouble();
                    } else {
                        assert false;
                    }
                }
            }
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(timeEvolutionLogFile));

        for(int i = 0; i <= step; i++) {
            for(int j = 0; j < rows; j++) {
                double average = evolutions[i][j] / ((double) trialCount);
                
                writer.write(Double.toString(average) + " ");
            }

            writer.newLine();
        }
        
        writer.flush();
        writer.close();
    }
    
}
