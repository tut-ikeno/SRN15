/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.synchrony;

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
        int nodeCount = Integer.parseInt(args[2]);
        
        int[] evolutions = new int[step + 1];

        for(int i = 1; i <= trialCount; i++) {
            Scanner scanner = new Scanner(new File(i + ".log"));

            int j = 0;

            while(scanner.hasNextDouble()) {
                evolutions[j] += nodeCount - scanner.nextInt(); /* 異常の数に変換 */
                j++;
            }
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(timeEvolutionLogFile));

        for(int i = 0; i <= step; i++) {
            double average = nodeCount - (evolutions[i] / trialCount); /* 正常の数に変換 */
            
            writer.write(String.valueOf(average));
            writer.newLine();;
        }
        
        writer.flush();
        writer.close();
    }
    
}
