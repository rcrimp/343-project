import java.util.*;
import java.lang.Math;
     
public class Nqueen{
   /* settings, with default values */
   public static int BOARD_SIZE = 8;
   public static int POPULATION = 50;
   public static int MAX_GENERATIONS = 60;
   public static int MAX_RESTARTS = 10;
   public static double KEEP_RATIO = 0.25;
   public static double MUTATION_P = 0.001;

   public static List<Board> boards;

   /* main */
   public static void main(String[] args){
      int c = 0;
      while(c < args.length) { //read args in pairs
         String temp = args[c++];
         if(c < args.length)
            parseArg(temp, args[c++]); //parse the pair of args
      }
      System.err.println("Begining the Genetic Algorithm with the following settings:" +
                         "\nboard size:  " + BOARD_SIZE +
                         "\npopulation:  " + POPULATION +
                         "\ngenerations: " + MAX_GENERATIONS +
                         "\nrestarts:    " + MAX_RESTARTS +
                         "\nkeep ratio:  " + KEEP_RATIO +
                         "\nmutation p:  " + MUTATION_P + "\n");
 
      /**************************************************/

      int generations = 0;
      int resets = 0;
      
      boards = new ArrayList<Board>();
      for(int i = 0; i < POPULATION; i++)
         boards.add(new Board()); // fill the list with random elements
      
      while(getBest().fitness > 0){
         /* reorder the list before breeding, optional */
          sort();
         // shuffle();

         /* breed the elements */
          breedRandom();
         // breedPairs();

         /* if we exceed the generation limit restart */
         if(generations++ > MAX_GENERATIONS && resets < 10){ 
            resetAll();
            generations = 0;
            resets++;
         }
      }

      /* print the best board when done */
      System.err.println(getBest().toStringPretty());
      System.err.print("generations: ");
      System.out.println((resets*MAX_GENERATIONS + generations));
   }

   /* resets all elements in the population back to random elements */
   public static void resetAll(){
      for(int i = 0; i < POPULATION; i++)
         boards.set(i, new Board());
   }

   /* chooses successive pairs of elements in the list to breed */
   public static void breedPairs(){
      int numParents = Math.max(2,(int)(KEEP_RATIO*POPULATION));
      int numChildren = POPULATION - numParents;
      for(int i = 0; i < numChildren; i++){
         boards.set(numParents + i,
                    crossover_weighted_split(i % numParents, (i+1) % numParents));
      }
   }
   /* chooses parents at random, then breeds the with random crossovers */
   public static void breedRandom(){
      int numParents = Math.max(2,(int)(KEEP_RATIO*POPULATION));
      int numChildren = POPULATION - numParents;
      int r1, r2;
      for(int i = 0; i < numChildren; i++){
         r1 = (int)(Math.random()*numParents);
         r2 = (int)(Math.random()*numParents);
         while (r1 == r2)
            r2 = (int)(Math.random()*numParents);
         boards.set(numParents + i,
                    crossover_uniform(r1, r2));
      }
   }

   /* takes the indices of two parents, and breeds them with a single random split point  */
   public static Board crossover_random_split(int p1, int p2){
      Board parent1 = boards.get(p1);
      Board parent2 = boards.get(p2);
      //ensure we aren't breeding duplicates
      if(p1 != p2 && parent1.sameBoardAs(parent2))
         return new Board();
      int splitPoint = (int)(Math.random() * (BOARD_SIZE-1));
      return new Board(parent1, parent2, splitPoint);
   }
   /* takes the indices of two parents, and breeds them with a splitpoint derived from the parents fitness */
   public static Board crossover_weighted_split(int p1, int p2){
      Board parent1 = boards.get(p1);
      Board parent2 = boards.get(p2);
      //ensure we aren't breeding duplicates
      if(p1 != p2 && parent1.sameBoardAs(parent2))
         return new Board(); 
      //int splitPoint = (int)((Math.min(parent1.fitness, parent2.fitness) /
      //                       (double)Math.max(parent1.fitness, parent2.fitness)) * (BOARD_SIZE-1));
      int splitPoint = (parent2.fitness / (parent2.fitness + parent2.fitness)) * (BOARD_SIZE-1);
      return new Board(parent1, parent2, splitPoint);
   }
   /* takes the indices of two parents, and breeds them a uniform crossover method */
   public static Board crossover_uniform(int p1, int p2){
      Board parent1 = boards.get(p1);
      Board parent2 = boards.get(p2);
      //ensure we aren't breeding duplicates
      if(p1 != p2 && parent1.sameBoardAs(parent2))
         return new Board(); 
      return new Board(parent1, parent2, Math.random());
   }

   /* finds the most fit elements and returns it */
   public static Board getBest(){
      Board best = boards.get(0);
      for(int i = 1; i < POPULATION; i+=1)
         if(boards.get(i).fitness < best.fitness)
            best = boards.get(i);
      return best;
   }

