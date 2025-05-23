# Source Package file for NetBeans #

Create project in NetBeans and name it airportsimpulation
clone repo to package
run AirportSimulation


## Overview of the project ##
This simulation recreates the complex orchestration of an airport's daily operations, where multiple aircraft compete for limited resources while following strict safety protocols and operational procedures. The system demonstrates advanced concurrent programming concepts through realistic airport scenarios.
System Architecture
Core Components

### 🛩️ Aircraft (Planes) ###

Each plane operates as an independent thread with unique flight operations
Supports emergency priority handling for critical situations
Manages random passenger loads (1-50 passengers) for realistic capacity simulation
Tracks individual flight statistics and wait times

### 🏗️ Air Traffic Control (ATC)###

Central coordination hub managing all airport operations
Implements priority queuing system (emergency flights get precedence)
Monitors resource allocation and tracks comprehensive statistics
Ensures safe separation and sequencing of aircraft operations

### 🛬 Runway System ###

Single shared runway resource with mutex-based access control
Handles both landing and takeoff operations safely
Prevents conflicts through synchronized resource management

### 🚪 Gate Management ###

Three available gates with individual occupancy tracking
Dynamic gate assignment based on availability
Supports concurrent passenger and maintenance operations

### ⛽ Ground Services ###

Single refueling truck shared among all aircraft
Concurrent refueling operations alongside passenger services
Resource contention management for realistic ground delays

## Operational Flow ##
1. Arrival Phase

Aircraft request landing clearance from ATC
Emergency aircraft receive priority queue positioning
System checks for runway availability and gate assignment
Landing clearance granted when resources are available

2. Ground Operations (parallel)
- Disembar + Restock + New Boarding = 13 sec
- Refueling = 7 sec

3. Departure Phase

Aircraft request takeoff clearance
Runway availability verification
Safe taxi operations to runway
Takeoff execution and resource release

## Key Features ##

### 🚨 Emergency Handling ###

Priority queue system for emergency aircraft
Immediate resource allocation when available
Realistic emergency response protocols

### 📊 Real-time Statistics ###

Maximum, minimum, and average wait times
Total aircraft processed
Passenger throughput tracking
Comprehensive operational metrics

### 🎲 Realistic Variability ###

Random passenger counts (1-50 per flight)
Variable aircraft arrival intervals (0-2 seconds)
Unpredictable operational scenarios

### 🔄 Concurrent Operations ###

Parallel passenger and maintenance operations
Multiple aircraft processing simultaneously
Thread-safe resource sharing

## Technical Implementation ##
Concurrency Patterns

Producer-Consumer: ATC landing queue management
Resource Pool: Shared runway and refueling truck
Monitor Pattern: Gate availability tracking
Priority Queue: Emergency aircraft handling

Synchronization Mechanisms

Synchronized methods for resource access
Wait/notify patterns for coordination
Mutex locks for critical sections
Thread-safe collections for queue management

Thread Safety

Deadlock prevention through consistent lock ordering
Interrupt handling for graceful shutdown
Resource cleanup and proper thread termination

## Sample Output ##
-- APU Airport --

Plane 1: Requesting landing.
ATC: Plane 1, you are cleared to land. Proceed to Runway. Gate assigned: 1
Plane 1: Landing.
Plane 2: Requesting landing.
Plane 5 (Emergency): Requesting landing.
Plane 1: Passenger disembark started (23 passengers).
Plane 1: Refueling started.
...

--- ATC Statistics ---
Max Wait Time: 15.23
Avg Wait Time: 8.45
Min Wait Time: 2.10
Planes Served: 6
Passengers Boarded: 267

## Prerequisites ##

Java 8 or higher
Basic understanding of multithreading concepts
