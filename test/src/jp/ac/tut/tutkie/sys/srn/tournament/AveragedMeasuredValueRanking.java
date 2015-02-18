/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tut.tutkie.sys.srn.tournament;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author tokumitsu
 */
public class AveragedMeasuredValueRanking {

    public AveragedMeasuredValueRanking() {
        
    }

    public static void main(String[] args) throws Exception {
        new AveragedMeasuredValueRanking().createGameRanking(args);
    }

    public void createGameRanking(String[] args) throws Exception {
        File dataDirectory = new File(args[0]);

        File[] dataDirectories = dataDirectory.listFiles(new StratgyFilenameFileter());

        List<Result> resultList = new ArrayList<Result>();

        for (File directory : dataDirectories) {
            File dataFile = new File(directory.getAbsolutePath() + "/" + "average_of_time_evolution.log");

            if(!dataFile.exists()) {
                continue;
            }

            String[] strategies = directory.getName().split("_vs_");

            Scanner scanner = new Scanner(dataFile);
            
            //利用可能リソース取得のための読み飛ばし
            scanner.nextDouble();
            scanner.nextDouble();

            double resource = scanner.nextDouble();

            Result result = new Result(strategies, resource);

            resultList.add(result);
        }

        Collections.sort(resultList);

        int rank = 1;

        System.out.println("Rank Strategy Average");

        for (Result result : resultList) {
            System.out.println(String.format("%03d", rank) + " " + result);

            rank++;
        }

    }

    private class Result implements Comparable<Result> {

        private String[] strategies = null;
        private double averagedAvailableResource = 0.0;

        Result(String[] strategies, double resource) {
            this.strategies = strategies;
            this.averagedAvailableResource = resource;
        }

        public String[] getStrategies() {
            return strategies;
        }

        public double getAveragedAvailableResource() {
            return averagedAvailableResource;
        }

        public int compareTo(Result result) {
            double resource = result.getAveragedAvailableResource();

            if(this.averagedAvailableResource < resource) {
                return 1;
            } else if(this.averagedAvailableResource == resource) {
                return 0;
            } else {
                return -1;
            }
            
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for (String strategy : strategies) {
                builder.append(strategy + " ");
            }

            String resource = String.format("%.3f", averagedAvailableResource);

            builder.append(resource);

            return builder.toString();
        }

    }
    
}
