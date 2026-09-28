package Battle;

import Gym.Gym;
import Pokemon.Pokemon;

import java.util.List;

public class Battle
{
    public static double calculateTime(Gym gym, List<Pokemon> listPokemon)
    {

        //set var
        double totalPower = 0;

        for (Pokemon pokemon : listPokemon)
        {
            totalPower += pokemon.getPower();
        }

        return gym.getDifficulty() / totalPower;
    }
}
