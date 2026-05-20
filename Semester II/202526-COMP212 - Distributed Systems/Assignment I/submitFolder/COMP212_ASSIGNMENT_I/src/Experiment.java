import NetworkAndSimulator.Network;
import NetworkAndSimulator.Simulator;
import Tools.GraphMaker;
import Tools.Shuffle;
import Tools.TimeFormatter;

import java.util.*;

public class Experiment {
    public static int MULTIPLIER = 1;

    public static void main(String[] args) {
        taskA();
        taskB();
        taskC();
        taskD();
    }

    public static void taskA() {
        long startTime = System.currentTimeMillis();
        GraphMaker graphMaker = new GraphMaker("taskA.txt");
        int total = 1000 * MULTIPLIER;
        for (int size = 1; size < total; size++) {
            Simulator simulator = new Simulator();
            simulator.generateMainRing(size, 200, 0, Collections.emptyList());
            while (simulator.hasNextRound()) simulator.nextRound();
            graphMaker.record(size, Network.getRound(), Network.getTransmission());
            System.out.printf("Task A | Running %s | Simulating the size of [%d]\t / \t[%d]\n", TimeFormatter.format(System.currentTimeMillis() - startTime), size, total);
            simulator.validation();
            Network.reset();
        }
    }

    public static void taskB() {
        long startTime = System.currentTimeMillis();
        GraphMaker graphMaker = new GraphMaker("taskB.txt");
        int total = 1000 * MULTIPLIER;
        double percentage = 0.1;
        List<Integer> list = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        for (int size = 1; size < total; size++) {
            Simulator simulator = new Simulator();
            int subnetSize = (int) (size * percentage);
            while (list.size() < subnetSize) list.add(3 + rand.nextInt(18));
            simulator.generateMainRing(size, 200, subnetSize, list);
            while (simulator.hasNextRound()) simulator.nextRound();
            graphMaker.record(size, Network.getRound(), Network.getTransmission());
            System.out.printf("Task B | Running %s | Simulating the size of [%d]\t / \t[%d]\n", TimeFormatter.format(System.currentTimeMillis() - startTime), size, total);
            simulator.validation();
            Network.reset();
        }
    }

    public static void taskC() {
        long startTime = System.currentTimeMillis();
        GraphMaker graphMaker = new GraphMaker("taskC.txt");
        for (int x = 300 * MULTIPLIER; x <= 900 * MULTIPLIER; x++) {
            Simulator simulator = new Simulator();
            List<Integer> subnetSize = Shuffle.generateRandomAssigned(x, 100 * MULTIPLIER, 3);
            simulator.generateMainRing(1000 * MULTIPLIER - x, 200, 100 * MULTIPLIER, subnetSize);
            while (simulator.hasNextRound()) simulator.nextRound();
            graphMaker.record(x, Network.getRound(), Network.getTransmission());
            System.out.printf("Task C | Running %s | Simulating the size of [%d]\t / \t[%d]\n", TimeFormatter.format(System.currentTimeMillis() - startTime), x, 900 * MULTIPLIER);
            simulator.validation();
            Network.reset();
        }
    }

    public static void taskD() {
        long startTime = System.currentTimeMillis();
        GraphMaker graphMaker = new GraphMaker("taskD.txt");
        for (int x = 1; x <= 200 * MULTIPLIER; x++) {
            Simulator simulator = new Simulator();
            List<Integer> subnetSize = Shuffle.generateRandomAssigned(600 * MULTIPLIER, x, 3);
            simulator.generateMainRing(400 * MULTIPLIER - x, 200, x, subnetSize);
            while (simulator.hasNextRound()) simulator.nextRound();
            graphMaker.record(x, Network.getRound(), Network.getTransmission());
            System.out.printf("Task D | Running %s | Simulating the size of [%d]\t / \t[%d]\n", TimeFormatter.format(System.currentTimeMillis() - startTime), x, 200 * MULTIPLIER);
            simulator.validation();
            Network.reset();
        }
    }

}
