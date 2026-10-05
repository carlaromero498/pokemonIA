package Search;

import java.util.Objects;

/**
 * Represents a state in the high-level Route A* search (TSP).
 * 
 * Instead of grid coordinates, this state is defined by:
 * - currentLocation: index of the current point in CostTable (0-23 for gyms, 24 for start, 25 for U)
 * - visitedGyms: bitmask representing which of the 24 gyms have been visited (bit i is 1 if gym i is visited)
 */
public class RouteState {
    
    private final int currentLocation;
    private final int visitedGyms; // 32-bit integer is enough for 24 gyms

    public RouteState(int currentLocation, int visitedGyms) {
        this.currentLocation = currentLocation;
        this.visitedGyms = visitedGyms;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public int getVisitedGyms() {
        return visitedGyms;
    }

    /** Returns true if all 24 gyms have been visited. */
    public boolean allGymsVisited() {
        // (1 << 24) - 1 creates a mask with 24 ones: 00000000111111111111111111111111
        int targetMask = (1 << CostTable.NUM_GYMS) - 1;
        return (visitedGyms & targetMask) == targetMask;
    }

    /** Returns true if a specific gym (index 0-23) has been visited. */
    public boolean isGymVisited(int gymIndex) {
        return (visitedGyms & (1 << gymIndex)) != 0;
    }

    /** 
     * Creates a new RouteState visiting a new gym. 
     * Returns a new instance (states are immutable).
     */
    public RouteState visitGym(int gymIndex) {
        int newVisited = visitedGyms | (1 << gymIndex);
        return new RouteState(gymIndex, newVisited);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteState)) return false;
        RouteState that = (RouteState) o;
        return currentLocation == that.currentLocation && visitedGyms == that.visitedGyms;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentLocation, visitedGyms);
    }
    
    @Override
    public String toString() {
        return "RouteState{loc=" + currentLocation + ", visited=" + Integer.toBinaryString(visitedGyms) + "}";
    }
}
