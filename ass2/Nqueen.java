import java.util.*;
import java.lang.Math;
     
public class Nqueen{
   public static int MaxAttackingPairs;
   public static final int BOARD_SIZE = 8;
   public static final int POPULATION = 100; /* must be a multiple of 4 if using breedEveryEven */
   public static final double KEEP_RATIO = 0.2;
   public static final double MUTATION_P = 0.05;
   public static final int MAX_GENERATIONS = 100;

   public static List<Board> boards;
   
   public static void main(String[] args){  
      MaxAttackingPairs = binomial(BOARD_SIZE, 2);

      int generations = 0;
      boards = new ArrayList<Board>();
      resetAll();
      
      while(getBest().fitness > 0)
         {
            //shuffle(boards);
            sort();

            //breedEveryPair(boards);
            //breedRandomPair(boards);
            //breedEverySecondPair(boards);
            //breedEveryNew(boards);
            breedPercent();

            if(generations++ > MAX_GENERATIONS){
               resetAll();
               generations = 0;
            }
         }
      
      System.err.println(getBest());
      System.out.println(generations);     
   }

   public static void resetAll(){
      for(int i = 0; i < POPULATION; i++)
         boards.add(new Board());
   }
   
   public static void breedEveryPair(){
      for(int i = 0; i < POPULATION/2; i+=2){
         Board[] c = crossover_split(boards.get(i), boards.get(i+1));
         //Board[] c = crossover_uniform(boards.get(i), boards.get(i+1));
         // boards.set(POPULATION/2 + i, c[0]);
         // boards.set(POPULATION/2 + i + 1, c[1]);
         boards.set(POPULATION - i - 1, c[0]);
         boards.set(POPULATION - i - 2, c[1]);
      }
   }
   public static void breedEverySecondPair(){
      for(int i = 0; i < POPULATION; i+=4){
         Board[] c = crossover_split(boards.get(i), boards.get(i+2));
         boards.set(i+1, c[0]);
         boards.set(i+3, c[1]);
      }
   }
   public static void breedRandomPair(){
      for(int i = 0; i < POPULATION; i+=2){
         Board[] c = crossover_split(boards.get((int)(Math.random() * POPULATION)), boards.get((int)(Math.random() * POPULATION)));;
         boards.set((int)(Math.random() * POPULATION), c[0]);
         boards.set((int)(Math.random() * POPULATION), c[1]);
      }
   }
   public static void breedEveryNew(){
      for(int i = 0; i < POPULATION/4; i++){
         Board[] c = crossover_split(boards.get(i), boards.get(POPULATION/4+i));
         boards.set(POPULATION/2 + i, c[0]);
         boards.set((int)(POPULATION*(0.75)) + i, c[1]);
      }
   }
   public static void breedPercent(){
      int numParents = (int)(KEEP_RATIO*POPULATION);
      int numChildren = POPULATION - numParents;
      for(int i = 0; i < numChildren; i++){
         Board[] c = crossover_split(boards.get((int)(Math.random() * POPULATION)), boards.get((int)(Math.random() * POPULATION)));
         boards.set(numParents + i, c[0]);
      }
   }

   public static Board[] crossover_split(Board parent1, Board parent2){
      Board[] children = new Board[2];
      //int splitPoint = (int)(Math.random() * (BOARD_SIZE-1));
      int splitPoint = (int)((Math.min(parent1.fitness, parent2.fitness) /
                              (double)Math.max(parent1.fitness, parent2.fitness)) * 7);
      children[0] = new Board(parent1, parent2, splitPoint);
      children[1] = new Board(parent2, parent1, splitPoint);
      return children;
   }
   public static Board[] crossover_uniform(Board parent1, Board parent2){
      Board[] children = new Board[2];
      children[0] = new Board(parent1, parent2);
      children[1] = new Board(parent2, parent1);
      return children;
   }

   public static Board getBest(){
      Board best = boards.get(0);
      for(int i = 1; i < POPULATION; i+=1)
         if(boards.get(i).fitness < best.fitness)
            best = boards.get(i);
      return best;
   }
     
   public static void shuffle(){
      Random random = new Random();
      for(int i = POPULATION-1; i > 0; i--){
         int index = random.nextInt(i+1);
         Board temp = boards.get(index);
         boards.set(index, boards.get(i));
         boards.set(i, temp);
      }
   }
   public static void sort(){
      Collections.sort(boards, new Comparator<Board>(){
            @Override
               public int compare(Board v1, Board v2) {
               return v1.compareTo(v2);
            }
         });
   }

