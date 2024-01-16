package com.java;



import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class atcSim {
    ArrayDeque<Flight> arrivalQueue = new ArrayDeque<>();
    ArrayDeque<Flight> departureQueue = new ArrayDeque<>();
    ArrayList<Flight> arrivalStatistics = new ArrayList<>();
    ArrayList<Flight> departureStatistics = new ArrayList<>();


    enum FlightType {Arrival, Departure}

    static final int MIN_FLIGHT_SPACING = 10;
    int timeInterval;
    int fltSpacingCounter;
    int timerCounter;
    int numberOfDivertedArrivals;
    int numberOfDeniedDepartures;
    int numberOfArrivals;
    int numberOfDepartures;
    int flightNumberCounter;
    int arrivalQueueEmpty;
    int departureQueueEmpty;

    class Flight {
        String flightNumber;
        FlightType flightType;
        int minuteInQueue;
        int minuteOutQueue;


        // constructor
        public Flight(String flightNumber, FlightType flightType) {
            this.flightNumber = flightNumber;
            this.flightType = flightType;
        }

        public String toString() {
            return flightType + ": " + flightNumber;
        }

        //  "minute" that flight entered the queue
        public void setMinuteInQueue(int minute) {
            this.minuteInQueue = minute;
        }

        // "minute" that flight exits the queue
        // difference is time in queue
        public void setMinuteOutQueue(int minute) {
            this.minuteOutQueue = minute;
        }

        public int timeInQueue() {
            return minuteOutQueue - minuteInQueue;
        }
    }


    Random random = new Random(System.nanoTime());

    public int getPoissonRandom(double mean) {
        double L = Math.exp(-mean);
        int x = 0;
        double p = 1.0;
        do {
            p = p * random.nextDouble();
            x++;
        } while (p > L);
        return x - 1;
    }

    public void processDeparture(double meanDepartureFreq) {
        int count = 0;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanDepartureFreq)) > 0)
            addToDepartureQueue(count);
        if (timerCounter >= 10) {
            if (arrivalQueue.size() == 0) {
                removeFromDepartureQueue();
                timerCounter = 0;
            }
        }
    }

    private void processArrival(double meanArrivalFreq) {
        int count = 0;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanArrivalFreq)) > 0)
            addToArrivalQueue(count);
        if (timerCounter >= 10) {
            if (arrivalQueue.size() > 0) {
                removeFromArrivalQueue();
                timerCounter = 0;
            }
        }
    }

    public void addToArrivalQueue(int count) {
        for (int i = 0; i < count; i++) {
            Flight arrivalFlight = new Flight("AA" + flightNumberCounter++, FlightType.Arrival);
            if (arrivalQueue.size() < 5) {
                arrivalFlight.setMinuteInQueue(timeInterval);
                arrivalQueue.add(arrivalFlight);

            } else {
                this.numberOfDivertedArrivals++;
                System.out.println("Arrival queue full. Flight " + arrivalFlight + " rerouted at: " + timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60) + " hours");
            }
        }
    }

    public void addToDepartureQueue(int count) {
        for (int i = 0; i < count; i++) {
            Flight departureFlight = new Flight("AA" + flightNumberCounter++, FlightType.Departure);
            if (departureQueue.size() < 5) {
                departureFlight.setMinuteInQueue(timeInterval);
                departureQueue.add(departureFlight);

            } else {
                this.numberOfDeniedDepartures++;
                System.out.println("Departure queue full. Flight " + departureFlight + " rerouted at: " + timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60) + " hours");
            }

        }
    }

    public void removeFromArrivalQueue() {
        if (arrivalQueue.size() > 0) {
            Flight arrivalFlight = arrivalQueue.removeFirst();
            arrivalFlight.setMinuteOutQueue(timeInterval);
            arrivalStatistics.add(arrivalFlight);
            System.out.println("Flight " + arrivalFlight + " arrived at: " + +timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60) + " hours");
            numberOfArrivals++;
        }
    }

    public void removeFromDepartureQueue() {
        if (departureQueue.size() > 0) {
            Flight departureFlight = departureQueue.removeFirst();
            departureFlight.setMinuteOutQueue(timeInterval);
            departureStatistics.add(departureFlight);
            System.out.println("Flight " + departureFlight + " arrived at: " + +timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60) + " hours");
            numberOfDepartures++;
        }
    }

    public void printSimSummaryStatistics() {
        int emptyQueueInstances = arrivalQueueEmpty + departureQueueEmpty;
        double percentIdle = (double) emptyQueueInstances/timeInterval * 100.0;
        int ttlDepartureTimeInQueue = 0;
        int avgDepartureQueueTime = 0;
        int ttlArrivalTimeInQueue = 0;
        int avgArrivalQueueTime = 0;

        for (Flight flt: departureStatistics)
        {
            ttlDepartureTimeInQueue = ttlDepartureTimeInQueue + flt.timeInQueue();
        }
        if (departureStatistics.size() > 0)
            avgDepartureQueueTime = ttlDepartureTimeInQueue / departureStatistics.size();

        for (Flight flt: arrivalStatistics)
        {
            ttlArrivalTimeInQueue = ttlArrivalTimeInQueue + flt.timeInQueue();
        }
        if (arrivalStatistics.size() > 0)
            avgArrivalQueueTime = ttlArrivalTimeInQueue / arrivalStatistics.size();

        System.out.println("*****************************");
        System.out.println("SUMMARY STATISTICS");
        System.out.println("Time simulated: " + timeInterval / 60 + ":" + String.format("%02d", timeInterval % 60) + " hours");
        System.out.println("Number of Arrivals: " + numberOfArrivals);
        System.out.println("Number of Departures: " + numberOfDepartures);
        System.out.println("Total Flights Handled: " + flightNumberCounter);
        System.out.println("Average Arrivals Per Hour: " + numberOfArrivals/24);
        System.out.println("Average Departures Per Hour: " + numberOfDepartures/24);
        System.out.println("Arrivals in Queue: " + arrivalQueue.size());
        System.out.println("Departures in Queue: " + departureQueue.size());
        System.out.println("Number of Diverted Arrivals: " + numberOfDivertedArrivals);
        System.out.println("Number of Denied Departures: " + numberOfDeniedDepartures);
        System.out.println("Percent Time Idle Runway: " + String.format("%.2f",percentIdle) + "%");
        System.out.println("Average Arrival Queue Time: " + avgArrivalQueueTime + " minutes");
        System.out.println("Average Departure Queue Time: " + avgDepartureQueueTime + " minutes");
    }


        public void doSim () {
        double meanArrivalFreq = 0.0;
        double meanDepartureFreq = 0.0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter mean departure frequency (0.0 > df < 1.0): ");
        if (scanner.hasNextDouble())
            meanDepartureFreq = scanner.nextDouble();
        System.out.println("Enter mean arrival frequency 0.0 > af < 1.0): ");
        if (scanner.hasNextDouble())
            meanArrivalFreq = scanner.nextDouble();
        // Check if total probability of arrivals + departures > 100%
        if (meanDepartureFreq + meanArrivalFreq > 1.0) {
            System.out.println("Mean departure frequency plus mean arrival frequency cannot exceed 100%. Try again...");
            return;
        }
        for (int i = 0; i < 720; i++) {
            processArrival(meanArrivalFreq);
            processDeparture(meanDepartureFreq);
            if (arrivalQueue.size() == 0) {
                arrivalQueueEmpty++;
            }

            if (departureQueue.size() == 0) {
                departureQueueEmpty++;
            }

        }
        printSimSummaryStatistics();
    }
    public static void main(String[] args) {

        atcSim sim = new atcSim();
        sim.doSim();

    }
}












