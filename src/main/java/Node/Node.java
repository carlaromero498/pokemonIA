package Node;

import Estado.Estado;

public class Node implements Comparable<Node> {
    private int g; //o que gastei desde a partida
    private int h; //estimativa até destino
    private Node pai;
    private Estado estado;

    public Node(int g, int h, Node pai, Estado estado){
        this.estado = estado;
        this.g = g;
        this.h = h;
        this.pai =  pai;
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

    public Node getPai(){ 
        return pai;
    }

    public Estado getEstado(){
        return estado;
    }

    @Override 
    public int compareTo(Node outro){
        int c = Integer.compare(this.getF(), outro.getF()); 
        if (c != 0) return c;
        
        // Se o f for igual, desempata a favor daquele com menor heurística (h)
        return Integer.compare(this.getH(), outro.getH());
        // devolve um número negativo se this deve sair antes de outro, zero se são equivalentes e positivo se deve sair depois
        // integer.compare e não subtração pq pode causar overflow com valores grandes.
    }
}
