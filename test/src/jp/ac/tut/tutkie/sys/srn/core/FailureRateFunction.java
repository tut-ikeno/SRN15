/*
 * FailureRateFunction.java
 *
 * Created on July 23, 2007, 1:27 PM
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author Masahiro Tokumitsu
 */
public interface FailureRateFunction {
    public double getValue(long time);
}
