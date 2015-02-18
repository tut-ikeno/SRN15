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
public class AnalyticalSolutionCalculator {
    public static void main(String[] args) throws Exception {
        double startX = Double.parseDouble(args[0]);
        double endX = Double.parseDouble(args[1]);
        int nX = Integer.parseInt(args[2]); /* x分割数 */
        
        double stepX = (endX - startX) / nX;
        
        double startT = Double.parseDouble(args[3]);
        double endT = Double.parseDouble(args[4]);
        int nT = Integer.parseInt(args[5]); /* tの分割数 */
        
        double stepT = (endT - startT) / nT;
        
        double prn = Double.parseDouble(args[6]);
        double pra = Double.parseDouble(args[7]);
        double ps = Double.parseDouble(args[8]);
        double dx = Double.parseDouble(args[9]);
        double dt = Double.parseDouble(args[10]);
        
        AnalyticalSolution solution = new OneSidedChainRepairAnalyticalSolution(prn, pra, ps, dx, dt);
        
        for(int i = 0; i <= nT; i++) {
            double t = i * stepT;
            
            String filename = "analytical_solution_of_distribution_at_each_step." + i + ".log";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            for(int j = 0; j <= nX; j++) {
                double x = j * stepX;
                            
                double value = solution.getValue(x, t);
                
                writer.write(x + " " + value);
                writer.newLine();
            }
            
            writer.flush();
            writer.close();
        }
    }
    
}
