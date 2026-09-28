package Search;

import Pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HillClimbing
{
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
}
