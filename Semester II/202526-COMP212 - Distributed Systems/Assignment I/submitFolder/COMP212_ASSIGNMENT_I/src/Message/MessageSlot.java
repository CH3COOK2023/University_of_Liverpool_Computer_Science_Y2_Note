package Message;

import Constant.*;

import java.util.Arrays;

public class MessageSlot {
    private final Message[] slot;
    public MessageSlot(){
        this.slot = new Message[6];
        slot[Slot.RECEIVE_SLOT] = null;
        slot[Slot.TERMINATION_SLOT] = null;
        slot[Slot.TELL_NEXT_ACTIVATION_SIGN_SLOT] = null;
        slot[Slot.ACK_NEXT_ACTIVATION_SIGN_SLOT] = null;
        slot[Slot.TELL_PREV_ACTIVATION_SIGN_SLOT] = null;
        slot[Slot.ACK_PREV_ACTIVATION_SIGN_SLOT] = null;
    }
    public void setSlot(int SLOT, Message message){
        this.slot[SLOT] = message;
    }
    public Message[] getSlot(){
        return this.slot;
    }

    public void merge(MessageSlot from, MessageSlot to){
        Message[] fromSlot = from.slot;
        Message[] toSlot = to.slot;
        for (int i = 0; i < fromSlot.length; i++) {
            if(fromSlot[i]!=null) toSlot[i]=fromSlot[i];
        }
    }

    public void clear(){
        Arrays.fill(this.slot, null);
    }
}
