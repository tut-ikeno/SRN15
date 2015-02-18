/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author tokumitsu
 */
public class AveragedDistributionCalculatetor {
    
    private static final String logfile = "average_of_distribution_at_each_step.log";
    
    public static void main(String[] args) throws Exception {
        int trialCount = Integer.parseInt(args[0]);
        int step = Integer.parseInt(args[1]);
        int nodeCount = Integer.parseInt(args[2]);
        
        int[][] distributions = new int[step + 1][nodeCount];
        
        for(int i = 1; i <= trialCount; i++) {
            String filename = i + ".log";
            
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            
            String line = null;
            int t = 0;

            while ((line = reader.readLine()) != null) {
                for (int k = 0; k < nodeCount; k++) {
                    //String state = String.valueOf(line.charAt(k));
                    distributions[t][k] += line.charAt(k) - '0';
                }
                t++;
            }
            
            reader.close();
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(logfile));

        for(int i = 0; i <= step; i++) {
            for(int j = 0; j < nodeCount; j++) {
                double value = (double) distributions[i][j] / (double) trialCount;
                
                writer.write(value + " ");
            }
            writer.newLine();;
        }
        
        writer.flush();
        writer.close();
    }
}
