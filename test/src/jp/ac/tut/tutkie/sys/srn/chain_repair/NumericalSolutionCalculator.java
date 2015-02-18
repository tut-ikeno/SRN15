/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author tokumitsu
 */
public class NumericalSolutionCalculator {
    
    public static void main(String[] args) throws Exception {
//        int n = Integer.parseInt(args[0]);
//        
//        double prn = Double.parseDouble(args[1]);
//        double pra = Double.parseDouble(args[2]);
//        double ps = Double.parseDouble(args[3]);
//        
//        int maxStep = Integer.parseInt(args[4]);
        
        int n = 1000;
        double prn = 0.70;
        double pra = 0.60;
        double ps = 0.1;
        int maxStep = 2000;
        
        NumericalSolution solution = new OneSidedChainRepairNumericalSolution(n, prn, pra, ps);

        String filename = "numerical_solution_of_distribution_at_each_step.log";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        
        solution.init();
        
        for(int i = 0; i < maxStep; i++) {
            double[] distribution = solution.getDistribution();
            
            for(int j = 0; j < n; j++) {
                writer.write(" " + distribution[j]);
            }
            
            writer.newLine();
            
            solution.update(i);
        }
        
        writer.flush();
        writer.close();
    }
    
}
