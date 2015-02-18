/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.chain_repair;

/**
 *
 * @author tokumitsu
 */
public interface NumericalSolution {
    public void init();
    public void update(int step);
    public double[] getDistribution();
}
