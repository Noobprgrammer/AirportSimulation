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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ATC {
    private final int NUM_GATES = 3;
    private final Gate[] gates = new Gate[NUM_GATES];
    final LinkedList<Plane> landingQueue = new LinkedList<>();
    final Object landingQueueLock = new Object();
    private final Object gateAssignmentLock = new Object();
    private final Object coastingLock = new Object();
    private boolean isCoasting = false;

    private final AtomicReference<Double> maxWaitTime;
    private final AtomicReference<Double> totalWaitTime;
    private final AtomicReference<Double> minWaitTime;
    private final AtomicInteger planesServed;
    private final AtomicInteger passengersBoarded;

    public ATC(AtomicReference<Double> maxWaitTime, AtomicReference<Double> totalWaitTime, AtomicReference<Double> minWaitTime, AtomicInteger planesServed, AtomicInteger passengersBoarded) {
        this.maxWaitTime = maxWaitTime;
        this.totalWaitTime = totalWaitTime;
        this.minWaitTime = minWaitTime;
        this.planesServed = planesServed;
        this.passengersBoarded = passengersBoarded;
        
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
        maxWaitTime.updateAndGet(current -> Math.max(current, waitTime));
        
        minWaitTime.updateAndGet(current -> Math.min(current, waitTime));
        
        totalWaitTime.updateAndGet(current -> current + waitTime);
        
        planesServed.incrementAndGet();
    }

    public void addPassengersBoarded(int numPassengers) {
        passengersBoarded.addAndGet(numPassengers);
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
