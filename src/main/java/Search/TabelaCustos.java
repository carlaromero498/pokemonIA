package Search;

import Estado.Estado;
import Node.Node;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pré-calcula as distâncias A* entre todos os 26 pontos importantes do mapa.
 *
 * Indexação (alinhada com o bitmask de ginásios visitados):
 *   Índices 0–23 → 24 ginásios (2,3,4,5,6,7,8,9,B,C,D,E,G,H,I,J,K,L,N,O,P,Q,S,T)
 *   Índice 24    → origem '1'
 *   Índice 25    → destino 'U'
 *
 * Alternativa 2: 325 corridas do A* (simetria), reutiliza código já testado.
 */
public class TabelaCustos {

    public static final int TOTAL_PONTOS = 26;
    public static final int NUM_GINASIOS = 24;
    public static final int INDICE_ORIGEM = 24;
    public static final int INDICE_DESTINO = 25;

    // Ordem dos ginásios conforme o enunciado: símbolo → índice 0–23
    private static final String ORDEM_GINASIOS = "23456789BCDEGHIJKLNOPQST";

    private final int[][] custos;          // custos[i][j] = distância A* do ponto i ao ponto j
    private final Estado[] posicoes;        // posicoes[i] = posição do ponto i no mapa
    private final Map<Character, Integer> simboloParaIndice;

    public TabelaCustos(char[][] mapa) {
        this.custos = new int[TOTAL_PONTOS][TOTAL_PONTOS];
        this.posicoes = new Estado[TOTAL_PONTOS];
        this.simboloParaIndice = construirMapeamento();

        encontrarPontos(mapa);
        preCalcular(mapa);
    }

    /** Constrói o mapeamento símbolo → índice. */
    private Map<Character, Integer> construirMapeamento() {
        Map<Character, Integer> mapa = new LinkedHashMap<>();
        for (int i = 0; i < ORDEM_GINASIOS.length(); i++) {
            mapa.put(ORDEM_GINASIOS.charAt(i), i);
        }
        mapa.put('1', INDICE_ORIGEM);
        mapa.put('U', INDICE_DESTINO);
        return mapa;
    }

    /** Percorre o mapa e guarda a posição de cada ponto importante. */
    private void encontrarPontos(char[][] mapa) {
        int encontrados = 0;
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                char c = mapa[i][j];
                if (!AStar.ehTerreno(c)) {
                    Integer indice = simboloParaIndice.get(c);
                    if (indice == null) {
                        throw new IllegalStateException(
                            "Carácter inesperado no mapa: '" + c + "' em (" + i + "," + j + ")");
                    }
                    posicoes[indice] = new Estado(i, j);
                    encontrados++;
                }
            }
        }
        if (encontrados != TOTAL_PONTOS) {
            throw new IllegalStateException(
                "Esperados " + TOTAL_PONTOS + " pontos importantes, encontrados " + encontrados);
        }
    }

    /** Pré-calcula custos entre todos os pares usando A* com simetria (325 corridas). */
    private void preCalcular(char[][] mapa) {
        AStar astar = new AStar(mapa);
        int corridas = 0;

        for (int i = 0; i < TOTAL_PONTOS; i++) {
            custos[i][i] = 0; // custo de um ponto até si próprio, sem contar a partida
            for (int j = i + 1; j < TOTAL_PONTOS; j++) {
                Node resultado = astar.procurar(posicoes[i], posicoes[j]);
                if (resultado == null) {
                    throw new IllegalStateException(
                        "Sem caminho entre ponto " + i + " e ponto " + j);
                }
                // Exclui o custo da célula de partida para evitar dupla contagem ao encadear etapas.
                // Ao montar uma rota, soma-se custo(origem) + d(origem,g1) + d(g1,g2) + ... + d(gk,dest).
                int custoSemPartida = resultado.getG()
                    - AStar.custoCelula(mapa[posicoes[i].line()][posicoes[i].column()]);
                custos[i][j] = custoSemPartida;
                custos[j][i] = custoSemPartida; // simetria (todos os pontos especiais custam 1)
                corridas++;
            }
        }
        System.out.println("Tabela de custos pré-calculada: " + corridas + " corridas A*.");
    }

    /** Devolve o custo entre dois pontos pelo seu índice. */
    public int getCusto(int de, int para) {
        return custos[de][para];
    }

    /** Devolve a posição de um ponto pelo seu índice. */
    public Estado getPosicao(int indice) {
        return posicoes[indice];
    }

    /** Devolve o índice de um símbolo (letra ou dígito). */
    public int getIndice(char simbolo) {
        return simboloParaIndice.get(simbolo);
    }

    /** Devolve o símbolo correspondente a um índice. */
    public char getSimbolo(int indice) {
        if (indice == INDICE_ORIGEM) return '1';
        if (indice == INDICE_DESTINO) return 'U';
        return ORDEM_GINASIOS.charAt(indice);
    }
}
