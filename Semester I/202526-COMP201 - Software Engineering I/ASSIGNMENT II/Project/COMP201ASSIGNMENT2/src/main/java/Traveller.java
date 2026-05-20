import java.time.LocalDate;
import java.util.Objects;

public class Traveller {
    private final String fullName;
    private final String passportNumber;
    private final LocalDate dateOfBirth;

    /**
     * Constructor.
     * @param fullName The full name of traveller.
     * @param passportNumber The passport number of the traveller.
     * @param dateOfBirth The date of birth of the traveller.
     */
    public Traveller(String fullName, String passportNumber, LocalDate dateOfBirth) {
        // Simple check for passportNumber, fullName and dateOfBirth.
        if (passportNumber == null || passportNumber.isEmpty())
            throw new IllegalArgumentException("Passport is empty.");
        if (fullName == null || fullName.isEmpty())
            throw new IllegalArgumentException("Full name is empty.");
        if (dateOfBirth == null || LocalDate.now().isBefore(dateOfBirth))
            throw new IllegalArgumentException("Invalid date of birth.");

        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return Full name of the traveller.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return Passport number of the traveller.
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * @return Date of birth of the traveller.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }


    /**
     * @return A JSON pattern which can describe this object.
     */
    @Override
    public String toString() {
        return "Traveller{" + "fullName='" + fullName + '\'' + ", passportNumber='" + passportNumber + '\'' + ", dateOfBirth=" + dateOfBirth + '}';
    }

    // equals() and hashCode() ensure that object Traveller will not be duplicated.
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Traveller traveller = (Traveller) object;
        return Objects.equals(fullName, traveller.fullName) && Objects.equals(passportNumber, traveller.passportNumber) && Objects.equals(dateOfBirth, traveller.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, passportNumber, dateOfBirth);
    }
}
