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
public class Runway {
    private final ReentrantLock lock = new ReentrantLock();

    public void acquire() {
        lock.lock();  // blocks until the lock is acquired
    }

    public void release() {
        lock.unlock();  // releases the lock
    }

    public boolean isFree() {
        return !lock.isLocked();
    }
}
