package ClientService;

import Configuration.GlobalConfiguration;
import Interface.CastVote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import Exception.*;
import Log.Logging;

public class Client {
    private String clientUUID;
    private Registry registry;
    private CastVote votingSystem;
    private int myTicket;
    public Client() throws Exception{
        registry();
        connect();
    }
    private void registry(){
        try {
            this.registry = LocateRegistry.getRegistry(GlobalConfiguration.HOST, GlobalConfiguration.PORT);
        } catch (RemoteException e) {
            Logging.error("CLIENT    | Cannot register to RMI registry.");
            throw new ClientRegistryException(ExceptionName.CLIENT_REGISTRY_EXCEPTION);
        }
        Logging.info("CLIENT    | Successfully registered to RMI Service. Host = {}. Port = {}.", GlobalConfiguration.HOST,GlobalConfiguration.PORT);
    }
    private void connect(){
        try {
            this.votingSystem = (CastVote) registry.lookup(GlobalConfiguration.RMI_SERVICE_NAME);
        } catch (Exception e) {
            Logging.error("CLIENT    | Cannot connect to RMI service.");
            throw new ClientConnectionException(ExceptionName.CLIENT_CONNECTION_EXCEPTION);
        }
        Logging.info("CLIENT    | Successfully connect to RMI service.");
    }
    public void getTicket(){
        try {
            this.myTicket = votingSystem.getTicket();
        } catch (RemoteException e) {
            Logging.error("CLIENT    | Cannot fetch ticket.");
            throw new ClientFetchTicketException(ExceptionName.CLIENT_FETCH_TICKET_EXCEPTION);
        }
        Logging.info("CLIENT    | Successfully fetch ticket, ticket number {}", this.myTicket);
    }
    public void vote(int choice){
        try {
            votingSystem.vote(this.myTicket, choice);
        } catch (RemoteException e) {
            Logging.error("CLIENT    | Cannot vote to remote service. Ticket = {}. Choice = {}.",this.myTicket, choice);
            throw new ClientVotingFailException(ExceptionName.CLIENT_VOTING_FAIL_EXCEPTION);
        }
        Logging.info("CLIENT    | Successfully vote to remote service. Ticket = {}. Choice = {}.",this.myTicket, choice);
    }
    public Map<Integer, Integer> fetchVotingResults() {
        try {
            Map<Integer, Integer> results = votingSystem.getVotingResults(this.myTicket);
            Logging.info("CLIENT    | Successfully fetched voting results.");
            return results;
        } catch (RemoteException e) {
            Logging.error("CLIENT    | Cannot fetch voting results for ticket = {}.", this.myTicket);
            return null;
        }
    }
    public List<Integer> getCandidateList(){
        try {
            return votingSystem.getCandidatesList();
        } catch (RemoteException e) {
            Logging.info("CLIENT    | Cannot fetch candidate list.");
            return null;
        }
    }

}