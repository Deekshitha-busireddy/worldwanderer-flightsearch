package flight;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightSearchTest {

    @Test
    void testValidInput() {
        FlightSearch fs = new FlightSearch("Chris", "Tokyo", 900)
                .setDepartureDate(LocalDate.now().plusDays(10))
                .setReturnDate(LocalDate.now().plusDays(20))
                .setDepartureAirportCode("JFK")
                .setDestinationAirportCode("NRT")
                .setAdultPassengerCount(1)
                .setChildPassengerCount(0)
                .setInfantPassengerCount(0)
                .setSeatingClass("Economy")
                .setEmergencyRowSeating(false);

        assertTrue(fs.validateSearch());
    }

    @Test
    void testEmptyDestination() {
        FlightSearch fs = new FlightSearch("Chris", "", 500);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testEmptyName() {
        FlightSearch fs = new FlightSearch("", "Berlin", 400);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testInvalidBudget() {
        FlightSearch fs = new FlightSearch("Chris", "Berlin", -1);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testPassengerCountsNonNegative() {
        FlightSearch fs = new FlightSearch("A", "B", 100)
                .setAdultPassengerCount(-1);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testEmergencyRowRequiresAdultsNoChildrenNoInfants() {
        FlightSearch fs = new FlightSearch("A", "B", 100)
                .setEmergencyRowSeating(true)
                .setAdultPassengerCount(1)
                .setChildPassengerCount(0)
                .setInfantPassengerCount(0);
        assertTrue(fs.validateSearch()); // valid case
    }

    @Test
    void testIataCodesMustBe3UppercaseLetters() {
        FlightSearch fs = new FlightSearch("A", "B", 100)
                .setDepartureAirportCode("JFK")
                .setDestinationAirportCode("NRT");
        assertTrue(fs.validateSearch());

        fs.setDestinationAirportCode("Nar"); // invalid (not uppercase)
        assertFalse(fs.validateSearch());
    }

    @Test
    void testValidSeatingClassValues() {
        String[] allowed = {"economy", "PREMIUM", "Business", "FIRST"};
        for (String sc : allowed) {
            FlightSearch fs = new FlightSearch("A", "B", 100)
                    .setSeatingClass(sc);
            assertTrue(fs.validateSearch(), "Should allow: " + sc);
        }

        FlightSearch bad = new FlightSearch("A", "B", 100)
                .setSeatingClass("DELUXE");
        assertFalse(bad.validateSearch());
    }

    @Test
    void testDepartureBeforeReturnDate() {
        FlightSearch fs = new FlightSearch("A", "B", 100)
                .setDepartureDate(LocalDate.now().plusDays(1))
                .setReturnDate(LocalDate.now().plusDays(5));
        assertTrue(fs.validateSearch());
    }

    @Test
    void testInvalidBudgetWithEverythingElseValid() {
        FlightSearch fs = new FlightSearch("Chris", "Tokyo", 0) // not positive
                .setDepartureAirportCode("JFK")
                .setDestinationAirportCode("NRT")
                .setDepartureDate(LocalDate.now().plusDays(7))
                .setReturnDate(LocalDate.now().plusDays(9))
                .setAdultPassengerCount(1)
                .setSeatingClass("ECONOMY");
        assertFalse(fs.validateSearch());
    }
}
