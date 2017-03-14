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
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
		
		//add Algorithm Selection
		Object[] algorithmOptions = {"Genetic algorithm", "Lin-Kernighan algorithm", "RUN"};
		Object[] algorithmOption = {"Genetic algorithm", "Lin-Kernighan algorithm"};
		JOptionPane option = new JOptionPane("Select an algorithm and run: ",JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION,null, algorithmOptions);
		int algorithmSelection = option.showOptionDialog(null, "Select an algorithm and run:", null, JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null, algorithmOption, algorithmOptions[0]);

		//data source selection before the panel is shown
		int result = fileChooser.showOpenDialog(fileChooser);
		File selectedFile = new File("./data/tsp/pr76.tsp");
		if (result == JFileChooser.APPROVE_OPTION) {
		selectedFile = fileChooser.getSelectedFile();
		System.out.println(selectedFile.getName());
		}

		graphicUI(selectedFile, algorithmSelection);

	
	}
	
	public static void graphicUI(File selectedFile, int algorithmSelection) throws IOException {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./data/tsp/pr76.tsp"));
	
		Object[] algorithmOptions = {"Genetic algorithm", "Lin-Kernighan algorithm", "RUN"};
		JOptionPane option = new JOptionPane("Select an algorithm and run: ",JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION,null, algorithmOptions);
		
		// create the TSP problem instance and display panel
				TSPInstance instance = new TSPInstance(selectedFile);
				TSPPanel panel = new TSPPanel(instance);
				panel.setAutoRepaint(false);
				
				// create other components on the display
				JTextArea progressText = new JTextArea();
				
				JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				splitPane.setTopComponent(panel);
				
				//create right components on the display
				JSplitPane horizontalPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				horizontalPane.setTopComponent(new JScrollPane(progressText));
				JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

				rightPanel.add(fileChooser);
				rightPanel.add(option);
				
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
				
				//algorithm selection before the panel is shown
				if (algorithmSelection == 0 ) {
				//	System.out.println(algorithmOptions[algorithmSelection]);
					geneticAlg(instance, panel, progressText);
				}
				if (algorithmSelection == 1 ) {
				//	System.out.println(algorithmOptions[algorithmSelection]);
				}
				
				
				//selection after the panel is shown 
				boolean runOption = true;
				while(true) {
					String alg = (String) option.getValue();
					if(alg.equals(algorithmOptions[2])) {
						runOption = true;	
					}
					if((alg.equals(algorithmOptions[0])) && (runOption == true)) {
						runOption = false;
						selectedFile = fileChooser.getSelectedFile();
						System.out.println(alg);
						System.out.println(selectedFile.getName());
		
		
						TSPInstance instance2 = new TSPInstance(selectedFile);
						TSPPanel panel2 = new TSPPanel(instance2);
						panel2.setAutoRepaint(true);
						
						graphicUI(selectedFile, 0);
		
					}
					
					if(alg.equals(algorithmOptions[1])&& (runOption == true)) {
						runOption = false;
						System.out.println(alg);
					}
		
				}
			
	}
	
	public static void geneticAlg(TSPInstance instance, TSPPanel panel, JTextArea progressText) {
		long startTime = System.currentTimeMillis();
		//((TSPPanel) panel).clearTours();
		
		// create the optimization problem and evolutionary algorithm
		Country country = new Country(instance);
		TourManager manager =country.addCities();
		StringBuilder progress = new StringBuilder();
				
		// Initialize population
		Population pop = new Population(50, true);
		        
		// Evolve population for 100 generations
		pop = GA.evolvePopulation(pop);
		for (int i = 0; i < 100; i++) {
		    pop = GA.evolvePopulation(pop);
		}
        
	        // display current optimal solutions with red line   	
			GeneticTour geneticTour = pop.getFittest();
			Tour best = Tour.createTour(geneticTour.getTour(manager));
			((TSPPanel) panel).displayTour(best, Color.RED, new BasicStroke(2.0f));
			
			//String to be shown on the screen
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			progress.insert(0, "Iteration " + pop.getFittest() + "\n");
			progress.insert(1, "distance: " + pop.getFittest().getDistance() + "\n");
			progress.insert(1, "running time: " + totalTime + "\n");
			progressText.setText(progress.toString());
	        
	     // repaint the TSP display
	     	panel.repaint();
	     	     	
	     	return;
	}
	//	}

}
