package org.example;

import flight.FlightSearch;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        FlightSearch fs = new FlightSearch("Demo Passenger", "Paris", 750)
                .setDepartureAirportCode("JFK")
                .setDestinationAirportCode("CDG")
                .setDepartureDate(LocalDate.now().plusDays(14))
                .setReturnDate(LocalDate.now().plusDays(20))
                .setAdultPassengerCount(1)
                .setSeatingClass("Economy");

        System.out.println("Search: " + fs);
        System.out.println("Valid? " + fs.validateSearch());
    }
}
