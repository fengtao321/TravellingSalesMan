package org.moeaframework.problem.genetic;

import java.util.ArrayList;

public class TourManager {
	// Holds our cities
    private static ArrayList destinationCities = new ArrayList<City>();

    // Adds a destination city
    public static void addCity(City city) {
        destinationCities.add(city);
    }
    
    // Get a city
    public static City getCity(int index){
        return (City)destinationCities.get(index);
    }
    
    // Get the number of destination cities
    public static int numberOfCities(){
        return destinationCities.size();
    }
    
    public static int getCityIndex (TourManager manager, City city) {
    	return destinationCities.indexOf(city);
    }
}
