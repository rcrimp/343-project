import java.util.*;
import java.lang.Math;
     
public class Nqueen
{
   static final int board_size = 8;
   static final int population = 100;
    
       
   public static void main(String[] args)
   {  
      int bad = 0;
      for(int q = 0; q < 1000; q++){
         int gen = 0;
         List<Board> boards = new ArrayList<Board>();

         for(int i = 0; i < population; i++)
            boards.add(new Board(board_size));
      
         sort(boards);
         while(boards.get(0).fitness > 0 && gen < 10000)
            {
               //shuffle(boards);
               breedEveryPair(boards);
               sort(boards);
               gen++;
            }

         if(gen == 10000)
            {
               bad++;
            }
      
         //System.out.println(boards.get(0));
         System.out.println("generations: " + gen);
      }
      System.out.println("bad: " + bad);
      
   }

   public static void breedEveryPair(List<Board> boards)
   {
      int added = 0;
      for(int i = 0; i < population/2; i+=2)
      {
         Board[] c = crossover_split(boards.get(i), boards.get(i+1));
         //Board[] c = crossover_uniform(boards.get(i), boards.get(i+1));
         // boards.set(population/2 + i, c[0]);
         // boards.set(population/2 + i + 1, c[1]);
         boards.set(population - i - 1, c[0]);
         boards.set(population - i - 2, c[1]);
         added += 2;
      }
   }

  tatic Board[] crossover_split(Board parent1, Board parent2)
   {
      Board[] children = new Board[2];
      //int splitPoint = (int)(Math.random() * (board_size-1));
      double temp =
      Math.min(parent1.fitness, parent2.fitness) /
       (double)Math.max(parent1.fitness, parent2.fitness); 
      //System.out.println(parent1.fitness + " " + parent2.fitness + " " +  temp);
      int splitPoint = (int)(temp * 7);
      
      children[0] = new Board(board_size, parent1, parent2, splitPoint);
      children[1] = new Board(board_size, parent2, parent1, splitPoint);
      return children;
   }

   public static Board[] crossover_uniform(Board parent1, Board parent2)
   {
      Board[] children = new Board[2];
      children[0] = new Board(board_size, parent1, parent2);
      children[1] = new Board(board_size, parent2, parent1);
      return children;
   }

   public static Board[] crossover_three(Board parent1, Board parent2, Board parent3)
   {
      Board[] children = new Board[3];
      children[0] = new Board(board_size, parent1, parent2, parent3);
      children[1] = new Board(board_size, parent1, parent3, parent2);
      children[2] = new Board(board_size, parent2, parent3, parent1);
      return children;
   }
     
   public static void shuffle(List<Board> list)
   {
      Random random = new Random();
      for(int i = population-1; i > 0; i--){
         int index = random.nextInt(i+1);
         Board temp = list.get(index);
         list.set(index, list.get(i));
         list.set(i, temp);
      }
   }

   public static void sort(List<Board> list)
   {
      Collections.sort(list, new Comparator<Board>()
      {
         @Override
            public int compare(Board v1, Board v2) {
            return v1.compareTo(v2);
         }
      });
   }
}
     
class Board /*implements Comparable<Board>*/
{
   static final double MUTATION_P = 0.01;
     
   private int size;
   public int[] array;
   public float fitness;
       
   public Board(int size)
   {
      this.size = size;
      this.array = new int[size];
      /*for(int i = 0; i < size; i++)
        array[i] = (int)(Math.random() * size);
        mutate();*/
      for(int i = 0; i < size; i++)
         this.array[i] = i; //array = {1, 2, 3, 4 ... }
      shuffle();
      this.fitness = calcFitness();
   }

   public Board(int size, Board parent1, Board parent2)
   {
      this.size = size;
      array = new int[size];

      Board best = (parent1.fitness > parent2.fitness) ? parent1 : parent2;
      Board worst = (parent1.fitness < parent2.fitness) ? parent1 : parent2;
      
      double p = best.fitness / (worst.fitness + best.fitness);
      
      for(int i = 0; i < size; i++)
         {
            if(Math.random() < p)
               array[i] = best.array[i];
            else
               array[i] = worst.array[i];
         }
      mutate();
      this.fitness = calcFitness();
   }

   public Board(int size, Board parent1, Board parent2, Board parent3)
   {
      this.size = size;
      array = new int[size];
      
      for(int i = 0; i < size; i++)
         {
            if (parent1.array[i] == parent2.array[i])
               array[i] = parent1.array[i];
            else
               array[i] = parent3.array[i];
         }
      mutate();
      this.fitness = calcFitness();
   }
   
   public Board(int size, Board parent1, Board parent2, int splitPoint)
   {
      this.size = size;
      array = new int[size];
      for(int i = 0; i < size; i++)
      {
         if(i <= splitPoint)
            array[i] = parent1.array[i];
         else
            array[i] = parent2.array[i];
      }
      mutate();
      this.fitness = calcFitness();
   }
     
   private void mutate()
   {
      if(Math.random() < MUTATION_P)
      {
         int a1 = (int)(Math.random() * size);
         int a2 = (int)(Math.random() * (size-1));
         if(a1 == a2) a2++;
         //swap indeces a1 and a2
         int tmp = array[a1];
         array[a1] = array[a2];
         array[a2] = tmp;
      }
      
      // if(Math.random() < MUTATION_P)
      // {
      //    int tmp = (int)(Math.random() * size);
      //    array[tmp] = (int)(Math.random() * size);
      //    }
   }
       
   /* smaller fitness is better */
   private float calcFitness(){
      int result = 0;
      for(int i = 0; i < size; i++)
         result += countEast(i) + countSouthEast(i) + countNorthEast(i);
      return 1/result;
   }

   public float calcFitness2(){
      int f = 0;
      for(int i = 0; i < size; i++)
      {
         for(int j = 0; j < size; j++)
         {
            if (i == j) break;
            
            if(i != j && Math.abs(i-j) == Math.abs(array[i]-array[j]) )
            {
               f++;
            }
            if (i != j && array[i] == array[j])
            {
               f++;
            }
         }
      }
      return 1/f;
   }
   //count the queens horizontally left (east) of the current piece
   private int countEast(int start){
      int result = 0;
      for(int i = start+1; i < size; i++)
         if (array[i] == array[start])
            result++;                        
      return result;
   }
   private int countNorthEast(int start){
      int result = 0;
      for(int i = start+1; i < size; i++)
         if (array[i] == array[start]-i+start)
            result++;
      return result;
   }
   private int countSouthEast(int start){
      int result = 0;
      for(int i = start+1; i < size; i++)
         if (array[i] == array[start]+i-start)
            result++;
      return result;
   }

   /* simple knuth shuffle */
   private void shuffle(){
      Random random = new Random();
      for(int i = size-1; i > 0; i--){
         int index = random.nextInt(i+1);
         int temp = array[index];
         array[index] = array[i];
         array[i] = temp;
      }
   }
     
   public int compareTo(Board v2) {
      return this.fitness - v2.fitness;
   }
     
   public String toString(){
      String grid = "";
      String prefix = " ";
      for(int row = 0; row < size; row++){
         prefix += array[row] + " ";
         grid += "|";
         for(int col = 0; col < size; col++){
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
      for(int col = 0; col < size; col++)
         s += array[col];
      return s;
   }
}
