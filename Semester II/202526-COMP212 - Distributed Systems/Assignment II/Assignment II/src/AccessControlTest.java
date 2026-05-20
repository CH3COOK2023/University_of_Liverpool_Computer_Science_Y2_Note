import ClientService.Client;
import ClientService.Server;
import Log.Logging;

public class AccessControlTest {
    public static void main(String[] args) throws Exception {
        // 1. start server
        new Thread(() -> {
            try {
                Server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 2. waiting RMI start...
        Logging.info("TEST      | Waiting for RMI server to start (5000ms) ...");
        Thread.sleep(5000);

        // test if the server denied fetching result before voting
        try{
            Client client = new Client();
            client.getTicket();
            client.fetchVotingResults();
            Logging.error("TEST      | Test failed.");
        }catch (Exception e){
            Logging.info("TEST      | Test success! Server denied the request because : {}", e.getMessage());
        }

        // test if the server denied the vote if ticket invalid
        try{
            Client client = new Client();
            client.vote(1);
            Logging.error("TEST      | Test failed.");
        }catch (Exception e){
            Logging.info("TEST      | Test success! Server denied the voting because : {}", e.getMessage());
        }

        // test if the server denied the illegal choice
        try{
            Client client = new Client();
            client.getTicket();
            client.vote(-1); // candidate -1 does not exist.
            Logging.error("TEST      | Test failed.");
        }catch (Exception e){
            Logging.info("TEST      | Test success! Server denied the voting because : {}", e.getMessage());
        }
        // 4. test if the server denied double voting (using the same ticket twice)
        try {
            Client client = new Client();
            client.getTicket();
            client.vote(1); // First vote: should succeed
            Logging.info("TEST      | First vote succeeded, attempting second vote...");
            client.vote(2); // Second vote with the SAME client/ticket: should be denied
            Logging.error("TEST      | Test failed. Server allowed double voting.");
        } catch (Exception e) {
            Logging.info("TEST      | Test success! Server denied double voting because : {}", e.getMessage());
        }

        Server.terminate();
        System.exit(0);
    }
}
