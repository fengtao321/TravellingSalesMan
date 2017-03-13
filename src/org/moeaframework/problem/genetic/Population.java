package org.moeaframework.problem.genetic;

public class Population {
	// Holds population of tours
    GeneticTour[] tours;

    // Construct a population
    public Population(int populationSize, boolean initialise) {
        tours = new GeneticTour[populationSize];
        // If we need to initialise a population of tours do so
        if (initialise) {
            // Loop and create individuals
            for (int i = 0; i < populationSize(); i++) {
                GeneticTour newTour = new GeneticTour();
                newTour.generateIndividual();
                saveTour(i, newTour);
            }
        }
    }
    
    // Saves a tour
    public void saveTour(int index, GeneticTour tour) {
        tours[index] = tour;
    }
    
    // Gets a tour from population
    public GeneticTour getTour(int index) {
        return tours[index];
    }

    // Gets the best tour in the population
    public GeneticTour getFittest() {
        GeneticTour fittest = tours[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getTour(i).getFitness()) {
                fittest = getTour(i);
            }
        }
        return fittest;
    }

    // Gets population size
    public int populationSize() {
        return tours.length;
    }
}
