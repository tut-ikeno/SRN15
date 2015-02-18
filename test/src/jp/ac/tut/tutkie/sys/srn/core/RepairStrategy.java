/*
 * RepairStrategy.java
 *
 * Created on 2007/07/02, 17:06
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class RepairStrategy {
    
    private AgentGameActionEnum[] code = null;
    
    /** Creates a new instance of RepairStrategy */
    public RepairStrategy(String code) 
        throws RepairStrategyCodeFormatException {
        this(code.toCharArray());
    }
    
    public RepairStrategy(char[] charCode)
        throws RepairStrategyCodeFormatException {
        this.code = toActionEnumArray(charCode);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    public RepairStrategy(AgentGameActionEnum[] code) {
        this.code = new AgentGameActionEnum[code.length];
        
        for(int i = 0; i < code.length; i++) {
            this.code[i] = code[i];
        }
    }
    
    public AgentGameActionEnum[] getStrategyCodeArray() {
        return code;
    }
    
    private AgentGameActionEnum[] toActionEnumArray(char[] charCode)
        throws RepairStrategyCodeFormatException {
        AgentGameActionEnum[] code = new AgentGameActionEnum[charCode.length];
        
        for(int i = 0; i < code.length; i++) {
            char playerCode = charCode[i];
            
            if(playerCode == '0') {
                code[i] = AgentGameActionEnum.DEFECTION;
            } else if(playerCode == '1') {
                code[i] = AgentGameActionEnum.COOPERATION;
            } else if(playerCode == 'r') {
                code[i] = AgentGameActionEnum.REVERSE;
            } else if(playerCode == 's') {
                code[i] = AgentGameActionEnum.STAY;
            } else {
                throw new RepairStrategyCodeFormatException("No such strategy code:" + playerCode);
            }
        }
        
        return code;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < code.length; i++) {
            builder.append(code[i]);
        }

        return builder.toString();
    }

    public int toDecimal() {
        int dec = 0;
        int k = 1;
        
        if(code.length == 0) {
            return k;
        }
        
        for(AgentGameActionEnum element: code) {
            if((element.equals(AgentGameActionEnum.COOPERATION))
                || (element.equals(AgentGameActionEnum.REVERSE))
                || (element.equals(AgentGameActionEnum.STAY))) {
                dec += k;
            }
            
            /*  条件分岐しないのがあやしいので、使う時は確認  */
           
            k = k << 1; /*  k *= 2; */
        }
        
        return dec;
    }

    public boolean isEquivalent(RepairStrategy strategy) {
        AgentGameActionEnum[] code = strategy.getStrategyCodeArray();

        for(int i = 0; i < code.length; i++) {
            if(code[i] != this.code[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RepairStrategy) {
            return isEquivalent((RepairStrategy) obj);
        }

        return false;
    }

}
