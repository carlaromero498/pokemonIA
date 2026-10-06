package Main;

import java.util.List;

import Estado.Estado;
import Map.MapLoader;
import Map.MapVisualizer;
import Search.TabelaCustos;

public class Main {
	public static void main(String[] args) {
		try {
			String path = "./kanto.txt";
			MapLoader loader = new MapLoader(path);
			char[][] mapa = loader.getMapa();
			System.out.println("Mapa carregado: " + loader.getRows() + "x" + loader.getCols());

			// valida TabelaCustos (procura pontos importantes)
			TabelaCustos tabela = new TabelaCustos(mapa);
			System.out.println("Tabela de custos pré-calculada com sucesso.");
			List<Estado> testPath = List.of(
			new Estado(0, 0),
			new Estado(0, 1),
			new Estado(0, 2),
			new Estado(1, 2)
);
			MapVisualizer.displayPath(mapa, testPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