   /* shuffles the list of boards, very simple shuffle*/
   public static void shuffle(){
      Random random = new Random();
      for(int i = POPULATION-1; i > 0; i--){
         int index = random.nextInt(i+1);
         Board temp = boards.get(index);  //swap(index, i)
         boards.set(index, boards.get(i));
         boards.set(i, temp);
      }
   }
   /* sorts the list of boards */
   public static void sort(){
      Collections.sort(boards, new Comparator<Board>(){
            @Override
               public int compare(Board v1, Board v2) {
               return v1.compareTo(v2);
            }
         });
   }

   /* parses the args, stupidly simple lol */
   public static void parseArg(String type, String value){
      switch (type.toLowerCase().charAt(0)){
      case 'b':
         BOARD_SIZE = Math.max(1,stringToInt(value));
         break;
      case 'p':
         POPULATION = Math.max(1,stringToInt(value));
         break;
      case 'g':
         MAX_GENERATIONS = Math.max(1,stringToInt(value));
         break;
      case 'r':
         MAX_RESTARTS = Math.max(0,stringToInt(value));
         break;
      case 'k':
         KEEP_RATIO = stringToDouble(value);
         if(KEEP_RATIO > 1) KEEP_RATIO = 1;
         break;
      case 'm':
         MUTATION_P = stringToDouble(value);
         if(MUTATION_P < 0f) MUTATION_P = 0f;
         if(MUTATION_P > 1f) MUTATION_P = 1f;
         break;
      default:
         System.err.println("ERR: unkown arg: " + type);
      }
      
   }
   public static int stringToInt(String s){
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException e) {
         System.err.println("ERR: Argument" + s + " must be an integer.");
         System.exit(1);
      }
      return 0;
   }
   public static double stringToDouble(String s){
      try {
         return Double.parseDouble(s);
      } catch (NumberFormatException e) {
         System.err.println("Argument" + s + " must be a double.");
         System.exit(1);
      }
      return 0;
   }
}

/* a Board object describes a particular board permutation using an array
 * 
 * 
 */
class Board {
   public int[] array; //the board permutation is stored this array
   public int fitness; //the fitness

   /* this constructor makes a random board permutation */
   public Board(){
      array = new int[Nqueen.BOARD_SIZE];
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++)
         array[i] = i; //array = {1, 2, .. 8}
      shuffle();
      fitness = calcFitness();
   }
   /* this constructor creates a board from 2 other 'parent' boards
      uses single point crossover point */
   public Board(Board parent1, Board parent2, int splitPoint){    
      array = new int[Nqueen.BOARD_SIZE];
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++){
         if(i <= splitPoint)
            array[i] = parent1.array[i];
         else
            array[i] = parent2.array[i];
      }
      mutate();
      fitness = calcFitness();
   }
   /* this constructor creates a board from 2 other 'parent' boards
      uses a uniform distribution crossover method */
   public Board(Board best, Board worst, double p){
      array = new int[Nqueen.BOARD_SIZE];      
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++){
         if(Math.random() < p)
            array[i] = best.array[i];
         else
            array[i] = worst.array[i];
      }
      mutate();
      fitness = calcFitness();
   }                  

   /* mutate the board */
   private void mutate(){
      for(int n = 0; n < Nqueen.BOARD_SIZE; n++){
         if(Math.random() < Nqueen.MUTATION_P){
            array[n] = (int)(Math.random() * Nqueen.BOARD_SIZE);
         }
      }
   }
       
   /* calculates the fitness as a count of un-orderd attacking pairs of queens
      best fitness = 0
      worst fitness = 8 choose 2 = 28
   */
   private int calcFitness(){
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

   /* shuffles the array, simple knuth shuffle */
   private void shuffle(){
      Random random = new Random();
      for(int i = Nqueen.BOARD_SIZE-1; i > 0; i--){
         int index = random.nextInt(i+1);
         int temp = array[index];
         array[index] = array[i];
         array[i] = temp;
      }
   }

   /* compareTo required for sort */
   public int compareTo(Board v2){
      double tmp = this.fitness - v2.fitness;
      return (tmp > 0) ? 1 : (tmp < 0) ? -1 : 0;
   }

   /* used to determine if this is identical to another board */
   public boolean sameBoardAs(Board board2){
      int count = 0;
      for(int i = 0; i < Nqueen.BOARD_SIZE; i++)
         if (array[i] == board2.array[i])
            count++;
      return (count == Nqueen.BOARD_SIZE);
   }

   /* nice string representation of the board, in a nice grid */
   public String toStringPretty(){
      String grid = "";
      for(int col = 0; col < Nqueen.BOARD_SIZE; col++){
         grid += "|";
         for(int row = 0; row < Nqueen.BOARD_SIZE; row++){
            if(array[row] == col)
               grid += "Q|";
            else
               grid += "_|";
         }
         if(col+1 < Nqueen.BOARD_SIZE)
            grid += "\n";
      }
      return toString() + "\n" + grid + "\nfitness: " + fitness;
   }

   /* simple string representation of the board */
   public String toString(){
      String s = "[";
      for(int col = 0; col < Nqueen.BOARD_SIZE; col++){
         s += (array[col]+1);
         if(col+1 < Nqueen.BOARD_SIZE)
            s += ",";
      }
      return s + "]";
   }
}
