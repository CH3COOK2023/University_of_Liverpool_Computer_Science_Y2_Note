import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class HotelReservation extends Reservation {
    private final String roomType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String hotelName;
    private final String hotelAddress;

    /**
     * Constructor.
     * @param reservationNumber The number of reservation.
     * @param client Client object.
     * @param allTravellers Set of all travellers.
     * @param roomType The type of the room.
     * @param startDate Start date.
     * @param endDate End date.
     * @param hotelName The name of the hotel.
     * @param hotelAddress The address of the hotel.
     */
    public HotelReservation(String reservationNumber, Client client, Set<Traveller> allTravellers, String roomType, LocalDate startDate, LocalDate endDate, String hotelName, String hotelAddress) {
        super(reservationNumber, client, allTravellers);
        // Simple check for roomType, hotelName, hotelAddress, startDate and endDate
        if (roomType == null || roomType.isEmpty()) throw new IllegalArgumentException("Room type is empty.");
        if (hotelName == null || hotelName.isEmpty()) throw new IllegalArgumentException("Hotel name is empty.");
        if (hotelAddress == null || hotelAddress.isEmpty()) throw new IllegalArgumentException("Hotel address is empty.");
        if (startDate == null || endDate == null || startDate.isAfter(endDate))
            throw new IllegalArgumentException("Start and end date is invalid.");
        this.roomType = roomType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
    }

    /**
     * @return The type of the room of the hotel reservation.
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * @return The start date of the hotel reservation.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return The end date of the hotel reservation.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @return The hotel name.
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * @return The hotel address.
     */
    public String getHotelAddress() {
        return hotelAddress;
    }

    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "HotelReservation{" + "Reservation=" + super.toString() + ", " + "roomType='" + roomType + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", hotelName='" + hotelName + '\'' + ", hotelAddress='" + hotelAddress + '\'' + '}';
    }

    // equals() and hashCode() for hash set operation.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        HotelReservation that = (HotelReservation) object;
        return Objects.equals(roomType, that.roomType) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(hotelName, that.hotelName) && Objects.equals(hotelAddress, that.hotelAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomType, startDate, endDate, hotelName, hotelAddress);
    }
}
