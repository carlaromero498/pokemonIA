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

        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(new Pokemon("Pikachu", 1.5, 6));
        pokemons.add(new Pokemon("Bulbasaur", 1.4, 6));
        pokemons.add(new Pokemon("Rattata", 1.3, 6));
        pokemons.add(new Pokemon("Caterpie", 1.2, 6));
        pokemons.add(new Pokemon("Weedle", 1.1, 6));

        java.util.Collections.shuffle(pokemons);

        for (int i = 0; i < gyms.size(); i++) {
            List<Pokemon> participatingPokemon = new ArrayList<>();

            Pokemon pokemon = pokemons.get(i % pokemons.size());
            participatingPokemon.add(pokemon);

            solution.addBattle(participatingPokemon);
        }

        return solution;
    }





    public double calculateCost(Solution solution) {
        if (!isValidSolution(solution)) {
            return Double.POSITIVE_INFINITY;
        }

        double totalCost = 0;

        for (double battleTime : calculateBattleTimes(solution)) {
            totalCost += battleTime;
        }

        return totalCost;
    }




    public List<Double> calculateBattleTimes(Solution solution) {
        if (solution == null || solution.getNumberOfBattles() != gyms.size()) {
            throw new IllegalArgumentException(
                    "Solution must contain exactly one battle per gym."
            );
        }

        List<Double> battleTimes = new ArrayList<>();

        for (int i = 0; i < solution.getNumberOfBattles(); i++) {
            battleTimes.add(
                    Battle.calculateTime(gyms.get(i), solution.getBattles().get(i))
            );
        }

        return List.copyOf(battleTimes);
    }


    public int getGymCount() {
        return gyms.size();
    }

    public char getGymSymbol(int index) {
        return gyms.get(index).getId().charAt(0);
    }


    public boolean isValidSolution(Solution solution) {
        if (solution == null ||
                solution.getNumberOfBattles() != gyms.size()) {
            return false;
        }

        java.util.Map<String, Integer> energy =
                new java.util.HashMap<>();

        energy.put("Pikachu", 6);
        energy.put("Bulbasaur", 6);
        energy.put("Rattata", 6);
        energy.put("Caterpie", 6);
        energy.put("Weedle", 6);

        for (List<Pokemon> battle : solution.getBattles()) {
            if (battle == null || battle.isEmpty()) {
                return false;
            }

            // Evitamos que un Pokémon aparezca dos veces
            // en la misma batalla.
            java.util.Set<String> participants =
                    new java.util.HashSet<>();

            for (Pokemon pokemon : battle) {
                if (pokemon == null) {
                    return false;
                }

                String name = pokemon.getName();

                if (!energy.containsKey(name)) {
                    return false;
                }

                if (!participants.add(name)) {
                    return false;
                }

                if (energy.get(name) <= 0) {
                    return false;
                }
            }

            // Solo descontamos energía cuando todo el equipo
            // de esa batalla ha superado las comprobaciones.
            for (Pokemon pokemon : battle) {
                String name = pokemon.getName();
                energy.put(name, energy.get(name) - 1);
            }
        }

        // Al menos un Pokémon debe llegar con energía positiva.
        for (int remainingEnergy : energy.values()) {
            if (remainingEnergy >= 1) {
                return true;
            }
        }

        return false;
    }




    public Solution createNeighbor(Solution current) {
        Random random = new Random();

        // Copiamos las batallas para no modificar la solución original.
        Solution neighbor = current.copy();

        int gymIndex = random.nextInt(neighbor.getNumberOfBattles());

        List<Pokemon> battle = new ArrayList<>(
                neighbor.getBattles().get(gymIndex)
        );

        List<Pokemon> allPokemon = List.of(
                new Pokemon("Pikachu", 1.5, 6),
                new Pokemon("Bulbasaur", 1.4, 6),
                new Pokemon("Rattata", 1.3, 6),
                new Pokemon("Caterpie", 1.2, 6),
                new Pokemon("Weedle", 1.1, 6)
        );

        // 0 = sustituir, 1 = añadir, 2 = quitar.
        int operation = random.nextInt(3);

        if (operation == 0) {
            // Sustituir un Pokémon por otro que no esté ya en el equipo.
            int position = random.nextInt(battle.size());

            List<Pokemon> available = new ArrayList<>();

            for (Pokemon candidate : allPokemon) {
                boolean alreadyPresent = false;

                for (int i = 0; i < battle.size(); i++) {
                    if (i != position &&
                            battle.get(i).getName().equals(candidate.getName())) {
                        alreadyPresent = true;
                        break;
                    }
                }

                if (!alreadyPresent &&
                        !battle.get(position).getName().equals(candidate.getName())) {
                    available.add(candidate);
                }
            }

            if (!available.isEmpty()) {
                battle.set(position, available.get(
                        random.nextInt(available.size())
                ));
            }

        } else if (operation == 1) {
            // Añadir un Pokémon que todavía no participe.
            List<Pokemon> available = new ArrayList<>();

            for (Pokemon candidate : allPokemon) {
                boolean alreadyPresent = false;

                for (Pokemon participant : battle) {
                    if (participant.getName().equals(candidate.getName())) {
                        alreadyPresent = true;
                        break;
                    }
                }

                if (!alreadyPresent) {
                    available.add(candidate);
                }
            }

            if (!available.isEmpty()) {
                battle.add(available.get(
                        random.nextInt(available.size())
                ));
            }

        } else {
            // Quitar un Pokémon sin dejar el equipo vacío.
            if (battle.size() > 1) {
                battle.remove(random.nextInt(battle.size()));
            }
        }

        // Construimos la solución vecina.
        Solution result = new Solution();

        for (int i = 0; i < neighbor.getNumberOfBattles(); i++) {
            if (i == gymIndex) {
                result.addBattle(battle);
            } else {
                result.addBattle(
                        new ArrayList<>(neighbor.getBattles().get(i))
                );
            }
        }

        return result;
    }


    public Solution hillClimb(Solution initialSolution) {
        if (initialSolution == null || !isValidSolution(initialSolution)) {
            throw new IllegalArgumentException(
                    "Initial solution must be valid."
            );
        }
        Solution current = initialSolution;
        double currentCost = calculateCost(current);

        int maxIterations = 1000;
        int neighborsPerIteration = 20;

        for (int i = 0; i < maxIterations; i++) {
            Solution bestNeighbor = null;
            double bestNeighborCost = currentCost;

            for (int j = 0; j < neighborsPerIteration; j++) {
                Solution neighbor = createNeighbor(current);

                if (isValidSolution(neighbor)) {
                    double neighborCost = calculateCost(neighbor);

                    if (neighborCost < bestNeighborCost) {
                        bestNeighbor = neighbor;
                        bestNeighborCost = neighborCost;
                    }
                }
            }

            if (bestNeighbor == null) {
                break;
            }

            current = bestNeighbor;
            currentCost = bestNeighborCost;
        }

        return current;
    }



    public void printFinalEnergies(Solution solution) {
        java.util.Map<String, Integer> energy = new java.util.LinkedHashMap<>();

        energy.put("Pikachu", 6);
        energy.put("Bulbasaur", 6);
        energy.put("Rattata", 6);
        energy.put("Caterpie", 6);
        energy.put("Weedle", 6);

        for (List<Pokemon> battle : solution.getBattles()) {
            for (Pokemon pokemon : battle) {
                energy.put(
                        pokemon.getName(),
                        energy.get(pokemon.getName()) - 1
                );
            }
        }

        System.out.println("\n--- FINAL POKEMON ENERGY ---");

        for (java.util.Map.Entry<String, Integer> entry : energy.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void printBattlePlan(Solution solution)
    {
        if (solution == null) {
            throw new IllegalArgumentException("Solution cannot be null.");
        }
        List<Double> times = calculateBattleTimes(solution);

        System.out.println("\n--- BEST BATTLE PLAN ---");

        for (int i = 0; i < solution.getNumberOfBattles(); i++) {
            Gym gym = gyms.get(i);
            List<Pokemon> team = solution.getBattles().get(i);

            System.out.print("Gym " + gym.getId() + ": ");

            for (int j = 0; j < team.size(); j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                System.out.print(team.get(j).getName());
            }

            System.out.printf(" | Time: %.2f minutes%n", times.get(i));
        }
    }




}
