import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TravelingSalesman {

	ArrayList<Integer[]> population;
	ArrayList<City> cities;

	
	Random rand = new Random();



	public static void main(String[] args) {
		TravelingSalesman tsp = new TravelingSalesman();
		tsp.simulate();
	}

	public TravelingSalesman() {
		cities = new ArrayList<City>();	
		getCities();
		
		int populationSize = 100;
		
		
		population = new ArrayList<Integer[]>();
		

		for(int i = 0; i < populationSize; i++) {
			population.add(new Integer[cities.size()]);
			for(int j = 0; j < cities.size(); j ++) {
				population.get(i)[j] = j;
			}
			int temp = rand.nextInt(400);
			for(int k = 0; k < temp; k++) {
				mutate(i);
			}
		}



	}
	
	public void getCities()  {
		 	BufferedReader br = null;
		    try {
		    	br = new BufferedReader(new FileReader("TSPDATA.txt"));
		        String line = br.readLine();
		 	   int count = 0;
	        	
		        while (line != null) {
		        
		            if(count >= 2) {
		            	line = line.replaceAll("\\s+",",");
		            	line = line.substring(1);
		            	line = line.substring(line.indexOf(",")+1);
		            	
		            	int x = Integer.parseInt(line.substring(0,line.indexOf(",")));
		            	int y = Integer.parseInt(line.substring(line.indexOf(",")+1));
		            	City c = new City(x,y);
		            	cities.add(c);
	
		            }
		            
		            count++;
		            line = br.readLine();
		            
		        }
		    
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    }
		    finally {
		        try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		   
	
	}
	
	public void mutateAll(){
		
		
		for(int i = 0; i < 13; i++) {
			mutate(rand.nextInt(population.size()));
		}
	}

	public void mutate(int index) {

		int index1 = rand.nextInt(cities.size());
		int index2 = rand.nextInt(cities.size());
		int val1 = population.get(index)[index1];
		int val2 = population.get(index)[index2];

		population.get(index)[index2] = val1;
		population.get(index)[index1] = val2;



	}

	public void simulate() {
		int count = 0;


		while(count < 1000000){
			
			if(count % 1000 == 0) {
				System.out.println("Crossovers: " + (count) + "\tAvg Fitness:" + averageFitness());
			}	
			

			crossOver();
			mutateAll();
			removeWorstTwo();
			
					
			count++;
			
			
		}

		
		System.out.println("Finished simulation");
		Integer[] solution = findBest();
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i] + ", ");
		}


	}
	
	public Integer[] findBest() {
		int bestFit = Integer.MAX_VALUE;
		int bestIndex = -1;
		for(int i = 0; i < population.size(); i ++) {
			int fit = fitness(population.get(i));
			if(fit < bestFit ) {
				bestFit = fit;
				bestIndex = i;
			}
		}
		
		return population.get(bestIndex);
	}

	public double averageFitness() {

		int sum = 0;
		for(int i = 0; i < population.size(); i++) {	
			sum += fitness(population.get(i));
		}

		return (sum*1.0000)/population.size();

	}

	public int[] findParents() {


		List<Integer[]> parentsList = new ArrayList<Integer[]>();
		int bestFit = Integer.MAX_VALUE;
		int bestIndex = -1;

		for(int i = 0; i < 10; i ++) {
			int index = rand.nextInt(population.size());
			Integer[] tempArr = { fitness(population.get(index)) , index};
			parentsList.add(tempArr);
			if(tempArr[0] < bestFit) {
				bestFit = tempArr[0];
				bestIndex = tempArr[1];
			}
		}

		int[] parents = new int[2];
		parents[0] = bestIndex;

		int oldIndex = bestIndex;
		bestFit =  Integer.MAX_VALUE;
		bestIndex = -1;
		for(int j = 0; j < parentsList.size(); j++) {
			if(parentsList.get(j)[0] < bestFit && parentsList.get(j)[1] != oldIndex) {

				bestFit = parentsList.get(j)[0];
				bestIndex = parentsList.get(j)[1];

			}
		}

		parents[1] = bestIndex;
		return parents;


	}

	public void crossOver() {
		
		int[] indexes = findParents();

		Integer[] parent1 = population.get(indexes[0]);
		Integer[] parent2 = population.get(indexes[1]);
		
		
		ArrayList<HashSet<Integer>> neighbors = new ArrayList<HashSet<Integer>>();
		
		for(int i = 0; i < parent1.length; i++) {

			neighbors.add(new HashSet<Integer>());
			if(i == 0) {
				neighbors.get(i).add(parent1[1]);
				neighbors.get(i).add(parent1[parent1.length-1]);
				
				neighbors.get(i).add(parent2[1]);
				neighbors.get(i).add(parent2[parent2.length-1]);
				
			}
			else if(i == parent1.length -1) {
				neighbors.get(i).add(parent1[0]);
				neighbors.get(i).add(parent1[i-1]);
				
				neighbors.get(i).add(parent2[0]);
				neighbors.get(i).add(parent2[i-1]);
			}
			else {
				neighbors.get(i).add(parent1[i-1]);
				neighbors.get(i).add(parent1[i+1]);
				
				neighbors.get(i).add(parent2[i-1]);
				neighbors.get(i).add(parent2[i+1]);
			}
		}
		
		
		
		ArrayList<Integer> child1 = new ArrayList<Integer>();
		
		double probability = rand.nextDouble();
		int allele;
		if(probability <= 0.5) {
			allele = parent1[0];
		}
		else {
			allele = parent2[0];
		}
		
		
	
		while(child1.size() < cities.size()){
			child1.add(allele);
			
			//removeFromTable(allele,neighbors);
			for(HashSet<Integer> h : neighbors) {
				h.remove(allele);
			}
			
			
			
			if(neighbors.get(allele).isEmpty()) {
	
				int r = rand.nextInt(cities.size());
				if(child1.contains(r)) {
					r = 0;
					while(child1.contains(r) && r < cities.size()) {
						r++;
					}
				}
				allele = r;
			}
			else {
			
				boolean common = false;
				for(Integer a : neighbors.get(allele)){
					if(a < 0){
						allele = Math.abs(a);
						common = true;
						break;
					}
				}
			if(!common) {
				int minSize = Integer.MAX_VALUE;
				int minIndex = 0;
				int count = 0;
				
				for(HashSet<Integer> h : neighbors) {
					
						if(h.size() < minSize && !child1.contains(count)) {
							minSize = h.size();
							minIndex = count;
						}
						count++;
					
				}
				
				allele = minIndex;
				
			}
		}		
			
		}
		Integer[] tempArr1 = Arrays.copyOf(child1.toArray(), child1.toArray().length, Integer[].class);
		population.add(tempArr1);
	
	}
	
	/*

	public void crossOver() {
		int[] indexes = findParents();

		Integer[] parent1 = population.get(indexes[0]);
		Integer[] parent2 = population.get(indexes[1]);

		int crossOverPoint = rand.nextInt(cities.size());//0;
		
		int probability = rand.nextInt(3);
		
		if(probability == 0) {
			crossOverPoint = cities.size()/2 -1;
		}
		else if(probability == 1) {
			crossOverPoint = cities.size()/2;
		}
		else if(probability == 2) {
			crossOverPoint = cities.size()/2+1;
		}
		




		ArrayList<Integer> child1 = new ArrayList<Integer>(cities.size());
		ArrayList<Integer> child2 = new ArrayList<Integer>(cities.size());

		//0 to crossoverpoint from first
		// crossoverpoint to n

		for(int i = 0; i < crossOverPoint; i++) {
			child1.add(parent1[i]);
			child2.add( parent2[i]);
		}



		for(int j = crossOverPoint; j < cities.size(); j++) {
			if(!child1.contains(parent2[j])){
				child1.add(parent2[j]);
			}
			if(!child2.contains(parent1[j])){
				child2.add(parent1[j]);
			}
		}

		for(int k = 0; k < crossOverPoint; k++) {
			if(!child1.contains(parent2[k]))
				child1.add(parent2[k]);

			if(!child2.contains(parent1[k]))
				child2.add(parent1[k]);
		}


		Integer[] tempArr1 = Arrays.copyOf(child1.toArray(), child1.toArray().length, Integer[].class);
		Integer[] tempArr2 = Arrays.copyOf(child2.toArray(), child1.toArray().length, Integer[].class);
		population.add(tempArr1);
		population.add(tempArr2);


		

	}
	
	*/
	
	public void removeWorstTwo(){
		int maxIndex = -1;
		int maxFitness = -1;

		int nextIndex = -1;
		int nextFitness = -1;
		for(int i = 0; i < population.size(); i++) {

			int fit = fitness(population.get(i));
			if(fit > maxFitness) {
				maxFitness = fit;
				maxIndex = i;
			}
		}

		population.remove(maxIndex);



/*

		for(int j = 0; j < population.size(); j++) {
			int fit = fitness(population.get(j));
			if(fit > nextFitness && j != maxIndex ){
				nextFitness = fit;
				nextIndex = j;
			}
		}


		population.remove(nextIndex);	
	*/

	}



	public int fitness(Integer[] individual) {
		int distance= 0;
		
		for(int i = 0; i < individual.length-1; i++) { //TODO: change n here
			distance+= distanceBetween(individual[i],individual[i+1]);
		}
		distance+=distanceBetween(individual.length-1,0);
		
		return distance;
	}
	
	public double distanceBetween(int index1, int index2) {
		City city1 = cities.get(index1);
		City city2 = cities.get(index2);
		
		return Math.sqrt(Math.pow(city2.getX()-city1.getX(),2) + Math.pow(city2.getY()-city1.getY(),2));
	}

	




}