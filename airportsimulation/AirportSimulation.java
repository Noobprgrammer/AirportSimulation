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
public class AirportSimulation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("-- APU Airport --\n");

        ATC atc = new ATC();
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
        
        atc.printStatistics();
    }
}
    
