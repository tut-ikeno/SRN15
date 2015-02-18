/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.io.File;
import java.util.Scanner;

/**
 *
 * @author tokumitsu
 */
public class RoundRobinTournamentResult {

    private int distributionRowCount = 3;
    
    private int strategyCount = 2;

    private int averagedAvailableResourceIndex = 4;


    public static void main(String[] args) throws Exception {
        new RoundRobinTournamentResult().start(args);
    }

    public void start(String[] args) throws Exception {

    }

    public void createGameResult(String[] args) throws Exception {
        File dataDirectory = new File(args[0]);

        File[] dataDirectories = dataDirectory.listFiles(new StratgyFilenameFileter());

        for (File directory : dataDirectories) {
            File dataFile = new File(directory.getAbsolutePath() + "/" + "average_of_time_evolution.log");
            
            if(!dataFile.exists()) {
                continue;
            }

        //    Map<String,
            
            Scanner scanner = new Scanner(dataFile);

            //読み飛ばし
            for (int i = 0; i < distributionRowCount++; i++) {
                scanner.nextDouble();
            }

            for (int i = 0; i < strategyCount; i++) {
                String strategy = scanner.next();

                for (int j = 0; j < averagedAvailableResourceIndex - 1; j++) {
                    scanner.nextDouble();
                }

                double averagedAvailableResource = scanner.nextDouble();

                StrategyResult strategyResult = new StrategyResult(strategy, averagedAvailableResource);
            }

            
        }
    }

    private class GameResult {
        
    }

    private class StrategyResult {

        private String strategy = null;
        private double averagedAvailableResource = 0.0;

        StrategyResult(String strategy, double resource) {
            this.strategy = strategy;
            this.averagedAvailableResource = resource;
        }

        public String getStrategy() {
            return strategy;
        }

        public double getAveragedAvailableResource() {
            return averagedAvailableResource;
        }
        
    }

}
