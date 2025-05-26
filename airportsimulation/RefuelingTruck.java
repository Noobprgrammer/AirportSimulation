/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airportsimulation;

/**
 *
 * @author GoatKy1e
 */
import java.util.concurrent.locks.*;

public class RefuelingTruck {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();
    private boolean isAvailable = true;

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (!isAvailable) {
                available.await();  // wait until it's available
            }
            isAvailable = false;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            isAvailable = true;
            available.signal();  // wake up one waiting thread
        } finally {
            lock.unlock();
        }
    }
}
