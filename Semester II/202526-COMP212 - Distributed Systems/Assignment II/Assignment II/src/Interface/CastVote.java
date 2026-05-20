package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface CastVote extends Remote {
    int getTicket() throws RemoteException;

    void vote(int ticket, int choice) throws RemoteException;

    Map<Integer,Integer> getVotingResults(int ticket) throws RemoteException;

    List<Integer> getCandidatesList() throws RemoteException;
}