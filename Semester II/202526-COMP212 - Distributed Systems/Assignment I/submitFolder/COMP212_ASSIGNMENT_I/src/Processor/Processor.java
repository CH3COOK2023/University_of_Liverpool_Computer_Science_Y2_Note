package Processor;

import Constant.ProcessorStatus;
import Constant.ProcessorType;
import Constant.Slot;
import Message.Message;
import Message.MessageSlot;
import NetworkAndSimulator.Network;
import NetworkAndSimulator.Simulator;

public class Processor {
    private int myId; // Current self id
    private int startRound; // Starting round
    private int leaderId; // Current perceived maximum leader id
    private ProcessorStatus status; // Current status, default is UNKNOWN
    private Processor nextProcessor; // Next processor
    private Processor prevProcessor; // Previous processor
    private ProcessorType type; // Processor type
    private boolean nextWakenUp;  // Whether the next node is woken up
    private boolean prevWakeUp;  // Whether the previous node is woken up
    private final MessageSlot forwardNextSlot; // Slot sent to the next node
    private final MessageSlot forwardPrevSlot; // Slot sent to the previous node
    private boolean shouldForwardNext;
    private boolean shouldForwardPrev;
    private boolean terminated; // Whether the current node has [completed] execution
    private Message[] receivedSlots; // Slot pulled from the network
    private Simulator subnetSimulator; // Subnet simulator

    public Processor(int myId, int startRound, ProcessorType type) {
        this.myId = myId;
        this.startRound = startRound;
        this.type = type;
        this.leaderId = myId;
        this.status = ProcessorStatus.UNKNOWN;
        this.forwardNextSlot = new MessageSlot();
        this.forwardPrevSlot = new MessageSlot();
        this.shouldForwardNext = false;
        this.shouldForwardPrev = false;
        this.terminated = false;
        this.receivedSlots = null;
    }

    public void setSubnetSimulator(Simulator subnetSimulator){
        this.subnetSimulator = subnetSimulator;
    }
    public int getLeaderId(){
        return this.leaderId;
    }

    // Set the next processor
    public void setNextProcessor(Processor next) {
        this.nextProcessor = next;
    }

    // Set the previous processor
    public void setPrevProcessor(Processor previous) {
        this.prevProcessor = previous;
    }

    // Whether the current node has completed execution
    public boolean isTerminated(){
        return this.terminated;
    }

    // Current node status
    public ProcessorStatus status(){
        return this.status;

    }

