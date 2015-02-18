/*
 * ConstantFailureRateFunction.java
 *
 * Created on September 26, 2007, 2:23 PM
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class ConstantFailureRateFunction implements FailureRateFunction {
    
    private double amplitude = 0.0;
    
    /** Creates a new instance of ConstantFailureRateFunction */
    public ConstantFailureRateFunction(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getValue(long time) {
        return amplitude;
    }
    
}
