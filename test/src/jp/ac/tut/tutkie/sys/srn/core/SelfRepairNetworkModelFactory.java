/*
 * SelfRepairNetworkModelFactory.java
 *
 * Created on 2007/05/02, 19:31
 *
 */

package jp.ac.tut.tutkie.sys.srn.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Masahiro Tokumitsu
 */
public class SelfRepairNetworkModelFactory {
    
    String[] arguments = null;
    
    /** Creates a new instance of SelfRepairNetworkModelFactory */
    public SelfRepairNetworkModelFactory(String[] args) {
        this.arguments = args;
    }
    
    public SelfRepairNetworkModel create(String className)
        throws ClassNotFoundException {
        if(className == null) {
            throw new NullPointerException();
        }
        
        Class modelClass = Class.forName(className);
        
        Constructor constructor = null;
        
        try {
            constructor = modelClass.getConstructor(arguments.getClass());
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        SelfRepairNetworkModel model = null;
        
        Object[] parameters = new Object[1];
        parameters[0] = arguments;
        
        try {
            model = (SelfRepairNetworkModel) constructor.newInstance(parameters);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return model;
    }
}
