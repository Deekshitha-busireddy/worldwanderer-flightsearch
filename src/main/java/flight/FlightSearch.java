// src/main/java/flight/FlightSearch.java
package flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Set;

/**
 * Validates a flight search request and, if valid, stores the parameters
 * in the object's attributes. If invalid, attributes remain unchanged.
 *
 * Spec highlights:
 *  - Total passengers 1..9
 *  - Children: not allowed in emergency rows or first class; <= 2 per adult
 *  - Infants: not allowed in emergency rows or business class; <= 1 per adult
 *  - Dates: DD/MM/YYYY (STRICT), departure not in past, return not before departure
 *  - Seating class in {economy, premium economy, business, first}
 *  - Emergency rows only allowed for economy (updated Condition 10 wording)
 *  - Airports allowed: syd, mel, lax, cdg, del, pvg, doh; origin != destination
 *  - All input strings are expected in lowercase per assignment note
 */
public class FlightSearch {

    // Stored attributes (as strings per assignment starter code)
    private String  departureDate;          // DD/MM/YYYY
    private String  departureAirportCode;   // e.g., "mel"
    private boolean emergencyRowSeating;
    private String  returnDate;             // DD/MM/YYYY
    private String  destinationAirportCode; // e.g., "pvg"
    private String  seatingClass;           // economy | premium economy | business | first
    private int     adultPassengerCount;
    private int     childPassengerCount;
    private int     infantPassengerCount;

    // Allowed values
    private static final Set<String> ALLOWED_AIRPORTS =
            Set.of("syd", "mel", "lax", "cdg", "del", "pvg", "doh");

    private static final Set<String> ALLOWED_CLASSES =
            Set.of("economy", "premium economy", "business", "first");

    private static final DateTimeFormatter STRICT_DMY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses STRICT dd/MM/yyyy. Returns null if invalid.
     */
    private LocalDate parseStrict(String dmy) {
        try {
            return LocalDate.parse(dmy, STRICT_DMY);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Validates according to all conditions. On success, stores attributes and returns true.
     * On failure, attributes are NOT modified and false is returned.
     */
    public boolean runFlightSearch(String departureDate, String departureAirportCode, boolean emergencyRowSeating,
                                   String returnDate, String destinationAirportCode, String seatingClass,
                                   int adultPassengerCount, int childPassengerCount, int infantPassengerCount) {

        // Keep a snapshot of previous values (so we can restore on invalid)
        final String  prevDepDate   = this.departureDate;
        final String  prevDepCode   = this.departureAirportCode;
        final boolean prevEmergency = this.emergencyRowSeating;
        final String  prevRetDate   = this.returnDate;
        final String  prevDstCode   = this.destinationAirportCode;
        final String  prevClass     = this.seatingClass;
        final int     prevAdults    = this.adultPassengerCount;
        final int     prevChildren  = this.childPassengerCount;
        final int     prevInfants   = this.infantPassengerCount;

        // ---- Validation starts ----

        // Seating class must be valid
        if (seatingClass == null || !ALLOWED_CLASSES.contains(seatingClass)) {
            return false;
        }

        // Airports must be valid and different
        if (departureAirportCode == null || destinationAirportCode == null
                || !ALLOWED_AIRPORTS.contains(departureAirportCode)
                || !ALLOWED_AIRPORTS.contains(destinationAirportCode)
                || departureAirportCode.equals(destinationAirportCode)) {
            return false;
        }

        // Dates strict format, valid combination, non past, and return >= departure
        LocalDate dep = parseStrict(departureDate);
        LocalDate ret = parseStrict(returnDate);
        if (dep == null || ret == null) return false;

        LocalDate today = LocalDate.now();
        if (dep.isBefore(today)) return false;        // Condition 6
        if (ret.isBefore(dep)) return false;          // Condition 8

        // Passenger totals: at least 1 and <= 9
        int total = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (total < 1 || total > 9) return false;     // Condition 1

        // Emergency row: only economy
        if (emergencyRowSeating && !"economy".equals(seatingClass)) {
            return false;                              // Updated Condition 10
        }

        // Children rules
        if (childPassengerCount > 0) {
            if (emergencyRowSeating) return false;     // Condition 2 (no children in emergency)
            if ("first".equals(seatingClass)) return false; // Condition 2 (no children in first)
            if (adultPassengerCount * 2 < childPassengerCount) return false; // Condition 4
        }

        // Infants rules
        if (infantPassengerCount > 0) {
            if (emergencyRowSeating) return false;     // Condition 3 (no infants in emergency)
            if ("business".equals(seatingClass)) return false; // Condition 3 (no infants in business)
            if (adultPassengerCount < infantPassengerCount) return false;    // Condition 5
        }

        // ---- If we got here, everything is valid: store attributes ----
        this.departureDate          = departureDate;
        this.departureAirportCode   = departureAirportCode;
        this.emergencyRowSeating    = emergencyRowSeating;
        this.returnDate             = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass           = seatingClass;
        this.adultPassengerCount    = adultPassengerCount;
        this.childPassengerCount    = childPassengerCount;
        this.infantPassengerCount   = infantPassengerCount;

        return true;
    }

    // -------- Getters (for tests / demo) --------
    public String  getDepartureDate()          { return departureDate; }
    public String  getDepartureAirportCode()   { return departureAirportCode; }
    public boolean isEmergencyRowSeating()     { return emergencyRowSeating; }
    public String  getReturnDate()             { return returnDate; }
    public String  getDestinationAirportCode() { return destinationAirportCode; }
    public String  getSeatingClass()           { return seatingClass; }
    public int     getAdultPassengerCount()    { return adultPassengerCount; }
    public int     getChildPassengerCount()    { return childPassengerCount; }
    public int     getInfantPassengerCount()   { return infantPassengerCount; }

    @Override
    public String toString() {
        return "FlightSearch{" +
                "dep='" + departureDate + '\'' +
                ", from='" + departureAirportCode + '\'' +
                ", emergency=" + emergencyRowSeating +
                ", ret='" + returnDate + '\'' +
                ", to='" + destinationAirportCode + '\'' +
                ", class='" + seatingClass + '\'' +
                ", adults=" + adultPassengerCount +
                ", children=" + childPassengerCount +
                ", infants=" + infantPassengerCount +
                '}';
    }

    // ------ Helpers for tests to check "unchanged when invalid" -------
    public static FlightSearch emptySnapshot() {
        return new FlightSearch(); // has null/0/false defaults
    }
}
