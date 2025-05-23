/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airportsimulation;

/**
 *
 * @author GoatKy1e
 */
public class Runway {
    private boolean isFree = true;

    public synchronized void acquire() {
        while (!isFree) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        isFree = false;
    }

    public synchronized void release() {
        isFree = true;
        notifyAll();
    }

    public synchronized boolean isFree() {
        return isFree;
    }
}
