import java.util.*;
import java.lang.Math;
     
public class Nqueen{
   public static final int BOARD_SIZE = 8;
   public static final int POPULATION = 100;
   public static int MaxAttackingPairs;
   static final double MUTATION_P = 0.01;
       
   public static void main(String[] args){  
      MaxAttackingPairs = binomial(BOARD_SIZE, 2);

      int gen = 0;
      List<Board> boards = new ArrayList<Board>();

      for(int i = 0; i < POPULATION; i++)
         boards.add(new Board());
      
      sort(boards);
      while(boards.get(0).fitness > 0 && gen < 10000)
         {
            //shuffle(boards);
            breedEveryPair(boards);
            sort(boards);
            gen++;
         }
      
      System.out.println(boards.get(0));
      System.out.println("generations: " + gen);     
   }

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

   public static void breedEveryPair(List<Board> boards){
      int added = 0;
      for(int i = 0; i < POPULATION/2; i+=2){
         Board[] c = crossover_split(boards.get(i), boards.get(i+1));
         //Board[] c = crossover_uniform(boards.get(i), boards.get(i+1));
         // boards.set(POPULATION/2 + i, c[0]);
         // boards.set(POPULATION/2 + i + 1, c[1]);
         boards.set(POPULATION - i - 1, c[0]);
         boards.set(POPULATION - i - 2, c[1]);
         added += 2;
      }
   }

  public static Board[] crossover_split(Board parent1, Board parent2){
      Board[] children = new Board[2];
      //int splitPoint = (int)(Math.random() * (BOARD_SIZE-1));
      double temp =
      Math.min(parent1.fitness, parent2.fitness) /
       (double)Math.max(parent1.fitness, parent2.fitness); 
      //System.out.println(parent1.fitness + " " + parent2.fitness + " " +  temp);
      int splitPoint = (int)(temp * 7);
      
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

   public static Board[] crossover_three(Board parent1, Board parent2, Board parent3){
      Board[] children = new Board[3];
      children[0] = new Board(parent1, parent2, parent3);
      children[1] = new Board(parent1, parent3, parent2);
      children[2] = new Board(parent2, parent3, parent1);
      return children;
   }
     
   public static void shuffle(List<Board> list){
      Random random = new Random();
      for(int i = POPULATION-1; i > 0; i--){
         int index = random.nextInt(i+1);
         Board temp = list.get(index);
         list.set(index, list.get(i));
         list.set(i, temp);
      }
   }

   public static void sort(List<Board> list){
      Collections.sort(list, new Comparator<Board>(){
         @Override
            public int compare(Board v1, Board v2) {
            return v1.compareTo(v2);
         }
      });
   }
}
     
class Board {
   public int[] array;
   public double fitness;
       
   public Board(){
      array = new int[Nqueen.BOARD_SIZE];
      /*for(int i = 0; i < size; i++)
        array[i] = (int)(Math.random() * size);
        mutate();*/
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

   public Board(Board parent1, Board parent2, Board parent3){
      array = new int[Nqueen.BOARD_SIZE];
      
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++){
            if (parent1.array[i] == parent2.array[i])
               array[i] = parent1.array[i];
            else
               array[i] = parent3.array[i];
         }
      mutate();
      fitness = calcFitness();
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
      if(Math.random() < Nqueen.MUTATION_P){
         int a1 = (int)(Math.random() * Nqueen.BOARD_SIZE);
         int a2 = (int)(Math.random() * (Nqueen.BOARD_SIZE-1));
         if(a1 == a2) a2++;
         //swap indeces a1 and a2
         int tmp = array[a1];
         array[a1] = array[a2];
         array[a2] = tmp;
      }
      
      // if(Math.random() < MUTATION_P)
      // {
      //    int tmp = (int)(Math.random() * Nqueen.BOARD_SIZE);
      //    array[tmp] = (int)(Math.random() * Nqueen.BOARD_SIZE);
      //    }
   }
       
   /* smaller fitness is better */
   private double calcFitness(){
      int attackingPairs = 0;
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++)
         attackingPairs += countEast(i) + countSouthEast(i) + countNorthEast(i);
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
