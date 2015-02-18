/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.dynamic_environment;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import jp.ac.tut.tutkie.sys.srn.core.FailureRateFunction;
import jp.ac.tut.tutkie.sys.srn.core.TriangleWaveFailureRateFunction;
import jp.ac.tut.tutkie.sys.srn.core.Agent;
import jp.ac.tut.tutkie.sys.srn.homogeneous_environment.SimulationParameters;
import jp.ac.tut.tutkie.sys.srn.homogeneous_environment.StrategicRepairSelfRepairNetworkModel;
import jp.ac.tut.tutkie.sys.srn.core.Environment;

/**
 *
 * @author tokumitsu
 */
public class StrategicRepairDynamicEnvironment extends StrategicRepairSelfRepairNetworkModel {

    public StrategicRepairDynamicEnvironment(String[] args) {
        super(args);

//        try {
//            System.setOut(new PrintStream("strategic_repair_in_dynamic_environment.log"));
//        } catch(Exception e) {
//            e.printStackTrace();;
//        }
    }

    @Override
    public void init(Environment environment) {
        super.init(environment);

        //以下は設定の移行処理
        Map<String, Number> map = getSimulationParametersMap();

        Number failureRateNumber = map.get(SimulationParameters.FAILURE_RATE);
        double failureRate = failureRateNumber.doubleValue();

        Number failureRateCycleNumber = map.get(SimulationParameters.FAILURE_RATE_CYCLE);
        int cycle = failureRateCycleNumber.intValue();

        FailureRateFunction function = new TriangleWaveFailureRateFunction(cycle, failureRate);

        List<Agent> list = getEnvironment().getAgentList();

        for(Agent agent: list) {
            Agent unit = (Agent) agent;

            unit.setFailureRateFunction(function);
        }
    }

}
