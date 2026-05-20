import java.time.LocalDate;
import java.util.*;

public class Client extends Traveller{
    private final String address;
    private final Set<Reservation> allReservation;

    /**
     * Constructor
     * @param fullName Full name of the client.
     * @param address Address of the client.
     * @param allReservation A reservation set, modifiable parse to constructor, must eliminate after finishing all the operation.
     */
    public Client(String fullName, String passportNumber, LocalDate dateOfBirth, String address, Set<Reservation> allReservation) {
        super(fullName, passportNumber, dateOfBirth);
        // Simple check for fullName and address.
        if (address == null || address.isEmpty()) throw new IllegalArgumentException("Address is empty.");
        /*Cannot do a deep copy here (only here), because we keep the bidirectional dependency.*/
        /*Drop this parse parameter after using constructor.*/
        /*See Test.java class.*/
        this.address = address;
        this.allReservation = allReservation;
    }

    /**
     * @return The address of the client.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return A set contains all the reservation which the client can access and manage.
     */
    public Set<Reservation> getAllReservation(){return Collections.unmodifiableSet(allReservation);}

    // Here toString method didn't add allReservation because it will trap in infinitely loop
    // But can be accessed by using getAllReservation();.

    /**
     * @return A JSON pattern which can describe this object without reservation set.
     */
    @Override
    public String toString() {
        return "Client{" +
                "address='" + address + '\'' +
                '}';
    }
    // equals() and hashCode() for hash set operation.

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Client client = (Client) object;
        return Objects.equals(address, client.address) && Objects.equals(allReservation, client.allReservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address); // There will be infinitely loop if allReservation is hashed.
    }
}