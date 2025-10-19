// src/test/java/flight/FlightSearchTest.java
package flight;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests cover:
 *  - each validation condition (including updated Condition 10)
 *  - attributes only set when valid (remain unchanged when invalid)
 *  - at least one "all-valid" case
 */
class FlightSearchTest {

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private String d(int daysFromToday) {
        return LocalDate.now().plusDays(daysFromToday).format(DMY);
    }

    private boolean runValid(FlightSearch fs) {
        // A baseline-valid set of inputs
        return fs.runFlightSearch(
                d(5), "mel", false,
                d(12), "pvg", "economy",
                2, 2, 0
        );
    }

    // Helper to assert attributes unchanged (all defaults) after a failed call
    private void assertUnchanged(FlightSearch fs) {
        assertNull(fs.getDepartureDate());
        assertNull(fs.getDepartureAirportCode());
        assertFalse(fs.isEmergencyRowSeating());
        assertNull(fs.getReturnDate());
        assertNull(fs.getDestinationAirportCode());
        assertNull(fs.getSeatingClass());
        assertEquals(0, fs.getAdultPassengerCount());
        assertEquals(0, fs.getChildPassengerCount());
        assertEquals(0, fs.getInfantPassengerCount());
    }

    @Test
    void testAllValid_setsAttributesAndReturnsTrue() {
        FlightSearch fs = new FlightSearch();
        boolean ok = fs.runFlightSearch(
                d(3), "syd", false,
                d(10), "cdg", "economy",
                1, 0, 0
        );
        assertTrue(ok);
        assertEquals(d(3), fs.getDepartureDate());
        assertEquals("syd", fs.getDepartureAirportCode());
        assertEquals(d(10), fs.getReturnDate());
        assertEquals("cdg", fs.getDestinationAirportCode());
        assertEquals("economy", fs.getSeatingClass());
        assertEquals(1, fs.getAdultPassengerCount());
        assertEquals(0, fs.getChildPassengerCount());
        assertEquals(0, fs.getInfantPassengerCount());
    }

    @Test
    void testTotalPassengersMustBeWithin1to9() {
        FlightSearch fs = new FlightSearch();

        // 0 passengers -> invalid
        boolean ok = fs.runFlightSearch(d(2), "mel", false, d(5), "pvg", "economy",
                0, 0, 0);
        assertFalse(ok); assertUnchanged(fs);

        // 10 passengers -> invalid
        ok = fs.runFlightSearch(d(2), "mel", false, d(5), "pvg", "economy",
                9, 1, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testChildrenNotInEmergencyOrFirst() {
        FlightSearch fs = new FlightSearch();
        // children + emergency -> invalid
        boolean ok = fs.runFlightSearch(d(3), "mel", true, d(7), "pvg", "economy",
                1, 1, 0);
        assertFalse(ok); assertUnchanged(fs);

        // children + first class -> invalid
        ok = fs.runFlightSearch(d(3), "mel", false, d(7), "pvg", "first",
                1, 1, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testInfantsNotInEmergencyOrBusiness() {
        FlightSearch fs = new FlightSearch();
        // infants + emergency -> invalid
        boolean ok = fs.runFlightSearch(d(3), "mel", true, d(7), "pvg", "economy",
                1, 0, 1);
        assertFalse(ok); assertUnchanged(fs);

        // infants + business -> invalid
        ok = fs.runFlightSearch(d(3), "mel", false, d(7), "pvg", "business",
                1, 0, 1);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testChildrenAtMostTwoPerAdult() {
        FlightSearch fs = new FlightSearch();
        // 3 children with 1 adult -> invalid
        boolean ok = fs.runFlightSearch(d(3), "mel", false, d(10), "pvg", "economy",
                1, 3, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testInfantsAtMostOnePerAdult() {
        FlightSearch fs = new FlightSearch();
        // 2 infants with 1 adult -> invalid
        boolean ok = fs.runFlightSearch(d(3), "mel", false, d(10), "pvg", "economy",
                1, 0, 2);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testDepartureNotInPast() {
        FlightSearch fs = new FlightSearch();
        String yesterday = LocalDate.now().minusDays(1).format(DMY);
        boolean ok = fs.runFlightSearch(yesterday, "mel", false, d(5), "pvg", "economy",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testStrictDateFormat() {
        FlightSearch fs = new FlightSearch();
        // invalid combination (29 Feb 2026 – 2026 is not leap year)
        boolean ok = fs.runFlightSearch("29/02/2026", "mel", false, d(10), "pvg", "economy",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testReturnNotBeforeDeparture() {
        FlightSearch fs = new FlightSearch();
        boolean ok = fs.runFlightSearch(d(10), "mel", false, d(5), "pvg", "economy",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testSeatingClassMustBeValid() {
        FlightSearch fs = new FlightSearch();
        boolean ok = fs.runFlightSearch(d(3), "mel", false, d(10), "pvg", "econom",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testEmergencyRowOnlyForEconomy() {
        FlightSearch fs = new FlightSearch();
        boolean ok = fs.runFlightSearch(d(3), "mel", true, d(10), "pvg", "business",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testAirportsAllowedAndNotSame() {
        FlightSearch fs = new FlightSearch();

        // invalid code
        boolean ok = fs.runFlightSearch(d(3), "xxx", false, d(10), "pvg", "economy",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);

        // same origin and destination
        ok = fs.runFlightSearch(d(3), "mel", false, d(10), "mel", "economy",
                1, 0, 0);
        assertFalse(ok); assertUnchanged(fs);
    }

    @Test
    void testBaselineValidHelper() {
        FlightSearch fs = new FlightSearch();
        assertTrue(runValid(fs), "Baseline valid set should pass");
    }
}
