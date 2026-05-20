import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Reservation {
    private final String reservationNumber;
    private final Client client;
    private final Set<Traveller> allTravellers;

    /**
     * Constructor.
     * @param reservationNumber The number of reservation.
     * @param client Client object.
     * @param allTravellers Set of all travellers.
     */
    public Reservation(String reservationNumber, Client client, Set<Traveller> allTravellers) {
        // Simple check reservationNumber, Client, and allTravellers.
        if (reservationNumber == null || reservationNumber.isEmpty())
            throw new IllegalArgumentException("Reservation number is empty.");
        if (client == null)
            throw new IllegalArgumentException("Client is empty.");
        if (allTravellers == null || allTravellers.isEmpty())
            throw new IllegalArgumentException("There must be at least 1 traveller.");

        this.reservationNumber = reservationNumber;
        this.client = client;
        this.allTravellers = new HashSet<>(allTravellers); // Deep copy to make sure immutable.
    }

    /**
     * @return A reservation number of the corresponding (instance of the) reservation.
     */
    public String getReservationNumber() {
        return reservationNumber;
    }

    /**
     * @return The client. The person who can check for all the corresponding (instance of the) reservation.
     */
    public Client getClient() {
        return client;
    }

    /**
     * @return Unmodifiable set, all travellers.
     */
    public Set<Traveller> getAllTravellers() {
        return Collections.unmodifiableSet(allTravellers);
    }

    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "Reservation{" + "reservationNumber='" + reservationNumber + '\'' + ", clientName=" + client + ", allTravellers=" + allTravellers + '}';
    }

    // equals() and hashCode() for hash set operation.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Reservation that = (Reservation) object;
        return Objects.equals(reservationNumber, that.reservationNumber) && Objects.equals(client, that.client) && Objects.equals(allTravellers, that.allTravellers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationNumber, client, allTravellers);
    }
}
