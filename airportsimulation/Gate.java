/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airportsimulation;

/**
 *
 * @author GoatKy1e
 */

public class Gate {
    
    private final int id;
    private boolean isAvailable = true;
    private Plane currentPlane = null;

    public Gate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized void acquire() {
        while (!isAvailable) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        isAvailable = false;
    }

    public synchronized void release() {
        isAvailable = true;
        currentPlane = null;
        notifyAll();
    }

    public synchronized boolean isAvailable() {
        return isAvailable;
    }

    public synchronized void occupy(Plane plane) {
        currentPlane = plane;
        isAvailable = false;
    }

    public synchronized Plane getCurrentPlane() {
        return currentPlane;
    }
    
}
