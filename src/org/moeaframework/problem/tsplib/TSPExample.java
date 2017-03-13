/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.moeaframework.problem.tsplib;

//import TSPInstance;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.genetic.City;
import org.moeaframework.problem.genetic.Country;
import org.moeaframework.problem.genetic.GA;
import org.moeaframework.problem.genetic.GeneticTour;
import org.moeaframework.problem.genetic.Population;
import org.moeaframework.problem.genetic.TourManager;

/**
 * Demonstration of optimizing a TSP problem using the MOEA Framework
 * optimization library (http://www.moeaframework.org/).  A window will appear
 * showing the progress of the optimization algorithm.  The window will contain
 * a visual representation of the TSP problem instance, with a thick red line
 * indicating the best tour found by the optimization algorithm.  Light gray
 * lines are the other (sub-optimal) tours in the population.
 */
public class TSPExample {
	
	/**
	 * The color for population members.
	 */
	private static final Color lightGray = new Color(128, 128, 128, 64);
	private static int width = 1200;
	private static int height = 1000;
	
	/**
	 * Converts a MOEA Framework solution to a {@link Tour}.
	 * 
	 * @param solution the MOEA Framework solution
	 * @return the tour defined by the solution
	 */
	public static Tour toTour(Solution solution) {
		int[] permutation = EncodingUtils.getPermutation(
				solution.getVariable(0));
		
		// increment values since TSP nodes start at 1
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]++;
		}
		
		return Tour.createTour(permutation);
	}
	
	/**
	 * Saves a {@link Tour} into a MOEA Framework solution.
	 * 
	 * @param solution the MOEA Framework solution
	 * @param tour the tour
	 */
	public static void fromTour(Solution solution, Tour tour) {
		int[] permutation = tour.toArray();
		
		// decrement values to get permutation
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]--;
		}
		
		EncodingUtils.setPermutation(solution.getVariable(0), permutation);
	}
	
	/**
	 * The optimization problem definition.  This is a 1 variable, 1 objective
	 * optimization problem.  The single variable is a permutation that defines
	 * the nodes visited by the salesman.
	 */
	public static class TSPProblem extends AbstractProblem {

		/**
		 * The TSP problem instance.
		 */
		private final TSPInstance instance;
		
		/**
		 * The TSP heuristic for aiding the optimization process.
		 */
		private final TSP2OptHeuristic heuristic;
		
		/**
		 * Constructs a new optimization problem for the given TSP problem
		 * instance.
		 * 
		 * @param instance the TSP problem instance
		 */
		public TSPProblem(TSPInstance instance) {
			super(1, 1);
			this.instance = instance;
			
			heuristic = new TSP2OptHeuristic(instance);
		}

		public void evaluate(Solution solution) {
			Tour tour = toTour(solution);
			
			// apply the heuristic and save the modified tour
			heuristic.apply(tour);
			fromTour(solution, tour);

			solution.setObjective(0, tour.distance(instance));
		}

		public Solution newSolution() {
			Solution solution = new Solution(1, 1);
			
			solution.setVariable(0, EncodingUtils.newPermutation(
					instance.getDimension()));
			
			return solution;
		}
		
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param args the command line arguments; none are defined
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws IOException {
		//add JFile chooser and button
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./data/tsp/pr76.tsp"));
		
		int result = fileChooser.showOpenDialog(fileChooser);
		File selectedFile = new File("./data/tsp/pr76.tsp");
		if (result == JFileChooser.APPROVE_OPTION) {
		selectedFile = fileChooser.getSelectedFile();
		System.out.println(selectedFile.getName());
		}

		// create the TSP problem instance and display panel
		TSPInstance instance = new TSPInstance(selectedFile);
		TSPPanel panel = new TSPPanel(instance);
		panel.setAutoRepaint(false);
		
		// create other components on the display
		StringBuilder progress = new StringBuilder();
		JTextArea progressText = new JTextArea();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(panel);
		
		//create right components on the display
		JSplitPane horizontalPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalPane.setTopComponent(new JScrollPane(progressText));
		JPanel rightPanel = new JPanel();

		rightPanel.add(fileChooser);
		
		horizontalPane.setBottomComponent(rightPanel);
		horizontalPane.setDividerLocation(width/2);
		
		splitPane.setBottomComponent(horizontalPane);
		splitPane.setDividerLocation(height/2);
		splitPane.setResizeWeight(1.0);
		

		
		// display the panel on a window
		JFrame frame = new JFrame("Traveling Salesman Problem");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		


		
		// create the optimization problem and evolutionary algorithm
		Country country = new Country(instance);
		TourManager manager =country.addCities();
		
		// Initialize population
        Population pop = new Population(50, true);
        
        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100; i++) {
            pop = GA.evolvePopulation(pop);
        }
        

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());
        
     // clear existing tours in display
     ((TSPPanel) panel).clearTours();
        
        // display current optimal solutions with red line   	
		GeneticTour geneticTour = pop.getFittest();
		Tour best = Tour.createTour(geneticTour.getTour(manager));
		((TSPPanel) panel).displayTour(best, Color.RED, new BasicStroke(2.0f));
		progress.insert(0, "Iteration " + pop.getFittest() + ": " +
				pop.getFittest().getDistance() + "\n");
		progressText.setText(progress.toString());
        
     // repaint the TSP display
     	panel.repaint();

	/*	
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				"NSGAII", properties, problem);
		
		int iteration = 0;
		
		// now run the evolutionary algorithm
		while (frame.isVisible()) {
			algorithm.step();
			iteration++;
			
			// clear existing tours in display
			((TSPPanel) panel).clearTours();

			// display population with light gray lines
			if (algorithm instanceof EvolutionaryAlgorithm) {
				EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm)algorithm;
				
				for (Solution solution : ea.getPopulation()) {
					((TSPPanel) panel).displayTour(toTour(solution), lightGray);
				}
			}
			
			// display current optimal solutions with red line
			Tour best = toTour(algorithm.getResult().get(0));
			((TSPPanel) panel).displayTour(best, Color.RED, new BasicStroke(2.0f));
			progress.insert(0, "Iteration " + iteration + ": " +
					best.distance(instance) + "\n");
			progressText.setText(progress.toString());
			
			// repaint the TSP display
			panel.repaint();
		}			*/
	}
//	}

}