   /* the maximum number of attacking queens is computed from the follwing methods */
   private static int binomial(int n, int k){
      return factorial(n) / (factorial(k)*factorial(n-k));
   }
   private static int factorial(int n){
      if (n < 0) {
         System.err.println("err:  negative factorial");
         return 0;
      }
      if (n < 2) return 1;
      return n * factorial(n-1);
   }
}
     
class Board {
   /* the particular board permutation is stored in aptly named array */
   public int[] array;
   public double fitness;
       
   public Board(){
      array = new int[Nqueen.BOARD_SIZE];
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++)
         array[i] = i; //array = {1, 2, 3, 4 ... }
      shuffle();
      fitness = calcFitness();
   }

   public Board(Board parent1, Board parent2){
      array = new int[Nqueen.BOARD_SIZE];
      Board best = (parent1.fitness > parent2.fitness) ? parent1 : parent2;
      Board worst = (parent1.fitness < parent2.fitness) ? parent1 : parent2;
      double p = best.fitness / (worst.fitness + best.fitness);
      
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++){
         if(Math.random() < p)
            array[i] = best.array[i];
         else
            array[i] = worst.array[i];
      }
      mutate();
      this.fitness = calcFitness();
   }
   
   public Board(Board parent1, Board parent2, int splitPoint){
      array = new int[Nqueen.BOARD_SIZE];
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++){
         if(i <= splitPoint)
            array[i] = parent1.array[i];
         else
            array[i] = parent2.array[i];
      }
      mutate();
      this.fitness = calcFitness();
   }
     
   private void mutate(){
      for(int n = 0; n < Nqueen.BOARD_SIZE; n++){
         if(Math.random() < Nqueen.MUTATION_P){
            array[n] = (int)(Math.random() * Nqueen.BOARD_SIZE);
         }
      }
   }
       
   /* smaller fitness is better */
   private double calcFitness(){
      int attackingPairs = 0;
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++)
         attackingPairs += countEast(i) + countSouthEast(i) + countNorthEast(i);

      // if (attackingPairs == 0) return 0;
      // if (attackingPairs == Nqueen.MaxAttackingPairs) return 1;
      // return 1 / (double)(Nqueen.MaxAttackingPairs - attackingPairs);
      return attackingPairs;
   }
   
   //count the queens horizontally left (east) of the current piece
   private int countEast(int start){
      int result = 0;
      for(int i = start+1; i < Nqueen.BOARD_SIZE; i++)
         if (array[i] == array[start])
            result++;                        
      return result;
   }
   private int countNorthEast(int start){
      int result = 0;
      for(int i = start+1; i < Nqueen.BOARD_SIZE; i++)
         if (array[i] == array[start]-i+start)
            result++;
      return result;
   }
   private int countSouthEast(int start){
      int result = 0;
      for(int i = start+1; i < Nqueen.BOARD_SIZE; i++)
         if (array[i] == array[start]+i-start)
            result++;
      return result;
   }

   /* simple knuth shuffle */
   private void shuffle(){
      Random random = new Random();
      for(int i = Nqueen.BOARD_SIZE-1; i > 0; i--){
         int index = random.nextInt(i+1);
         int temp = array[index];
         array[index] = array[i];
         array[i] = temp;
      }
   }
     
   public int compareTo(Board v2){
      double tmp = this.fitness - v2.fitness;
      return (tmp > 0) ? 1 : (tmp < 0) ? -1 : 0;
   }
     
   public String toString(){
      String grid = "";
      String prefix = " ";
      for(int row = 0; row < Nqueen.BOARD_SIZE; row++){
         prefix += array[row] + " ";
         grid += "|";
         for(int col = 0; col < Nqueen.BOARD_SIZE; col++){
            if(array[col] == row)
               grid += "Q|";
            else
               grid += "_|";
         }
         grid += "\n";
      }
      return "fitness: " + fitness + "\n" + prefix + "\n" + grid;
   }

   public String toString2(){
      String s = "";
      for(int col = 0; col < Nqueen.BOARD_SIZE; col++)
         s += array[col];
      return s;
   }
}
