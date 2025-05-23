/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airportsimulation;

/**
 *
 * @author GoatKy1e
 */
import java.util.*;

public class ATC {
    private final int NUM_GATES = 3;
    private final Gate[] gates = new Gate[NUM_GATES];
    final LinkedList<Plane> landingQueue = new LinkedList<>();
    final Object landingQueueLock = new Object();
    private final Object gateAssignmentLock = new Object();
    private final Object coastingLock = new Object();
    private boolean isCoasting = false;

    private double maxWaitTime = 0;
    private double totalWaitTime = 0;
    private double minWaitTime = Double.MAX_VALUE;
    private int planesServed = 0;
    private int passengersBoarded = 0;

    public ATC() {
        for (int i = 0; i < NUM_GATES; i++) {
            gates[i] = new Gate(i + 1);
        }
    }

    public void requestLanding(Plane plane, boolean isEmergency) {
        synchronized (landingQueueLock) {
            if (isEmergency) {
                landingQueue.addFirst(plane);
                log("Plane " + plane.getId() + " (Emergency): Requesting landing.");
            } else {
                landingQueue.addLast(plane);
                log("Plane " + plane.getId() + ": Requesting landing.");
            }
            landingQueueLock.notifyAll();
        }
    }

    public Gate getAvailableGate() {
        synchronized (gateAssignmentLock) {
            for (Gate gate : gates) {
                if (gate.isAvailable()) {
                    return gate;
                }
            }
            return null;
        }
    }

    public Plane getNextPlaneToLand() throws InterruptedException {
        synchronized (landingQueueLock) {
            while (landingQueue.isEmpty()) {
                landingQueueLock.wait();
            }
            return landingQueue.pollFirst();
        }
    }

    public boolean canStartCoasting() {
        synchronized (coastingLock) {
            return !isCoasting;
        }
    }

    public void startCoasting() {
        synchronized (coastingLock) {
            isCoasting = true;
        }
    }

    public void finishCoasting() {
        synchronized (coastingLock) {
            isCoasting = false;
            coastingLock.notifyAll();
        }
    }

    public void waitForCoastingAvailable() throws InterruptedException {
        synchronized (coastingLock) {
            while (isCoasting) {
                coastingLock.wait();
            }
        }
    }

    public void grantLandingPermission(Plane plane, Runway runway, Gate gate) {
        runway.acquire();
        gate.acquire();
        log("ATC: Plane " + plane.getId() + ", you are cleared to land. Proceed to Runway. Gate assigned: " + gate.getId());
        plane.setAssignedGate(gate);
    }

    public void grantTakeoffPermission(Plane plane, Runway runway) {
        runway.acquire();
        log("ATC: Plane " + plane.getId() + ", you are cleared for takeoff.");
    }

    public void releaseRunway(Runway runway) {
        runway.release();
    }

    public void releaseGate(Gate gate) {
        gate.release();
    }

    public void updateWaitTimes(double waitTime) {
        synchronized (this) {
            maxWaitTime = Math.max(maxWaitTime, waitTime);
            minWaitTime = Math.min(minWaitTime, waitTime);
            totalWaitTime += waitTime;
            planesServed++;
        }
    }

    public void addPassengersBoarded(int numPassengers) {
        synchronized (this) {
            passengersBoarded += numPassengers;
        }
    }

    public void printStatistics() {
        log("Checking All Gates: All Empty");
        double avgWaitTime = (planesServed > 0) ? totalWaitTime / planesServed : 0;
        System.out.println("\n--- ATC Statistics ---");
        System.out.println("Max Wait Time: " + String.format("%.2f", maxWaitTime));
        System.out.println("Avg Wait Time: " + String.format("%.2f", avgWaitTime));
        System.out.println("Min Wait Time: " + (minWaitTime == Double.MAX_VALUE ? "0.00" : String.format("%.2f", minWaitTime)));
        System.out.println("Planes Served: " + planesServed);
        System.out.println("Passengers Boarded: " + passengersBoarded);
    }

    public boolean checkGatesEmpty() {
        synchronized (gateAssignmentLock) {
            for (Gate gate : gates) {
                if (!gate.isAvailable()) {
                    return false;
                }
            }
            return true;
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
