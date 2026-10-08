package Search;

public class RouteNode implements Comparable<RouteNode> {
    
    private final int encodedState;
    private final int g;
    private final int f;
    private final RouteNode parent;

    public RouteNode(int encodedState, int g, int f, RouteNode parent) {
        this.encodedState = encodedState;
        this.g = g;
        this.f = f;
        this.parent = parent;
    }

    public int getEncodedState() {
        return encodedState;
    }

    public int getG() {
        return g;
    }

    public int getF() {
        return f;
    }

    public RouteNode getParent() {
        return parent;
    }

    @Override
    public int compareTo(RouteNode other) {
        // Compare by f (total estimated cost)
        int c = Integer.compare(this.f, other.f);
        if (c != 0) return c;
        
        // Tie-break: prefer the node that has visited more gyms (higher mask value)
        // This acts as a proxy for "closer to the goal" since we don't store 'h' directly
        int thisMask = RouteState.decodeMask(this.encodedState);
        int otherMask = RouteState.decodeMask(other.encodedState);
        return Integer.compare(otherMask, thisMask); // Descending order (higher mask first)
    }
}
