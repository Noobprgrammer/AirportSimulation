/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package airportsimulation;

/**
 *
 * @author GoatKy1e
 */
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
public class AirportSimulation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("-- APU Airport --\n");

        // Atomic variables for statistics
        AtomicReference<Double> maxWaitTime = new AtomicReference<>(0.0);
        AtomicReference<Double> totalWaitTime = new AtomicReference<>(0.0);
        AtomicReference<Double> minWaitTime = new AtomicReference<>(Double.MAX_VALUE);
        AtomicInteger planesServed = new AtomicInteger(0);
        AtomicInteger passengersBoarded = new AtomicInteger(0);

        ATC atc = new ATC(maxWaitTime, totalWaitTime, minWaitTime, planesServed, passengersBoarded);
        Runway runway = new Runway();
        RefuelingTruck refuelingTruck = new RefuelingTruck();
        List<Thread> planeThreads = new ArrayList<>();
        Random rand = new Random();

        // Create 6 planes, with plane 5 being an emergency
        for (int i = 1; i <= 6; i++) {
            boolean isEmergency = (i == 5);
            Plane plane = new Plane(i, atc, runway, refuelingTruck, isEmergency);
            Thread thread = new Thread(plane);
            planeThreads.add(thread);
            thread.start();
            
            try {
                Thread.sleep(rand.nextInt(2000)); // Simulate staggered arrival
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Wait for all plane threads to finish
        for (Thread thread : planeThreads) {
            try {
                thread.join(120000); // 120 seconds timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Wait for all gates to be empty before printing statistics
        while (!atc.checkGatesEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Print statistics from main method
        printStatistics(maxWaitTime, totalWaitTime, minWaitTime, planesServed, passengersBoarded);
    }

    private static void printStatistics(AtomicReference<Double> maxWaitTime, AtomicReference<Double> totalWaitTime, AtomicReference<Double> minWaitTime, AtomicInteger planesServed, AtomicInteger passengersBoarded) {
        System.out.println("Checking All Gates: All Empty");
        double avgWaitTime = (double)totalWaitTime.get() / planesServed.get();
        System.out.println("\n--- ATC Statistics ---");
        System.out.println("Max Wait Time: " + String.format("%.2f", maxWaitTime.get()));
        System.out.println("Avg Wait Time: " + String.format("%.2f", avgWaitTime));
        System.out.println("Min Wait Time: " + (minWaitTime.get() == Double.MAX_VALUE ? "0.00" : String.format("%.2f", minWaitTime.get())));
        System.out.println("Planes Served: " + planesServed.get());
        System.out.println("Passengers Boarded: " + passengersBoarded.get());
    }
}
    
