package flight;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Core model for a flight search with validation rules.
 */
public class FlightSearch {

    // --- Core inputs ---
    private String passengerName;
    private String destination;
    private double budget;

    // Dates
    private LocalDate departureDate;
    private LocalDate returnDate;

    // IATA airport codes (3 uppercase letters)
    private String departureAirportCode;
    private String destinationAirportCode;

    // Seating
    private String seatingClass; // ECONOMY, PREMIUM, BUSINESS, FIRST (case-insensitive)
    private boolean emergencyRowSeating;

    // Passenger counts
    private int adultPassengerCount;
    private int childPassengerCount;
    private int infantPassengerCount;

    // --- Ctors ---
    public FlightSearch() {}

    public FlightSearch(String passengerName, String destination, double budget) {
        this.passengerName = trimOrNull(passengerName);
        this.destination   = trimOrNull(destination);
        this.budget        = budget;
    }

    // --- Fluent setters / Getters ---

    public String getPassengerName() { return passengerName; }
    public FlightSearch setPassengerName(String passengerName) {
        this.passengerName = trimOrNull(passengerName);
        return this;
    }

    public String getDestination() { return destination; }
    public FlightSearch setDestination(String destination) {
        this.destination = trimOrNull(destination);
        return this;
    }

    public double getBudget() { return budget; }
    public FlightSearch setBudget(double budget) {
        this.budget = budget;
        return this;
    }

    public LocalDate getDepartureDate() { return departureDate; }
    public FlightSearch setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public LocalDate getReturnDate() { return returnDate; }
    public FlightSearch setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        return this;
    }

    public String getDepartureAirportCode() { return departureAirportCode; }
    public FlightSearch setDepartureAirportCode(String code) {
        this.departureAirportCode = trimOrNull(code);
        return this;
    }

    public String getDestinationAirportCode() { return destinationAirportCode; }
    public FlightSearch setDestinationAirportCode(String code) {
        this.destinationAirportCode = trimOrNull(code);
        return this;
    }

    public String getSeatingClass() { return seatingClass; }
    public FlightSearch setSeatingClass(String seatingClass) {
        this.seatingClass = seatingClass == null ? null : seatingClass.trim();
        return this;
    }

    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public FlightSearch setEmergencyRowSeating(boolean emergencyRowSeating) {
        this.emergencyRowSeating = emergencyRowSeating;
        return this;
    }

    public int getAdultPassengerCount() { return adultPassengerCount; }
    public FlightSearch setAdultPassengerCount(int adultPassengerCount) {
        this.adultPassengerCount = adultPassengerCount;
        return this;
    }

    public int getChildPassengerCount() { return childPassengerCount; }
    public FlightSearch setChildPassengerCount(int childPassengerCount) {
        this.childPassengerCount = childPassengerCount;
        return this;
    }

    public int getInfantPassengerCount() { return infantPassengerCount; }
    public FlightSearch setInfantPassengerCount(int infantPassengerCount) {
        this.infantPassengerCount = infantPassengerCount;
        return this;
    }

    // --- Validation rules ---

    public boolean validateSearch() {
        // 1) Name & destination must not be blank
        if (isBlank(passengerName)) return false;
        if (isBlank(destination)) return false;

        // 2) Budget must be positive
        if (budget <= 0) return false;

        // 3) If both dates present, departure must be before return
        if (departureDate != null && returnDate != null) {
            if (!departureDate.isBefore(returnDate)) return false;
        }

        // 4) IATA codes (if provided) must be exactly 3 uppercase letters
        if (departureAirportCode != null && !departureAirportCode.matches("[A-Z]{3}")) return false;
        if (destinationAirportCode != null && !destinationAirportCode.matches("[A-Z]{3}")) return false;

        // 5) Passenger counts must be non-negative
        if (adultPassengerCount < 0 || childPassengerCount < 0 || infantPassengerCount < 0) return false;

        // 6) Emergency row: at least 1 adult, no children/infants
        if (emergencyRowSeating) {
            if (adultPassengerCount < 1) return false;
            if (childPassengerCount > 0 || infantPassengerCount > 0) return false;
        }

        // 7) Seating class must be allowed (if provided)
        if (seatingClass != null) {
            String sc = seatingClass.trim().toUpperCase();
            switch (sc) {
                case "ECONOMY":
                case "PREMIUM":
                case "BUSINESS":
                case "FIRST":
                    break; // allowed
                default:
                    return false;
            }
        }

        return true;
    }

    // --- Helpers ---
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "FlightSearch{" +
                "passengerName='" + passengerName + '\'' +
                ", destination='" + destination + '\'' +
                ", budget=" + budget +
                ", departureDate=" + departureDate +
                ", returnDate=" + returnDate +
                ", departureAirportCode='" + departureAirportCode + '\'' +
                ", destinationAirportCode='" + destinationAirportCode + '\'' +
                ", seatingClass='" + seatingClass + '\'' +
                ", emergencyRowSeating=" + emergencyRowSeating +
                ", adultPassengerCount=" + adultPassengerCount +
                ", childPassengerCount=" + childPassengerCount +
                ", infantPassengerCount=" + infantPassengerCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightSearch that)) return false;
        return Double.compare(that.budget, budget) == 0 &&
                emergencyRowSeating == that.emergencyRowSeating &&
                adultPassengerCount == that.adultPassengerCount &&
                childPassengerCount == that.childPassengerCount &&
                infantPassengerCount == that.infantPassengerCount &&
                Objects.equals(passengerName, that.passengerName) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(departureDate, that.departureDate) &&
                Objects.equals(returnDate, that.returnDate) &&
                Objects.equals(departureAirportCode, that.departureAirportCode) &&
                Objects.equals(destinationAirportCode, that.destinationAirportCode) &&
                Objects.equals(seatingClass, that.seatingClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerName, destination, budget, departureDate, returnDate,
                departureAirportCode, destinationAirportCode, seatingClass,
                emergencyRowSeating, adultPassengerCount, childPassengerCount, infantPassengerCount);
    }
}