    // Run one round
    public void run() {
        if(this.subnetSimulator!=null) {
            System.out.print("");
        }
        // First, pull your own information from the network for this round
        this.receivedSlots = Network.getMyMessage(this.myId).getSlot();

        // 1. If currently a NON_INTERFACE_PROCESSOR, and not woken up in the current round, we simulate message arrival but do not process it, and return directly
        if (this.type == ProcessorType.NON_INTERFACE_PROCESSOR && Network.getRound() < startRound) return;

        // 2. If currently an INTERFACE_PROCESSOR, and the subnet has not completed the election
        if (this.type == ProcessorType.INTERFACE_PROCESSOR && this.subnetSimulator.hasNextRound()){
            // Then currently not woken up, still need to run a round of the subnet
            // If currently woken up, also need to run a round of the subnet
            this.subnetSimulator.nextRound(false);
            // Check if the subnet has a next round
            // If after running, it is found that the subnet has completed the election
            if(!this.subnetSimulator.hasNextRound()){
                // First verify if the election is truly completed
                this.subnetSimulator.validation();
                // Now allow to start
                this.startRound = Network.getRound();
                // Set own id and leader id
                this.myId = this.subnetSimulator.getLeaderId();
                this.leaderId = this.myId;
            }else{
                this.startRound = Network.getRound()+1;
            }
            // Of course, if the current processor has not woken up yet, regardless of the subnet status, it should return directly after execution
            if(Network.getRound() < this.startRound)
                return;
        }

        // Waking up requires bidirectional broadcasting
        if(Network.getRound() == startRound){

            // If the current round happens to be its own waking up round, then it must tell the previous node that it has woken up and can accept messages from the previous node
            // And, set that it should be forwarded to the previous node
            this.forwardPrevSlot.setSlot(Slot.TELL_PREV_ACTIVATION_SIGN_SLOT, new Message(Slot.TELL_PREV_ACTIVATION_SIGN_SLOT, this.myId, this.myId, this.prevProcessor.getMyId()));
            this.shouldForwardPrev = true;

            // If the current round happens to be its own waking up round, then it must tell the next node that it has woken up and can accept messages from the next node
            // For the next node, it might happen to receive some information from the previous node during the waking up round, so process it simultaneously, thus temporarily not sending
            this.forwardNextSlot.setSlot(Slot.TELL_NEXT_ACTIVATION_SIGN_SLOT, new Message(Slot.TELL_NEXT_ACTIVATION_SIGN_SLOT, this.myId, this.myId, this.nextProcessor.getMyId()));
            this.shouldForwardNext = true;


            // The processor should push its own id to the next node
            this.forwardNextSlot.setSlot(Slot.RECEIVE_SLOT, new Message(Slot.RECEIVE_SLOT, this.myId, this.myId, this.nextProcessor.getMyId()));
        }

        // See if anyone has pushed a new leaderId to it
        if (receivedSlots[Slot.RECEIVE_SLOT] != null) {
            int receivedId = receivedSlots[Slot.RECEIVE_SLOT].data();

            // Fix: prioritize determining if it is itself (or if it belongs to its own subnet)
            // Here it is divided into interface processors and non-interface processors
            // If it is a non-interface processor, then determine if the received id and its own id are consistent to judge if it is the LEADER
            // If it is an interface processor, then determine if the received id and the id elected by its [subnet] are consistent to judge if it is the leader
            if (this.type == ProcessorType.NON_INTERFACE_PROCESSOR && receivedId == this.myId
                    || this.type == ProcessorType.INTERFACE_PROCESSOR && !this.subnetSimulator.hasNextRound() && receivedId == this.subnetSimulator.getLeaderId()) {
                this.status = ProcessorStatus.LEADER;
                this.forwardNextSlot.setSlot(Slot.TERMINATION_SLOT, new Message(Slot.TERMINATION_SLOT, this.leaderId, this.myId, this.nextProcessor.getMyId()));
                shouldForwardNext = true;
            }
            // Then determine if the leaderId needs to be updated
            else if (receivedId > this.leaderId) {
                this.leaderId = receivedId;
                this.forwardNextSlot.setSlot(Slot.RECEIVE_SLOT, new Message(Slot.RECEIVE_SLOT, this.leaderId, this.myId, this.nextProcessor.getMyId()));
                this.shouldForwardNext = true;
                this.status = ProcessorStatus.LOST; // Lost the election, but not terminated yet!
            }
            // Ignore if smaller
        }

        // See if there is a Termination
        if(receivedSlots[Slot.TERMINATION_SLOT]!=null){
            this.forwardNextSlot.setSlot(Slot.TERMINATION_SLOT,new Message(Slot.TERMINATION_SLOT,this.leaderId,this.myId,this.nextProcessor.getMyId()));
            this.terminated = true;
            shouldForwardNext = true;
        }



        // If a [waking up] signal is received from the previous node, return [I acknowledge receipt that you have woken up]
        // And set local variable: previous node has woken up
        if(receivedSlots[Slot.TELL_NEXT_ACTIVATION_SIGN_SLOT]!=null){
            // prev node sends TELL_NEXT_ACTIVATION_SIGN_SLOT so here is the previous node sending [I have woken up] to this node
            forwardPrevSlot.setSlot(Slot.ACK_PREV_ACTIVATION_SIGN_SLOT,new Message(Slot.ACK_PREV_ACTIVATION_SIGN_SLOT,this.myId,this.myId,this.prevProcessor.getMyId()));
            shouldForwardPrev = true;
            this.prevWakeUp = true;
        }
        // If a [waking up] signal is received from the next node, return [I acknowledge receipt that you have woken up]
        // And set local variable: next node has woken up
        if(receivedSlots[Slot.TELL_PREV_ACTIVATION_SIGN_SLOT]!=null){
            forwardNextSlot.setSlot(Slot.ACK_NEXT_ACTIVATION_SIGN_SLOT,new Message(Slot.ACK_NEXT_ACTIVATION_SIGN_SLOT,this.myId,this.myId,this.nextProcessor.getMyId()));
            // Since the next node told us it woke up, we need to tell it what our current maximum leaderId is
            this.forwardNextSlot.setSlot(Slot.RECEIVE_SLOT,new Message(Slot.RECEIVE_SLOT,this.leaderId,this.myId,this.nextProcessor.getMyId()));
            shouldForwardNext = true;
            this.nextWakenUp = true;
        }

        // If an ACK signal [I confirm I know you have woken up] is received from the next node, then it can be confirmed that the next node is awake
        // This helps in this situation: this node woke up late, and the previous and next nodes have already woken up
        if(receivedSlots[Slot.ACK_PREV_ACTIVATION_SIGN_SLOT]!=null){
            this.nextWakenUp = true;
        }

        // Similarly, if an ACK signal [I confirm I know you have woken up] is received from the previous node, then it can be confirmed that the previous node is awake
        if(receivedSlots[Slot.ACK_NEXT_ACTIVATION_SIGN_SLOT]!=null){
            this.prevWakeUp = true;
        }


        /// Sending part is as follows

        // Waking up round regardless of whether the other party (Prev and Next) is recorded as woken up in own record, must send own waking up signal
        if(Network.getRound() == this.startRound){
            Network.send(this.prevProcessor.getMyId(),this.forwardPrevSlot);
            forwardPrevSlot.clear();
            shouldForwardPrev = false;

            Network.send(this.nextProcessor.getMyId(),this.forwardNextSlot);
            forwardNextSlot.clear();
            shouldForwardNext = false;
            return;
        }

        // Otherwise, currently already past the waking up round. We choose whether to send based on whether the other party has woken up
        if(shouldForwardNext && nextWakenUp){
            Network.send(this.nextProcessor.getMyId(),this.forwardNextSlot);
            forwardNextSlot.clear();
            shouldForwardNext = false;
        }
        if(shouldForwardPrev){
            Network.send(this.prevProcessor.getMyId(),this.forwardPrevSlot);
            forwardPrevSlot.clear();
            shouldForwardPrev = false;
        }
    }

    public void setAsInterfaceProcessor() {
        this.type = ProcessorType.INTERFACE_PROCESSOR;
        this.leaderId = Integer.MIN_VALUE;
        this.startRound = Integer.MAX_VALUE;
    }


    // get method (although in the same class)
    public int getMyId() {
        return myId;
    }
}