package NetworkAndSimulator;

import Constant.BaseException;
import Constant.BaseExceptionType;
import Constant.ProcessorStatus;
import Constant.ProcessorType;
import Processor.Processor;
import Tools.Shuffle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {
    private final List<Processor> rings;
    private boolean hasNextRound;

    public Simulator() {
        rings = new ArrayList<>();
        hasNextRound = false;
    }

    public void generateMainRing(int mainRingSize, int startBound, int interfaceProcessorNumber, List<Integer> subnetSize) {
        // The total size of the main ring minus the interface size
        // plus all processors in the sub-rings, equals the number of IDs that need to be allocated.
        int requiredIdSize = mainRingSize;
        for (int x : subnetSize) requiredIdSize += x;
        generateMainRing(mainRingSize, startBound, interfaceProcessorNumber, subnetSize, Shuffle.newArray(requiredIdSize), 0);
    }

    /**
     * Ring generation function.
     *
     * @param mainRingSize             The size of the entire main ring.
     * @param startBound               The start time bound for each processor, ranging from [0, startBound].
     * @param interfaceProcessorNumber The number of interface processors.
     * @param subnetSize               The size of each sub-ring.
     * @param shuffledId               An array of shuffled IDs.
     * @param shuffledIdStart          The starting index to draw from the shuffledId array.
     */
    public void generateMainRing(int mainRingSize, int startBound, int interfaceProcessorNumber, List<Integer> subnetSize, int[] shuffledId, int shuffledIdStart) {
        if (mainRingSize < subnetSize.size()) throw new BaseException(BaseExceptionType.TOO_MANY_SUBNET);
        if (subnetSize.size() != interfaceProcessorNumber) throw new BaseException(BaseExceptionType.NUMBER_NOT_MATCHING);
        if(shuffledIdStart >= shuffledId.length) throw new BaseException(BaseExceptionType.SHUFFLE_ID_OUT_OF_BOUND);
        Random random = new Random(System.currentTimeMillis());

        // We need to create total = idContains + interfaceProcessorNumber processors.
        // idContains includes the non-interface processors on the main ring and the sub-rings.
        // interfaceProcessorNumber represents the interface processors on the main ring.
        // Among the total processors, we need idContains processors to have an ID, and interfaceProcessorNumber processors to not have an ID.
        // First, generate the subnets. Subnets are controlled by the Simulator.
        Simulator[] subnet = new Simulator[subnetSize.size()];
        for (int i = 0; i < subnet.length; i++) {
            Simulator simulator = new Simulator();
            simulator.generateMainRing(subnetSize.get(i), startBound, 0, // 子网没有【子网】
                    new ArrayList<>(), shuffledId, shuffledIdStart);
            subnet[i] = simulator;
            shuffledIdStart += subnetSize.get(i);
        }
        // Then, generate the main ring.
        // First, generate all processors as non-interface processors.
        for (int i = 0; i < mainRingSize; i++) {
            int c = shuffledId[shuffledIdStart++];
            rings.add(new Processor(c, random.nextInt(startBound + 1), ProcessorType.NON_INTERFACE_PROCESSOR));
        }
        // Randomly take the first interfaceProcessorNumber elements from interfaceIndex to act as interfaces.
        int[] interfaceIndex = new int[mainRingSize];
        for (int i = 0; i < mainRingSize; i++) interfaceIndex[i] = i;
        Shuffle.apply(interfaceIndex);

        for (int i = 0; i < interfaceProcessorNumber; i++) {
            rings.get(interfaceIndex[i]).setAsInterfaceProcessor();
            rings.get(interfaceIndex[i]).setSubnetSimulator(subnet[i]);
        }

        // Connect the main ring.
        for (int i = 0; i < rings.size(); i++) {
            int prevId = i == 0 ? rings.size() - 1 : i - 1;
            int nextId = i == rings.size() - 1 ? 0 : i + 1;
            rings.get(i).setNextProcessor(rings.get(nextId));
            rings.get(i).setPrevProcessor(rings.get(prevId));
        }
        hasNextRound = true;
    }

    public void nextRound() {
        nextRound(true);
    }

    public void nextRound(boolean increaseNetworkRound) {

        if (!hasNextRound) return;
        boolean allTerminated = true;
        int unknown = 0;
        int leader = 0;
        int lost = 0;
        for (Processor processor : rings) {
            processor.run();
            if (!processor.isTerminated()) allTerminated = false;
            if (processor.status() == ProcessorStatus.LEADER) leader++;
            if (processor.status() == ProcessorStatus.LOST) lost++;
            if (processor.status() == ProcessorStatus.UNKNOWN) unknown++;
        }
        if (Constant.System.DEBUG_MODE)
            System.out.printf("round = %d\t Unknown = %d\t Leader = %d\t Lost = %d%n", Network.getRound(), unknown, leader, lost);
        hasNextRound = !allTerminated;
        if (increaseNetworkRound) Network.nextRound();

    }

    public boolean hasNextRound() {
        return hasNextRound;
    }

    public void terminate() {
        Network.reset();
        rings.clear();
        hasNextRound = false;
    }

    public boolean validation() {
        int unknown = 0;
        int leader = 0;
        int lost = 0;
        int leaderId = Integer.MIN_VALUE;
        for (Processor processor : rings) {
            if (processor.status() == ProcessorStatus.LEADER) leader++;
            if (processor.status() == ProcessorStatus.LOST) lost++;
            if (processor.status() == ProcessorStatus.UNKNOWN) unknown++;
            if (leaderId == Integer.MIN_VALUE) leaderId = processor.getLeaderId();
            else if (leaderId != processor.getLeaderId())
                throw new BaseException(BaseExceptionType.INCONSISTENT_LEADER_ID);
        }
        if (unknown != 0) throw new BaseException(BaseExceptionType.PROCESSOR_STATUS_NOT_DETERMINED);
        if (leader > 1) throw new BaseException(BaseExceptionType.TOO_MANY_LEADER);
        if (leader < 1) throw new BaseException(BaseExceptionType.LEADER_NOT_FOUND);
        if (lost != rings.size() - 1) throw new BaseException(BaseExceptionType.WRONG_NUMBER_OF_LOST_PROCESSOR);
        return true;
    }

    public int getLeaderId() {
        if (rings.isEmpty()) throw new BaseException(BaseExceptionType.EMPTY_RING);
        return this.rings.get(0).getLeaderId();
    }
}
