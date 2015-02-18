/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author tokumitsu
 */
public class StratgyFilenameFileter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        String[] tokens = name.split("_vs_");

        int strategyCount = 2;

        if ((tokens.length < strategyCount) || tokens[0].equals(tokens[1])) {
            return false;
        }

        return true;
    }
    
}
