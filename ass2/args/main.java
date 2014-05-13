public class main{

   public static int POPULATION = 100;
   public static int MAX_GENERATIONS = 100;
   public static int MAX_RESTARTS = 10;
   public static double KEEP_RATIO = 0.20;
   public static double MUTATION_P = 0.05;
   
   public static void main(String[] args){
      

      System.out.println(POPULATION);
      System.out.println(MAX_GENERATIONS);
      System.out.println(MAX_RESTARTS);
      System.out.println(KEEP_RATIO);
      System.out.println(MUTATION_P);
   }

   public static void parseArg(String type, String value){
      switch (type.toLowerCase().charAt(0)){
      case 'p':
         POPULATION = stringToInt(value);
         break;
      case 'g':
         MAX_GENERATIONS = stringToInt(value);
         break;
      case 'r':
         MAX_RESTARTS = stringToInt(value);
         break;
      case 'k':
         KEEP_RATIO = stringToDouble(value);
         break;
      case 'm':
         MUTATION_P = stringToDouble(value);
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
