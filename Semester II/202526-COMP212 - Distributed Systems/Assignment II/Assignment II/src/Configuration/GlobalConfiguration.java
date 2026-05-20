package Configuration;

public class GlobalConfiguration {
    // RMI Settings
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    // Log Settings
    public static final String LOG_FILE_PATH = "logs/voting.log";
    // Voting Settings
    public static final int NUM_OF_VOTING_OPTIONS = 15;
    // Other Settings
    public static final String RMI_SERVICE_NAME = "VotingService";
    public static final long SERVICE_RANDOM_SEED = 67;
}
