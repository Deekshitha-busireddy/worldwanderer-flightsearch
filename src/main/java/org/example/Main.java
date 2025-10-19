// src/main/java/org/example/Main.java
package org.example;

import flight.FlightSearch;

public class Main {
    public static void main(String[] args) {
        FlightSearch fs = new FlightSearch();

        boolean ok = fs.runFlightSearch(
                "01/12/2025", // departure (DD/MM/YYYY)
                "mel",        // from
                false,        // emergency row?
                "15/12/2025", // return
                "pvg",        // to
                "economy",    // class
                2,            // adults
                2,            // children
                0             // infants
        );

        System.out.println("Valid? " + ok);
        System.out.println(fs);
    }
}
