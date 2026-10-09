package Battle;

import Gym.Gym;
import Pokemon.Pokemon;

import java.util.List;

public class Battle
{

    public static double calculateTime(Gym gym, List<Pokemon> listPokemon) {

        if (gym == null || listPokemon == null || listPokemon.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }

        double totalPower = 0;

        for (Pokemon pokemon : listPokemon) {
            if (pokemon == null || pokemon.getPower() <= 0) {
                return Double.POSITIVE_INFINITY;
            }

            totalPower += pokemon.getPower();
        }

        return gym.getDifficulty() / totalPower;
    }
}
