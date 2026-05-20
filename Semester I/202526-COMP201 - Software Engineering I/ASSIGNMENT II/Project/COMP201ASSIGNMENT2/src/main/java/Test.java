import java.time.*;
import java.util.*;

public class Test {
    public static void main(String[] args) {

        Client mainClient; // This is the client.
        // The operation below should be contained in a code block(or an function).
        // To ensure the client object is immutable.
        {
            // Now simulating a trip.
            // From British to Germany.
            // There are 3 traveller: Alice Anderson, Benjamin Baker, Charlie Cooper.
            // Main client is Alice Anderson.
            // Their passport number are (respectively): AA001,BB001,CC001
            // The date of birth are (respectively): 2000/01/01, 2001/01/01, 2002/01/01
            // Additionally, the address of Alice Anderson is "Liverpool Travel Company L35TR".
            
            Set<Reservation> reservations = new LinkedHashSet<>(); // This reservation should be eliminated in the code block to prevent modification outside.
            Client aliceAnderson = new Client("Alice Anderson", "AA001", LocalDate.of(2000, 1, 1),"Liverpool Travel Company L35TR", reservations);
            Traveller benjaminBaker = new Traveller("Benjamin Baker", "BB001", LocalDate.of(2001, 1, 1));
            Traveller charlieCooper = new Traveller("Charlie Cooper", "CC001", LocalDate.of(2002, 1, 1));
            
            // Add 3 people in that set.
            Set<Traveller> travellers = new LinkedHashSet<>();
            travellers.add(aliceAnderson); // Alice Anderson is client as well as traveller.
            travellers.add(benjaminBaker);
            travellers.add(charlieCooper);
            
            // Create an airline reservation.
            // Reservation number : "AL_001"
            // Airline Name : "LIVERPOOL_AIRLINE"
            // Flight Number : "COMP201FLIGHT"
            // Class of Seat : "FIRST CLASS"
            // Departure time : 2027/01/02-13:30(PM)
            AirlineReservation airlineReservations = new AirlineReservation("AL_001", aliceAnderson, travellers, "LIVERPOOL_AIRLINE", "COMP201FLIGHT", "FIRST CLASS", LocalDate.of(2027, 1, 1), LocalTime.of(13, 30));
            // Create a hotel reservation.
            // Reservation Number : "HT_001"
            // Room type : "Double Bed Room"
            // Hotel Name : "Germany Deluxe Hotel"
            // From/ To : 2027/01/02 - 2027/01/04
            // Hotel Address : "Deluxe Town"
            HotelReservation hotelReservations = new HotelReservation("HT_001", aliceAnderson, travellers, "Double Bed Room", LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 4), "Germany Deluxe Hotel", "Deluxe Town");
            // Create a car reservation.
            // Driver licence number from Benjamin Baker and Charlie Cooper (respectively) : DLN_001, DLN_002.
            // Reservation Number : "CR_001"
            // Type of car : "5 Seats Car"
            // Hiring day : 3
            // Start from : 2027/01/02
            Set<String> driverLicenceNumbers = new LinkedHashSet<>();
            driverLicenceNumbers.add("DLN_001");
            driverLicenceNumbers.add("DLN_002");
            CarReservation carReservations = new CarReservation("CR_001", aliceAnderson, travellers, "5 Seats Car", LocalDate.of(2027, 1, 2), 3, driverLicenceNumbers);
            // Create an insurance reservation.
            // Reservation Number : "IR_001"
            // Valid : 2027/01/01 - 2027/12/31
            // Level of Cover : "GOLD"
            InsuranceReservation insuranceReservations = new InsuranceReservation("IR_001", aliceAnderson, travellers, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 12, 31), "GOLD");
            // Link the four reservation to reservations set.
            reservations.add(airlineReservations);
            reservations.add(hotelReservations);
            reservations.add(carReservations);
            reservations.add(insuranceReservations);

            mainClient = aliceAnderson; // Finally linked the mainClient (which exposed to outside)
        }

        // Now the client is immutable object.
        System.out.println(SPLIT);
        System.out.println("Main Client Full Name : " + mainClient.getFullName());
        System.out.println("Main Client Passport Number : " + mainClient.getPassportNumber());
        System.out.println("Main Client Address : " + mainClient.getAddress());
        System.out.println("Main Client Date of Birth : " + mainClient.getDateOfBirth());
        for (Reservation reservation : mainClient.getAllReservation()) {
            System.out.println(SPLIT_SINGLE);
            System.out.println("Reservation ID : " + reservation.getReservationNumber());
            for (Traveller eachTraveller : reservation.getAllTravellers()) {
                System.out.println("\tName: "+eachTraveller.getFullName());
                System.out.println("\t\tPassport Number : "+eachTraveller.getPassportNumber());
                System.out.println("\t\tDate of Birth : "+eachTraveller.getDateOfBirth());
            }
            System.out.println("\tFurther Detail : " + reservation.toString().replace("{","\n\t\t\t\t\t{"));
            System.out.println(SPLIT_SINGLE);
        }
        System.out.println(SPLIT);

        // There will be a conflict between Defensive Copy and Bidirectional Association
        // So in the `Test.java` Class
        // We have to make sure `Set<Reservation> reservations = new LinkedHashSet<>();` was included in a code block`{}`
        // After all operations, the object `reservations ` will be eliminated.
        // All the object will then be immutable.
        // mainClient                     --> cannot be further modified.
        // mainClient.getAllReservation() --> cannot be further modified.
        // mainClient.get*()              --> cannot be further modified.
        // mainClient is immutable.
    }
    private static final String SPLIT =        "======================================================================================";
    private static final String SPLIT_SINGLE = "--------------------------------------------------------------------------------------";

}
