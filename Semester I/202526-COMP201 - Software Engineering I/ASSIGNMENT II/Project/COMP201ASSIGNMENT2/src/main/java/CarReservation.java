import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CarReservation extends Reservation {
    private final String typeOfCar;
    private final LocalDate startDate;
    private final int daysOfHire;
    private final Set<String> driverLicenseNumbers;

    /**
     * Constructor.
     * @param reservationNumber The number of reservation.
     * @param client Client object.
     * @param allTravellers Set of all travellers.
     * @param typeOfCar The type of the car.
     * @param startDate The start date.
     * @param daysOfHire The days travellers want to hire.
     * @param driverLicenseNumbers A set, contains all the driver licence number of travellers.
     */
    public CarReservation(String reservationNumber, Client client, Set<Traveller> allTravellers, String typeOfCar, LocalDate startDate, int daysOfHire, Set<String> driverLicenseNumbers) {
        super(reservationNumber, client, allTravellers);
        // Simple check for typeOfCar, startDate, daysOfHire, and driverLicenceNumbers.
        if (typeOfCar == null || typeOfCar.isEmpty())
            throw new IllegalArgumentException("Type of car is empty.");
        if (startDate == null || LocalDate.now().isAfter(startDate))
            throw new IllegalArgumentException("Start date invalid.");
        if (daysOfHire <= 0)
            throw new IllegalArgumentException("Hire day should large than 0.");
        if (driverLicenseNumbers == null || driverLicenseNumbers.isEmpty())
            throw new IllegalArgumentException("Invalid driver's licence numbers.");
        this.typeOfCar = typeOfCar;
        this.startDate = startDate;
        this.daysOfHire = daysOfHire;
        this.driverLicenseNumbers = new HashSet<>(driverLicenseNumbers);
    }

    /**
     * @return The type of the car.
     */
    public String getTypeOfCar() {
        return typeOfCar;
    }

    /**
     * @return The start date of hiring the car.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return Number of day traveller want to hire the car.
     */
    public int getDaysOfHire() {return daysOfHire;}

    /**
     * @return A set contains all the driver licence numbers from travellers.
     */
    public Set<String> getDriverLicenseNumbers() {
        return Collections.unmodifiableSet(driverLicenseNumbers);
    }
    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "CarReservation{" + "Reservation=" + super.toString() + ", " + "typeOfCar='" + typeOfCar + '\'' + ", startDate=" + startDate + ", daysOfHire=" + daysOfHire + ", driverLicenseNumbers=" + driverLicenseNumbers + '}';
    }

    // equals() and hashCode() for hash set operation.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        CarReservation that = (CarReservation) object;
        return daysOfHire == that.daysOfHire && Objects.equals(typeOfCar, that.typeOfCar) && Objects.equals(startDate, that.startDate) && Objects.equals(driverLicenseNumbers, that.driverLicenseNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), typeOfCar, startDate, daysOfHire, driverLicenseNumbers);
    }
}
