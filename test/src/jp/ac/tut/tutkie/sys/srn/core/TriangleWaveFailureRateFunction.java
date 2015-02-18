/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author tokumitsu
 */
public class TriangleWaveFailureRateFunction implements FailureRateFunction {

    private int cycle = 0;
    private double amplitude = 0.0;
    private int halfCycle = 0;

    /** Creates a new instance of SinFailureRateFunction */
    public TriangleWaveFailureRateFunction(int cycle, double amplitude) {
        this.cycle = cycle;
        this.amplitude = amplitude;
        this.halfCycle = cycle / 2;
    }

    public double getValue(long time) {
        time = time % cycle;

        double value = 0.0;

        if(time <= halfCycle) {
            value = ((2.0 * amplitude) / cycle) * time;
        } else {
            value = ((-2.0 * amplitude) / cycle) * time + 2.0 * amplitude;
        }

        return value;
    }
    
}
