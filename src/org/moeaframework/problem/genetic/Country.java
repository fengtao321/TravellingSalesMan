package org.moeaframework.problem.genetic;

import java.util.Map;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.Node;
import org.moeaframework.problem.tsplib.NodeCoordinates;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.Tour;

public class Country {
	TSPInstance problem = null;
	public Country(TSPInstance problem){
	this.problem = problem;
	}
	
	public TourManager addCities () {
		TourManager manager = new TourManager();
		
		City city;
		NodeCoordinates test = (NodeCoordinates) problem.getDistanceTable();
		Map<Integer, Node> nodes = test.getNodes();

//		double[] position = test.get(0).getPosition();
		for(Map.Entry m: nodes.entrySet()) {
			Node node = (Node) m.getValue();
			double[] position = node.getPosition();
			 city = new City(position[0], position[1]);
			 manager.addCity(city);
		}
		return manager;
	}
}
