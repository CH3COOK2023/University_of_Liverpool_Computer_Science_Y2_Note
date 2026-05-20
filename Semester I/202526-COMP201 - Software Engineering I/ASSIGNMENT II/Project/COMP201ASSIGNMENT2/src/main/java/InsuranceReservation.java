import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class InsuranceReservation extends Reservation {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String levelOfCover;

    /**
     * Constructor.
     * @param reservationNumber The number of reservation.
     * @param client Client object.
     * @param allTravellers Set of all travellers.
     * @param startDate The start date.
     * @param endDate The end date.
     * @param levelOfCover the level of insurance cover.
     */
    public InsuranceReservation(String reservationNumber, Client client, Set<Traveller> allTravellers, LocalDate startDate, LocalDate endDate, String levelOfCover) {
        super(reservationNumber, client, allTravellers);
        // Simple check for startDate, endDate, and levelOfCover
        if (startDate == null || endDate == null || startDate.isAfter(endDate))
            throw new IllegalArgumentException("Start and end date is invalid.");
        if (levelOfCover == null || levelOfCover.isEmpty())
            throw new IllegalArgumentException("Cover level is empty.");
        this.startDate = startDate;
        this.endDate = endDate;
        this.levelOfCover = levelOfCover;
    }

    /**
     * @return The start date of the insurance.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return The end date of the insurance.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @return The level of the insurance.
     */
    public String getLevelOfCover() {
        return levelOfCover;
    }

    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "InsuranceReservation{" + "Reservation=" + super.toString() + ", " + "startDate=" + startDate + ", endDate=" + endDate + ", levelOfCover='" + levelOfCover + '\'' + '}';
    }

    // equals() and hashCode() for hash set operation.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        InsuranceReservation that = (InsuranceReservation) object;
        return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(levelOfCover, that.levelOfCover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate, levelOfCover);
    }
}
