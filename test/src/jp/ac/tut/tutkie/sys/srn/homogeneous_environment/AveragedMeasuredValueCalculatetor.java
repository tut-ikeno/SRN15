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
public class AveragedMeasuredValueCalculatetor {
    
    private static final String timeEvolutionLogFile = "average_of_time_evolution_at_each_step.log";
    private static final String meanValueLogFile = "average_of_time_evolution.log";
    
    public static void main(String[] args) throws Exception {
        int step = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        
        double[] evolutions = new double[rows];

        Scanner scanner = new Scanner(new File(timeEvolutionLogFile));

        for (int j = 0; j <= step; j++) {
            for (int k = 0; k < rows; k++) {
                if (scanner.hasNextDouble()) {
                    evolutions[k] += scanner.nextDouble();
                } else {
                    assert false;
                }
            }
        }
 
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(meanValueLogFile));

        for (int j = 0; j < rows; j++) {
            double average = evolutions[j] / ((double) (step + 1));

            writer.write(Double.toString(average) + " ");
        }

 //       writer.newLine();
        
        writer.flush();
        writer.close();
    }
    
}
