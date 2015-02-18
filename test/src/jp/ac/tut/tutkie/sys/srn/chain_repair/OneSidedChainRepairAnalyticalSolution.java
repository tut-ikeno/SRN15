/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

/**
 * (-c/a) + (c/a + f(x-bt)) * exp (at)
 * @author tokumitsu
 */
public class OneSidedChainRepairAnalyticalSolution implements AnalyticalSolution {

    private double prn = 0.0;
    private double pra = 0.0;
    private double ps = 0.0;
    private double dx = 0.0;
    private double dt = 0.0;
    
    private double a = 0.0;
    private double b = 0.0;
    private double c = 0.0;
    
    private static final double EPS = 1.0e-5;
    
    public OneSidedChainRepairAnalyticalSolution(double prn,
            double pra,
            double ps,
            double dx,
            double dt) {
        this.prn = prn;
        this.pra = pra;
        this.ps = ps;
        this.dx = dx;
        this.dt = dt;
        
        a = (prn - pra - 1.0) / dt;
        b = (prn - pra) * (dx / dt);
        c = pra / dt;
    }
    
    public double getValue(double x, double t) {
        /* Solved by hand */
        //double value =  - (c / a) + (c / a + f(x - b * t)) * Math.exp(a * t);

        /* Solved by mathematica */
        //double value = -(c / a) + (c / a + f((b * t - x) / b)) * Math.exp((a * x) / b);
        
        /* Solved by eqworld */
        double value = - (c / a) + (-c / a + f(x - b * t)) * Math.exp((a * x) / b);
        
        return value;
    }

    public double getPrn() {
        return prn;
    }

    public double getPra() {
        return pra;
    }

    public double getPs() {
        return ps;
    }
    
    private double f(double x) {
        if(x <= EPS) {
            return ps;
        } else {
            return 0;
        }
    }

}
