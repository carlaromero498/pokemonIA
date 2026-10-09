package Search;

import Pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class Solution
{
    private List<List<Pokemon>> battles;


    public Solution() {
        battles = new ArrayList<>();
    }

    public List<List<Pokemon>> getBattles()
    {
        return battles;
    }

    public void addBattle(List<Pokemon> pokemons)
    {
        battles.add(pokemons);
    }

    public int getNumberOfBattles()
    {
        return battles.size();
    }

    public Solution copy()
    {
        Solution copy = new Solution();

        for (List<Pokemon> battle : battles) {
            copy.addBattle(new ArrayList<>(battle));
        }

        return copy;
    }

}
