/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

/**
 *
 * @author tokumitsu
 */
public class OneSidedChainRepairNumericalSolution implements NumericalSolution {

    private int n = 0;

    private double prn = 0.0;
    private double pra = 0.0;
    private double ps = 0.0;
    
    private double[] states = null;
    private double[] nextStates = null;
    
    public OneSidedChainRepairNumericalSolution(
            int n,
            double prn,
            double pra,
            double ps) {
        this.n = n;
        this.prn = prn;
        this.pra = pra;
        this.ps = ps;
        
        states = new double[n + 1];
        nextStates = new double[n + 1];
    }
    
    public void init() {
        nextStates[0] = ps; /* あとは0.0だから処理必要なし */
    }
    
    public void update(int step) {
        step = step % n;
        
        for(int i = 0; i <= n; i++) {
            states[i] = nextStates[i];
        }

        states[0] = states[n]; /* 右端を左端と接続 */
        
        for (int i = 0; i < n; i++) {
            nextStates[i + 1] = h(step, i) * ((prn - pra) * states[i] + pra)
                    + (1.0 - h(step, i)) * states[i + 1];
        }
    }

    public double[] getDistribution() {
        return nextStates;
    }
    
    private static final double eps = 1.0e-5;
    
    private double h(double t, double x) {
        if(Math.abs(t - x) < eps) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

}