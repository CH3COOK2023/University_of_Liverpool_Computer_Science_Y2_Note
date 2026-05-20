import Constant.Format;
import NetworkAndSimulator.Network;
import NetworkAndSimulator.Simulator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // Here are some simple demonstration!
        // Structure with subnet and startup delay
        subnetAndDelay();
        // Structure with delay only, but without subnet
        // Therefore, this is the asynchronous startup LCR algorithm.
        delay();
        // Structure with subnet only, but without startup delay
        subnet();
        // Structure without subnet and without startup delay
        withoutSubnetAndWithoutDelay();

    }

    public static void subnetAndDelay() {
        System.out.println(Format.DOUBLE_LINE);
        System.out.println("Simulating with subnet and with startup delay.");
        System.out.println(Format.SINGLE_LINE);
        int mainRingSize = 3500;
        int startBound = 300; // randomly from 0 to startBound
        int interfaceProcessorNumber = 5;
        List<Integer> subnetSize = Arrays.asList(2000, 100, 1750, 20, 9);
        execute(mainRingSize, startBound, interfaceProcessorNumber, subnetSize);
    }

    public static void delay(){
        System.out.println(Format.DOUBLE_LINE);
        System.out.println("Simulating without subnet and with startup delay.");
        System.out.println(Format.SINGLE_LINE);
        int mainRingSize = 3500;
        int startBound = 300; // randomly from 0 to startBound
        int interfaceProcessorNumber = 0;
        List<Integer> subnetSize = Collections.emptyList();
        execute(mainRingSize, startBound, interfaceProcessorNumber, subnetSize);
    }

    public static void subnet(){
        System.out.println(Format.DOUBLE_LINE);
        System.out.println("Simulating with subnet and without startup delay.");
        System.out.println(Format.SINGLE_LINE);
        int mainRingSize = 3500;
        int startBound = 0; // randomly from 0 to startBound
        int interfaceProcessorNumber = 5;
        List<Integer> subnetSize = Arrays.asList(2000, 100, 1750, 20, 9);
        execute(mainRingSize, startBound, interfaceProcessorNumber, subnetSize);
    }
    public static void withoutSubnetAndWithoutDelay(){
        System.out.println(Format.DOUBLE_LINE);
        System.out.println("Simulating without subnet and without startup delay.");
        System.out.println(Format.SINGLE_LINE);
        int mainRingSize = 3500;
        int startBound = 0; // randomly from 0 to startBound
        int interfaceProcessorNumber = 0;
        List<Integer> subnetSize = Collections.emptyList();
        execute(mainRingSize, startBound, interfaceProcessorNumber, subnetSize);
    }

    private static void execute(int mainRingSize, int startBound, int interfaceProcessorNumber, List<Integer> subnetSize) {
        Network.reset();
        System.out.println("Main ring size = " + mainRingSize);
        System.out.println("Start bound = " + startBound);
        System.out.println("Interface processor number = " + interfaceProcessorNumber);
        System.out.println("Subnet size = " + subnetSize);
        System.out.println(Format.SINGLE_LINE);


        Simulator simulator = new Simulator();
        simulator.generateMainRing(mainRingSize, startBound, interfaceProcessorNumber, subnetSize);
        while (simulator.hasNextRound())
            simulator.nextRound();
        Network.printInformation();
        System.out.println("This simulation is " + (simulator.validation()?"validate.":"not validate."));
        System.out.println(Format.DOUBLE_LINE);
        System.out.println(Format.SPACE);
    }

}
