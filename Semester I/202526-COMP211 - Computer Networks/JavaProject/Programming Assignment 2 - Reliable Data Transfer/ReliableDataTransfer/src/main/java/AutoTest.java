import java.util.*;

public class AutoTest {
    public static double BEST_SCORE = Double.MAX_VALUE;
    public static String BEST_SCORE_INFO;
    public static int SEED = 0;
    public static  int SIMULATE_NUMBER = 1000;
    public static void main(String[] args) {
        System.out.print(">> Simulation Number = ");
        SIMULATE_NUMBER = new Scanner(System.in).nextInt();
        Random rand = new Random(SEED);
        Set<Integer> generatedSeed = new HashSet<>(SIMULATE_NUMBER);
        while (generatedSeed.size()<SIMULATE_NUMBER){
            generatedSeed.add(rand.nextInt());
        }
        System.out.println();
        int currentSimulation = -1;
        long time = System.currentTimeMillis();
        for (Integer seed : new ArrayList<>(generatedSeed)) {
            currentSimulation++;
            NetworkSimulator simulator  =
                    new NetworkSimulator(
                            100,
                            0.1,
                            0.1,
                            5.0,
                            0,
                            seed);
            simulator.runSimulator();
            int s = simulator.getNumberDelivered();
            if(simulator.getNumberDelivered()!=100)
            {
                System.out.println("TEST FAILED");
                System.out.println("SEED = ".concat(String.valueOf(SEED)));
                return;
            }
            if(System.currentTimeMillis() - time > 50){
                time = System.currentTimeMillis();
                System.out.printf("[%d/%d][%.2f%%]\r",currentSimulation,SIMULATE_NUMBER,(double)currentSimulation / (double)SIMULATE_NUMBER * 100.00);
            }
        }
    }
}
