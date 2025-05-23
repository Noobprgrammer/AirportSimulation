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

public class Plane implements Runnable{
    private final int id;
    private final ATC atc;
    private final Runway runway;
    private final RefuelingTruck refuelingTruck;
    private Gate assignedGate;
    private final boolean isEmergency;
    private final Random random = new Random();

    static final double LANDING_TIME = 1;
    static final double COASTING_TIME = 1;
    static final double DOCKING_TIME = 1;
    static final double DISEMBARK_TIME = 5;
    static final double REFILL_TIME = 3;
    static final double REFUEL_TIME = 7;
    static final double EMBARK_TIME = 5;
    static final double UNDOCKING_TIME = 1;
    static final double COAST_TO_RUNWAY_TIME = 1;
    static final double TAKEOFF_TIME = 1;
    static final int PLANE_CAPACITY = 50;

    public Plane(int id, ATC atc, Runway runway, RefuelingTruck refuelingTruck, boolean isEmergency) {
        this.id = id;
        this.atc = atc;
        this.runway = runway;
        this.refuelingTruck = refuelingTruck;
        this.isEmergency = isEmergency;
    }

    public int getId() {
        return id;
    }

    public void setAssignedGate(Gate gate) {
        this.assignedGate = gate;
        if (gate != null) {
            gate.occupy(this);
        }
    }

    public Gate getAssignedGate() {
        return assignedGate;
    }

    @Override
    public void run() {
        try {
            // Only request landing once at the beginning
            atc.requestLanding(this, isEmergency);
            land();
            coastToGate();
            gateOperations();
            coastToRunway();
            takeoff();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void land() throws InterruptedException {
        double waitStartTime = System.currentTimeMillis() / 1000.0;
        
        while (true) {
            Gate gate = atc.getAvailableGate();

            // If runway is free and there's an available gate and we can start coasting
            if (runway.isFree() && gate != null && atc.canStartCoasting()) {
                // Check if we're next in the queue
                synchronized (atc.landingQueueLock) {
                    if (!atc.landingQueue.isEmpty() && atc.landingQueue.peekFirst() == this) {
                        // Remove ourselves from the queue and proceed
                        atc.landingQueue.pollFirst();
                        atc.grantLandingPermission(this, runway, gate);
                        double waitTime = System.currentTimeMillis() / 1000.0 - waitStartTime;
                        atc.updateWaitTimes(waitTime);
                        break;
                    }
                }
            }
            Thread.sleep(100);
        }

        log("Plane " + id + ": Landing.");
        delay(LANDING_TIME);
        log("Plane " + id + ": Landed. Requesting taxi to Gate.");
    }

    private void coastToGate() throws InterruptedException {
        atc.startCoasting();
        log("Plane " + id + ": Taxiing to Gate " + assignedGate.getId() + ".");
        log("Plane " + id + ": Coasting to Gate " + assignedGate.getId() + ".");
        delay(COASTING_TIME);
        log("Plane " + id + ": Arrived at Gate " + assignedGate.getId() + ". Starting docking.");
        atc.finishCoasting();
        atc.releaseRunway(runway);
        delay(DOCKING_TIME);
        log("Plane " + id + ": Docked at Gate " + assignedGate.getId() + ".");
    }

    private void gateOperations() throws InterruptedException {
        // Generate random passenger numbers
        int passengersDisembarking = random.nextInt(PLANE_CAPACITY) + 1; // 1 to 50
        int passengersEmbarking = random.nextInt(PLANE_CAPACITY) + 1;    // 1 to 50
        
        // Process 1: Disembark -> Restock -> Embark
        Thread passengerOpsThread = new Thread(() -> {
            try {
                log("Plane " + id + ": Passenger disembark started (" + passengersDisembarking + " passengers).");
                delay(DISEMBARK_TIME);
                log("Plane " + id + ": Passenger disembark finished (" + passengersDisembarking + " passengers).");

                log("Plane " + id + ": Stock refilling started.");
                delay(REFILL_TIME);
                log("Plane " + id + ": Stock refilling finished.");

                log("Plane " + id + ": New passenger embark started (" + passengersEmbarking + " passengers).");
                delay(EMBARK_TIME);
                log("Plane " + id + ": New passenger embark finished (" + passengersEmbarking + " passengers).");
                atc.addPassengersBoarded(passengersEmbarking);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Process 2: Refueling
        Thread refuelThread = new Thread(() -> {
            try {
                refuelingTruck.acquire();
                log("Plane " + id + ": Refueling started.");
                delay(REFUEL_TIME);
                log("Plane " + id + ": Refueling finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                refuelingTruck.release();
            }
        });

        passengerOpsThread.start();
        refuelThread.start();

        passengerOpsThread.join();
        refuelThread.join();
    }

    private void coastToRunway() throws InterruptedException {
        atc.waitForCoastingAvailable();
        
        atc.startCoasting();
        log("Plane " + id + ": Starting undocking from Gate " + assignedGate.getId() + ".");
        delay(UNDOCKING_TIME);
        log("Plane " + id + ": Finished undocking from Gate " + assignedGate.getId() + ".");

        log("Plane " + id + ": Coasting to runway.");
        delay(COAST_TO_RUNWAY_TIME);
        log("Plane " + id + ": Arrived at runway. Requesting takeoff.");
        atc.finishCoasting();
    }

    private void takeoff() throws InterruptedException {
        atc.grantTakeoffPermission(this, runway);
        log("Plane " + id + ": Taking off.");
        delay(TAKEOFF_TIME);
        log("Plane " + id + ": Takeoff complete.");
        atc.releaseRunway(runway);
        atc.releaseGate(assignedGate);
    }

    private void delay(double seconds) throws InterruptedException {
        Thread.sleep((int) (seconds * 1000));
    }

    private void log(String message) {
        System.out.println(message);
    }
}
