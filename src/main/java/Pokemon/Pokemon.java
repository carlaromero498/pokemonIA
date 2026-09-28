package Pokemon;

public class Pokemon
{
    private String name;
    private double power;
    private int energy;

    public Pokemon(String name, double power, int energy) {
        this.name = name;
        this.power = power;
        this.energy = energy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    //cada vez que loita chamo a esta funcion para restar energia
    public void loseEnergy() {
        if (energy > 0) {
            energy--;
        }
    }

    //resetear su energia a inicial:6
    public void resetEnergy() {
        energy = 6;
    }

    //comprobar se pode loitar
    public boolean canFight() {
        return energy > 0;
    }
}
