package Search;

import Gym.Gym;
import Battle.Battle;
import Pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HillClimbing
{

    private List<Gym> gyms;

    public HillClimbing()
    {
        gyms = new ArrayList<>();

        gyms.add(new Gym("2", 35));
        gyms.add(new Gym("3", 40));
        gyms.add(new Gym("4", 45));
        gyms.add(new Gym("5", 50));
        gyms.add(new Gym("6", 55));
        gyms.add(new Gym("7", 60));
        gyms.add(new Gym("8", 65));
        gyms.add(new Gym("9", 70));
        gyms.add(new Gym("B", 75));
        gyms.add(new Gym("C", 80));
        gyms.add(new Gym("D", 85));
        gyms.add(new Gym("E", 90));
        gyms.add(new Gym("G", 95));
        gyms.add(new Gym("H", 100));
        gyms.add(new Gym("I", 110));
        gyms.add(new Gym("J", 120));
        gyms.add(new Gym("K", 130));
        gyms.add(new Gym("L", 140));
        gyms.add(new Gym("N", 150));
        gyms.add(new Gym("O", 155));
        gyms.add(new Gym("P", 160));
        gyms.add(new Gym("Q", 165));
        gyms.add(new Gym("S", 170));
        gyms.add(new Gym("T", 180));
    }

    public Solution createInitialSolution() {


        Solution solution = new Solution();

        Pokemon pikachu = new Pokemon("Pikachu", 1.5, 6);
        Pokemon bulbasaur = new Pokemon("Bulbasaur", 1.4, 6);
        Pokemon rattata = new Pokemon("Rattata", 1.3, 6);
        Pokemon caterpie = new Pokemon("Caterpie", 1.2, 6);
        Pokemon weedle = new Pokemon("Weedle", 1.1, 6);

        List<Pokemon> availablePokemon = List.of(
                pikachu,
                bulbasaur,
                rattata,
                caterpie,
                weedle
        );

        Random random = new Random();

        for (int i = 0; i < 24; i++) {

            List<Pokemon> participatingPokemon = new ArrayList<>();

            int numberOfPokemon = 1 + random.nextInt(5);

            while (participatingPokemon.size() < numberOfPokemon) {

                Pokemon pokemon = availablePokemon.get(
                        random.nextInt(availablePokemon.size())
                );

                if (!participatingPokemon.contains(pokemon)) {
                    participatingPokemon.add(pokemon);
                }
            }

            solution.addBattle(participatingPokemon);
        }

        return solution;
    }

    public double calculateCost(Solution solution)
    {
        double totalCost = 0;

        for (double battleTime : calculateBattleTimes(solution)) {
            totalCost += battleTime;
        }

        return totalCost;
    }

    public List<Double> calculateBattleTimes(Solution solution) {
        if (solution == null || solution.getBattles().size() > gyms.size()) {
            throw new IllegalArgumentException("Solution must contain no more battles than there are gyms.");
        }

        List<Double> battleTimes = new ArrayList<>();
        for (int i = 0; i < solution.getBattles().size(); i++) {
            battleTimes.add(Battle.calculateTime(gyms.get(i), solution.getBattles().get(i)));
        }
        return List.copyOf(battleTimes);
    }

    public int getGymCount() {
        return gyms.size();
    }

    public char getGymSymbol(int index) {
        return gyms.get(index).getId().charAt(0);
    }
}
