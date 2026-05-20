import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public class AirlineReservation extends Reservation {
    private final String airlineName;
    private final String flightNumber;
    private final String classOfSeat;
    private final LocalDate departureDate;
    private final LocalTime departureTime;

    /**
     * Constructor.
     * @param reservationNumber The number of reservation.
     * @param client Client object.
     * @param allTravellers Set of all travellers.
     * @param airlineName The name of the airline.
     * @param flightNumber The flight number.
     * @param classOfSeat The class of the seat.
     * @param departureDate The departure date of the airline.
     * @param departureTime The departure time of the airline.
     */
    public AirlineReservation(String reservationNumber, Client client, Set<Traveller> allTravellers, String airlineName, String flightNumber, String classOfSeat, LocalDate departureDate, LocalTime departureTime) {
        super(reservationNumber, client, allTravellers);
        // Simple check for airlineName, flightNumber, classOfSeat, departureDate, and departureTime
        if (airlineName == null || airlineName.isEmpty())
            throw new IllegalArgumentException("Airline name is empty.");
        if (flightNumber == null || flightNumber.isEmpty())
            throw new IllegalArgumentException("Flight number is empty.");
        if (classOfSeat == null || classOfSeat.isEmpty())
            throw new IllegalArgumentException("Class of seat is empty.");
        if (departureDate == null || LocalDate.now().isAfter(departureDate))
            throw new IllegalArgumentException("Departure date cannot be in the past.");
        if (departureTime == null) throw new IllegalArgumentException("Unspecified departure time.");
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.classOfSeat = classOfSeat;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
    }

    /**
     * @return Airline Name.
     */
    public String getAirlineName() {
        return airlineName;
    }

    /**
     * @return Flight number.
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * @return Class of the flight seat.
     */
    public String getClassOfSeat() {
        return classOfSeat;
    }

    /**
     * @return Departure date of the flight.
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * @return Departure time of the flight.
     */
    public LocalTime getDepartureTime() {
        return departureTime;
    }
    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "AirlineReservation{" + "Reservation=" + super.toString() + ", " + "airlineName='" + airlineName + '\'' + ", flightNumber='" + flightNumber + '\'' + ", classOfSeat='" + classOfSeat + '\'' + ", departureDate=" + departureDate + ", departureTime=" + departureTime + '}';
    }

    // equals() and hashCode() for hash set operation.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        AirlineReservation that = (AirlineReservation) object;
        return Objects.equals(airlineName, that.airlineName) && Objects.equals(flightNumber, that.flightNumber) && Objects.equals(classOfSeat, that.classOfSeat) && Objects.equals(departureDate, that.departureDate) && Objects.equals(departureTime, that.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), airlineName, flightNumber, classOfSeat, departureDate, departureTime);
    }
}