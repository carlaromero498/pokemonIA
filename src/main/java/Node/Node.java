package Node;

import Estado.Estado;

public class Node implements Comparable<Node> {
    private int g; // cost from start to this node
    private int h; // heuristic estimate to goal
    private Node parent;
    private Estado state;

    public Node(int g, int h, Node parent, Estado state){
        this.state = state;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public int getF(){
        return g + h;
    }

    public int getG(){
        return g;
    }

    public int getH(){ 
        return h;
    }

    public Node getParent(){ 
        return parent;
    }

    public Estado getState(){
        return state;
    }

    @Override 
    public int compareTo(Node other){
        int c = Integer.compare(this.getF(), other.getF()); 
        if (c != 0) return c;
        
        // Tie-break: prefer the node with lower h (closer to goal)
        return Integer.compare(this.getH(), other.getH());
        // Integer.compare avoids overflow that subtraction could cause with large values
    }
}
