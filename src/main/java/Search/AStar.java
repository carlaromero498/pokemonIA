package Search;

import Estado.Estado;
import Node.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

public class AStar {

    private final char[][] mapa;
    private int numExpandidos; // nós expandidos na última pesquisa

    public AStar(char[][] mapa) {
        this.mapa = mapa;
    }

    // custo mínimo possível de um passo (usado para garantir que a heurística é admissível)
    private static final int CUSTO_MINIMO_PASSO = 1;

    public static int manhattan(Estado atual, Estado objetivo) {
        int deltaLinha = Math.abs(atual.line() - objetivo.line());
        int deltaColuna = Math.abs(atual.column() - objetivo.column());
        return (deltaLinha + deltaColuna) * CUSTO_MINIMO_PASSO;
    }

    public static int custoCelula(char c) {
        switch (c) {
            case 'M': return 200;
            case 'A': return 30;
            case 'F': return 15;
            case 'R': return 5;
            default:  return 1;
        }
    }

    public List<Estado> vizinhosValidos(Estado atual) {
        List<Estado> vizinhos = new ArrayList<>();

        int linha = atual.line();
        int coluna = atual.column();

        int[][] deslocamentos = { {0, 1}, {0, -1}, {1, 0}, {-1, 0} }; // direita, esquerda, baixo, cima

        for (int[] d : deslocamentos) {
            int nLinha = linha + d[0];
            int nColuna = coluna + d[1];

            if (nLinha >= 0 && nLinha < mapa.length && nColuna >= 0 && nColuna < mapa[0].length) {
                vizinhos.add(new Estado(nLinha, nColuna));
            }
        }

        return vizinhos;
    }

    public Node procurar(Estado inicio, Estado objetivo) {
        return procurar(inicio, objetivo, true);
    }

    public Node procurar(Estado inicio, Estado objetivo, boolean usarHeuristica) {
        PriorityQueue<Node> fronteira = new PriorityQueue<>();
        Set<Estado> expandidos = new HashSet<>();

        // 1. nó inicial: g=custo da célula de partida (paga-se ao entrar), h=manhattan ou 0, pai=null
        int gInicial = custoCelula(mapa[inicio.line()][inicio.column()]);
        int hInicial = usarHeuristica ? manhattan(inicio, objetivo) : 0;
        Node noInicial = new Node(gInicial, hInicial, null, inicio);
        fronteira.add(noInicial);

        // 2. enquanto a fronteira não estiver vazia
        while (!fronteira.isEmpty()) {

            // 3. tira o nó com menor f
            Node atual = fronteira.poll();
            Estado estadoAtual = atual.getEstado();

            // 4. se o estado já foi expandido -> descarta
            if (expandidos.contains(estadoAtual)) {
                continue;
            }

            // 5. se é o objetivo -> devolve o nó
            // Nota: o objetivo é devolvido antes de ser adicionado a expandidos,
            // logo não é contado em numExpandidos.
            if (estadoAtual.equals(objetivo)) {
                this.numExpandidos = expandidos.size();
                return atual;
            }

            // 6. marca o estado como expandido
            expandidos.add(estadoAtual);

            // 7. para cada vizinho válido: cria um Node e mete na fronteira
            for (Estado vizinho : vizinhosValidos(estadoAtual)) {
                if (!expandidos.contains(vizinho)) {
                    int novoG = atual.getG() + custoCelula(mapa[vizinho.line()][vizinho.column()]);
                    int novoH = usarHeuristica ? manhattan(vizinho, objetivo) : 0;
                    Node novoNo = new Node(novoG, novoH, atual, vizinho);
                    fronteira.add(novoNo);
                }
            }
        }

        this.numExpandidos = expandidos.size();
        return null; // sem caminho
    }

    public int getNumExpandidos() {
        return numExpandidos;
    }
}
