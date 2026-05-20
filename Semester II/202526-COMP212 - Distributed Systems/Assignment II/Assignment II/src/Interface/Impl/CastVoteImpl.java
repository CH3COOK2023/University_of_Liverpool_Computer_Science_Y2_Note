package Interface.Impl;

import Configuration.GlobalConfiguration;
import Exception.*;
import Interface.CastVote;
import Log.Logging;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CastVoteImpl extends UnicastRemoteObject implements CastVote {

    private final HashSet<Integer> validTickets;
    private final HashSet<Integer> votedTickets;
    private final Map<Integer, Integer> votingMap;
    private final Random random;

    // Lock to handle high-concurrency access
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CastVoteImpl() throws Exception {
        super();
        validTickets = new HashSet<>();
        votedTickets = new HashSet<>();
        votingMap = new HashMap<>();
        random = new Random(GlobalConfiguration.SERVICE_RANDOM_SEED);
        generateCandidates();
    }

    private void generateCandidates() {
        if(GlobalConfiguration.NUM_OF_VOTING_OPTIONS < 1 ){
            Logging.error("SERVICE   | Invalid number of voting options. Number of voting options = {}.",GlobalConfiguration.NUM_OF_VOTING_OPTIONS);
            throw new ServiceInvalidConfigurationArgumentException(ExceptionName.SERVICE_INVALID_CONFIGURATION_ARGUMENT_EXCEPTION);
        }
        for (int i = 1; i <= GlobalConfiguration.NUM_OF_VOTING_OPTIONS; i++) votingMap.put(i, 0);
        Logging.info("SERVICE   | Successfully generate candidates from 1 to {}.",GlobalConfiguration.NUM_OF_VOTING_OPTIONS);
    }

    public List<Integer> getCandidatesList() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(votingMap.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getTicket() throws RemoteException {
        lock.writeLock().lock();
        try {
            int ticket = random.nextInt();
            while (this.validTickets.contains(ticket) || this.votedTickets.contains(ticket)) {
                ticket = random.nextInt();
            }
            validTickets.add(ticket);
            Logging.info("SERVICE   | Successfully generated a ticket. Ticket = {}.", ticket);
            return ticket;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void vote(int ticket, int choice) throws RemoteException {
        lock.writeLock().lock();
        try {
            if (!validTickets.contains(ticket)){
                Logging.error("SERVICE   | Invalid Ticket. Ticket = {}.", ticket);
                throw new ServiceInvalidTicketException(ExceptionName.SERVICE_INVALID_TICKET_EXCEPTION);
            }
            if (!votingMap.containsKey(choice)){
                Logging.error("SERVICE   | Invalid Choice. Choice = {}.", choice);
                throw new ServiceInvalidChoiceException(ExceptionName.SERVICE_INVALID_CHOICE_EXCEPTION);
            }
            Logging.info("SERVICE   | Vote Success. Ticket = {}. Choice = {}.", ticket, choice);
            validTickets.remove(ticket);
            votedTickets.add(ticket);
            votingMap.merge(choice, 1, Integer::sum);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<Integer, Integer> getVotingResults(int ticket) throws RemoteException {
        lock.readLock().lock();
        try {
            if(!votedTickets.contains(ticket)){
                Logging.error("SERVICE   | Cannot get voting results. Ticket = {}.", ticket);
                throw new ServiceResultAccessControlException(ExceptionName.SERVICE_RESULT_ACCESS_CONTROL_EXCEPTION);
            }
            Logging.info("SERVICE   | Successfully get voting results. Ticket = {}.", ticket);
            // Return a copy to prevent external modification issues while still holding the read state
            return new HashMap<>(votingMap);
        } finally {
            lock.readLock().unlock();
        }
    }
}