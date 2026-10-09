package Search;

/**
 * Represents a state in the high-level Route A* search).
 * 
 * Instead of grid coordinates, this state is defined by:
 * - currentLocation: index of the current point in CostTable (0-23 for gyms, 24
 * for start, 25 for U)
 * - visitedGyms: bitmask representing which of the 24 gyms have been visited
 * (bit i is 1 if gym i is visited)
 */
public class RouteState {

    // Constants for decoding
    private static final int MASK_SIZE = 1 << CostTable.NUM_GYMS; // 2^24 = 16777216
    private static final int FULL_MASK = MASK_SIZE - 1; // 24 ones

    /**
     * Encodes a location and a visited mask into a single 32-bit integer.
     * 
     * @param location Index of the current point (0-25)
     * @param mask     Bitmask of visited gyms (0 to 2^24 - 1)
     * @return encoded state
     */
    public static int encode(int location, int mask) {
        return (location * MASK_SIZE) + mask;
    }

    /** Extracts the location (0-25) from an encoded state. */
    public static int decodeLocation(int encodedState) {
        return encodedState / MASK_SIZE;
    }

    /** Extracts the visited mask (0 to 2^24 - 1) from an encoded state. */
    public static int decodeMask(int encodedState) {
        return encodedState % MASK_SIZE;
    }

    public static boolean allGymsVisited(int mask) {
        return (mask & FULL_MASK) == FULL_MASK;
    }

    public static boolean isGymVisited(int mask, int gymIndex) {
        return (mask & (1 << gymIndex)) != 0;
    }

    /** Returns a new mask with the specific gym added. */
    public static int visitGym(int mask, int gymIndex) {
        return mask | (1 << gymIndex);
    }
}
