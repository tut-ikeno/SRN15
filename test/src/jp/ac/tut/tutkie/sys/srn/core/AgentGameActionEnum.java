/*
 * AgentGameActionEnum.java
 *
 * Created on 2007/06/29, 14:41
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author Masahiro Tokumitsu
 */
public enum AgentGameActionEnum {
    COOPERATION("C"),
    DEFECTION("D"),
    REVERSE("R"),
    STAY("S");
    
    private String action = null;

    AgentGameActionEnum(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

}
