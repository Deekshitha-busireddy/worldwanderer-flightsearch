package flight;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightSearchTest {

    // ===== Level 1/2 tests (kept as-is) =====

    @Test
    void testEmptyDestination() {
        FlightSearch fs = new FlightSearch("Passenger", "", 300);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testValidInput() {
        FlightSearch fs = new FlightSearch("Alice Johnson", "London", 1200);
        assertTrue(fs.validateSearch());
    }

    @Test
    void testEmptyName() {
        FlightSearch fs = new FlightSearch("", "Paris", 500);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testInvalidBudget() {
        FlightSearch fs = new FlightSearch("Bob", "Rome", 0);
        assertFalse(fs.validateSearch());
    }

    @Test
    void testInvalidCharactersInName() {
        FlightSearch fs = new FlightSearch("Bob@123", "Rome", 400);
        assertFalse(fs.validateSearch());
    }

    // ===== Level 3 tests =====

    @Test
    void testIataCodesMustBe3UppercaseLetters() {
        FlightSearch fs = new FlightSearch("Chris", "Tokyo", 900);
        fs.setDepartureAirportCode("JFK")
                .setDestinationAirportCode("NRT");   // OK
        assertTrue(fs.validateSearch());

        fs.setDestinationAirportCode("Nar");   // lower/mixed -> invalid
        assertFalse(fs.validateSearch());
    }

    @Test
    void testDepartureBeforeReturnDate() {
        FlightSearch fs = new FlightSearch("Dana", "Berlin", 800);
        fs.setDepartureAirportCode("LAX")
                .setDestinationAirportCode("TXL")
                .setDepartureDate(LocalDate.now().plusDays(10))
                .setReturnDate(LocalDate.now().plusDays(5)); // return before departure -> invalid
        assertFalse(fs.validateSearch());

        fs.setReturnDate(LocalDate.now().plusDays(20)); // now OK
        assertTrue(fs.validateSearch());
    }

    @Test
    void testPassengerCountsNonNegative() {
        FlightSearch fs = new FlightSearch("Evan", "Seoul", 700);
        fs.setDepartureAirportCode("SFO")
                .setDestinationAirportCode("ICN")
                .setAdultPassengerCount(1)
                .setChildPassengerCount(-1); // invalid
        assertFalse(fs.validateSearch());

        fs.setChildPassengerCount(0);
        assertTrue(fs.validateSearch());
    }

    @Test
    void testEmergencyRowRequiresAdultsNoChildrenNoInfants() {
        FlightSearch fs = new FlightSearch("Fiona", "Dubai", 1500);
        fs.setDepartureAirportCode("JFK")
                .setDestinationAirportCode("DXB")
                .setEmergencyRowSeating(true)
                .setAdultPassengerCount(0);  // must have >=1 adult
        assertFalse(fs.validateSearch());

        fs.setAdultPassengerCount(1).setInfantPassengerCount(1); // infants not allowed
        assertFalse(fs.validateSearch());

        fs.setInfantPassengerCount(0).setChildPassengerCount(0); // now valid
        assertTrue(fs.validateSearch());
    }

    @Test
    void testValidSeatingClassValues() {
        FlightSearch fs = new FlightSearch("Gina", "Sydney", 2000);
        fs.setDepartureAirportCode("LHR")
                .setDestinationAirportCode("SYD")
                .setSeatingClass("business");
        assertTrue(fs.validateSearch());

        fs.setSeatingClass("diamond"); // invalid option
        assertFalse(fs.validateSearch());
    }
}
