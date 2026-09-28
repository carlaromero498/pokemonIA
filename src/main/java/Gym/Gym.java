package Gym;

public class Gym
{
    private String id;
    private int difficulty;

    public Gym(String id, int difficulty)
    {
        this.id = id;
        this.difficulty = difficulty;
    }

    public String getId() {
        return id;
    }

    public int getDifficulty()
    {
        return difficulty;
    }
}
