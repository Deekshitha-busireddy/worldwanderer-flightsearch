package flight;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * Level 3 FlightSearch domain class.
 * - Keeps Level 1/2 behaviour (constructor + validateSearch()) so your old tests still pass
 * - Adds extra attributes & validations used in new tests
 */
public class FlightSearch {

    // ===== Level 1/2 fields =====
    private String passengerName;
    private String destination;  // free-text city/country name
    private double budget;

    // ===== Level 3 fields =====
    private String departureAirportCode;      // IATA code, e.g., "JFK"
    private String destinationAirportCode;    // IATA code, e.g., "LHR"
    private LocalDate departureDate;          // must be before returnDate (if returnDate present)
    private LocalDate returnDate;             // optional in some scenarios
    private boolean emergencyRowSeating;      // allowed only with adults & no infants
    private String seatingClass;              // economy, premium, business, first
    private int adultPassengerCount;          // >= 0
    private int childPassengerCount;          // >= 0
    private int infantPassengerCount;         // >= 0

    private static final Set<String> ALLOWED_CLASSES =
            Set.of("economy", "premium", "business", "first");

    // ---------- Constructors ----------
    /** Level 1/2 constructor kept intact so your existing tests work */
    public FlightSearch(String passengerName, String destination, double budget) {
        this.passengerName = passengerName;
        this.destination = destination;
        this.budget = budget;
    }

    /** Empty constructor (used by some tools/tests) */
    public FlightSearch() {}

    // ---------- Core validation (kept compatible with your level-1/2 tests) ----------
    public boolean validateSearch() {
        // name: must exist & only letters/spaces/hyphen, length >= 1
        if (passengerName == null || passengerName.isBlank()) return false;
        if (!passengerName.matches("[A-Za-z\\- ]+")) return false;

        // destination: must exist & not be unreasonably long (<= 30)
        if (destination == null || destination.isBlank()) return false;
        if (destination.length() > 30) return false;

        // budget must be positive
        if (budget <= 0) return false;

        // ===== Level 3 additions (only applied if the fields are set) =====

        // airport codes (if provided): must be exactly 3 uppercase letters
        if (departureAirportCode != null && !departureAirportCode.matches("^[A-Z]{3}$")) return false;
        if (destinationAirportCode != null && !destinationAirportCode.matches("^[A-Z]{3}$")) return false;

        // dates (if both provided): departure must be before return
        if (departureDate != null && returnDate != null) {
            if (!departureDate.isBefore(returnDate)) return false;
        }

        // passenger counts must be non-negative
        if (adultPassengerCount < 0 || childPassengerCount < 0 || infantPassengerCount < 0) return false;

        // if emergency row seating, must have at least 1 adult and no infants/children
        if (emergencyRowSeating) {
            if (adultPassengerCount < 1) return false;
            if (infantPassengerCount > 0) return false;
            if (childPassengerCount > 0) return false;
        }

        // seating class (if provided) must be valid
        if (seatingClass != null) {
            if (!ALLOWED_CLASSES.contains(seatingClass.toLowerCase())) return false;
        }

        return true;
    }

    // ---------- Getters ----------
    public String getPassengerName() { return passengerName; }
    public String getDestination() { return destination; }
    public double getBudget() { return budget; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public LocalDate getDepartureDate() { return departureDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public String getSeatingClass() { return seatingClass; }
    public int getAdultPassengerCount() { return adultPassengerCount; }
    public int getChildPassengerCount() { return childPassengerCount; }
    public int getInfantPassengerCount() { return infantPassengerCount; }

    // ---------- Fluent setters (handy for tests) ----------
    public FlightSearch setDepartureAirportCode(String code) {
        this.departureAirportCode = code; return this;
    }
    public FlightSearch setDestinationAirportCode(String code) {
        this.destinationAirportCode = code; return this;
    }
    public FlightSearch setDepartureDate(LocalDate date) {
        this.departureDate = date; return this;
    }
    public FlightSearch setReturnDate(LocalDate date) {
        this.returnDate = date; return this;
    }
    public FlightSearch setEmergencyRowSeating(boolean emergency) {
        this.emergencyRowSeating = emergency; return this;
    }
    public FlightSearch setSeatingClass(String seatingClass) {
        this.seatingClass = seatingClass; return this;
    }
    public FlightSearch setAdultPassengerCount(int n) {
        this.adultPassengerCount = n; return this;
    }
    public FlightSearch setChildPassengerCount(int n) {
        this.childPassengerCount = n; return this;
    }
    public FlightSearch setInfantPassengerCount(int n) {
        this.infantPassengerCount = n; return this;
    }

    // ---------- toString (nice for debugging) ----------
    @Override
    public String toString() {
        return "FlightSearch[" +
                "passenger='" + passengerName + '\'' +
                ", destination='" + destination + '\'' +
                ", budget=" + budget +
                ", depIATA=" + departureAirportCode +
                ", destIATA=" + destinationAirportCode +
                ", depDate=" + departureDate +
                ", retDate=" + returnDate +
                ", emergencyRow=" + emergencyRowSeating +
                ", class='" + seatingClass + '\'' +
                ", adults=" + adultPassengerCount +
                ", children=" + childPassengerCount +
                ", infants=" + infantPassengerCount +
                ']';
    }

    // ---------- equals/hashCode (optional but useful) ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightSearch)) return false;
        FlightSearch that = (FlightSearch) o;
        return Double.compare(that.budget, budget) == 0 &&
                emergencyRowSeating == that.emergencyRowSeating &&
                adultPassengerCount == that.adultPassengerCount &&
                childPassengerCount == that.childPassengerCount &&
                infantPassengerCount == that.infantPassengerCount &&
                Objects.equals(passengerName, that.passengerName) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(departureAirportCode, that.departureAirportCode) &&
                Objects.equals(destinationAirportCode, that.destinationAirportCode) &&
                Objects.equals(departureDate, that.departureDate) &&
                Objects.equals(returnDate, that.returnDate) &&
                Objects.equals(seatingClass, that.seatingClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerName, destination, budget,
                departureAirportCode, destinationAirportCode,
                departureDate, returnDate, emergencyRowSeating,
                seatingClass, adultPassengerCount, childPassengerCount, infantPassengerCount);
    }
}
