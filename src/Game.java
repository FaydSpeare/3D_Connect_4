import java.util.*;

public class Game {

    public long white;
    public long black;
    public boolean toMove;

    private final int[][] fours = new int[][]{
            {0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}, // h1
            {0, 5, 10, 15}, {3, 6, 9, 12}, // d1
            {0, 4, 8, 12}, {1, 5, 9, 13}, {2, 6, 10, 14}, {3, 7, 11, 15}, // v1

            {16, 17, 18, 19}, {20, 21, 22, 23}, {24, 25, 26, 27}, {28, 29, 30, 31},
            {16, 21, 26, 31}, {19, 22, 25, 28},
            {16, 20, 24, 28}, {17, 21, 25, 29}, {18, 22, 26, 30}, {19, 23, 27, 31},

            {32, 33, 34, 35}, {36, 37, 38, 39}, {40, 41, 42, 43}, {44, 45, 46, 47},
            {32, 37, 42, 47}, {35, 38, 41, 44},
            {32, 36, 40, 44}, {33, 37, 41, 45}, {34, 38, 42, 46}, {35, 39, 43, 47},

            {48, 49, 50, 51}, {52, 53, 54, 55}, {56, 57, 58, 59}, {60, 61, 62, 63},
            {48, 53, 58, 63}, {51, 54, 57, 60},
            {48, 52, 56, 60}, {49, 53, 57, 61}, {50, 54, 58, 62}, {51, 55, 59, 63},

            {0, 16, 32, 48}, {1, 17, 33, 49}, {2, 18, 34, 50}, {3, 19, 35, 51},
            {4, 20, 36, 52}, {5, 21, 37, 53}, {6, 22, 38, 54}, {7, 23, 39, 55},
            {8, 24, 40, 56}, {9, 25, 41, 57}, {10, 26, 42, 58}, {11, 27, 43, 59},
            {12, 28, 44, 60}, {13, 29, 45, 61}, {14, 30, 46, 62}, {15, 31, 47, 63},

            {0, 21, 42, 63}, {15, 26, 37, 48}, {3, 22, 41, 60}, {12, 25, 38, 51},

            {0, 17, 34, 51}, {4, 21, 38, 55}, {8, 25, 42, 59}, {12, 29, 46, 63},
            {3, 18, 33, 48}, {7, 22, 37, 52}, {11, 26, 41, 56}, {15, 30, 45, 60},
            {0, 20, 40, 60}, {1, 21, 41, 61}, {2, 22, 42, 62}, {3, 23, 43, 63},
            {12, 24, 36, 48}, {13, 25, 37, 49}, {14, 26, 38, 50}, {15, 27, 39, 51}};

    private Map<Integer, int[][]> checks;

    private SplittableRandom rand;

    public Game(int i){
        this.white = 0x0000_0000_0000_0000L;
        this.black = 0x0000_0000_0000_0000L;
        this.toMove = true;

        this.rand = new SplittableRandom();
    }

    public Game(){
        this.white = 0x0000_0000_0000_0000L;
        this.black = 0x0000_0000_0000_0000L;
        this.toMove = true;

        this.rand = new SplittableRandom();
        checks = new HashMap<>();

        List<List<int[]>> split = new ArrayList<>();
        for(int i = 0; i < 64; i++){
            split.add(new ArrayList<>());
        }

        for(int[] array: fours){

            for(int i: array){

                int[] newArray = new int[3];
                int index = 0;
                for(int j = 0; j < 4; j++){
                    if(i != array[j]){
                        newArray[index] = array[j];
                        index++;
                    }
                }
                split.get(i).add(newArray);

            }
        }

        for(int i = 0; i < 64; i++){

            List<int[]> list = split.get(i);

            int[][] array = new int[list.size()][3];

            for(int k = 0; k < list.size(); k++){
                array[k] = list.get(k);
            }

            checks.put(i, array);
        }
    }

    public int isTerminal_lastMove(long white, long black, boolean toMove, int lastMove){

        if(Long.lowestOneBit(~(black | white)) == 0){
            return 0;
        }

        for(int[] array: checks.get(lastMove)){

            // If Black to Move, Check if White won.
            if(!toMove) {
                if ((white & (1L << array[0])) >>> array[0] == 1L) {
                    if ((white & (1L << array[1])) >>> array[1] == 1L) {
                        if ((white & (1L << array[2])) >>> array[2] == 1L) {
                            return 2;
                        }
                    }
                }
            }
            else {
                if ((black & (1L << array[0])) >>> array[0] == 1L) {
                    if ((black & (1L << array[1])) >>> array[1] == 1L) {
                        if ((black & (1L << array[2])) >>> array[2] == 1L) {
                            return -2;
                        }
                    }
                }
            }
        }


        return -1;
    }

    public int simulate(long white, long black, boolean toMove, int lastMove, List<Integer> moves){

        List<Integer> movesCopy = new ArrayList<>(moves);
        int result;

        while((result = isTerminal_lastMove(white, black, toMove, lastMove)) == -1){


            int randIndex = rand.nextInt(movesCopy.size());
            int randMove = movesCopy.get(randIndex);
            lastMove = randMove;

            //trickery
            movesCopy.set(randIndex, movesCopy.get(movesCopy.size()-1));
            //trickery

            if(toMove){
                white |= (1L << randMove);
            } else {
                black |= (1L << randMove);
            }

            toMove = !toMove;

            movesCopy.remove(movesCopy.size()-1);

            if(randMove < 48){
                movesCopy.add(randMove + 16);
            }
        }

        return result;
    }

    public static List<Integer> getMoves(long white, long black){
        long com = (white | black);
        int size = 16-Long.bitCount(com >>> 48);
        List<Integer> moves = new ArrayList<>(size);
        com = ~com;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 4; j++){
                int shift = i + 16*j;
                if((com & (1L << shift)) >>> shift == 1L){
                    moves.add(shift);
                    break;
                }
            }
        }
        return moves;
    }

    public void makeMove(int move){
        if(toMove){
            this.white |= (1L << move);
        } else {
            this.black |= (1L << move);
        }
        toMove = !toMove;
    }

    public boolean isTerminal(long white, long black){

        if(0L == ~(black | white)){
            return true;
        }

        for(int[] array: fours){

            if ((white & (1L << array[3])) >>> array[3] == 1L) {
                if ((white & (1L << array[2])) >>> array[2] == 1L) {
                    if ((white & (1L << array[1])) >>> array[1] == 1L) {
                        if ((white & (1L << array[0])) >>> array[0] == 1L) {
                            return true;
                        }
                    }
                }
            }
            if ((black & (1L << array[3])) >>> array[3] == 1L) {
                if ((black & (1L << array[2])) >>> array[2] == 1L) {
                    if ((black & (1L << array[1])) >>> array[1] == 1L) {
                        if ((black & (1L << array[0])) >>> array[0] == 1L) {
                            return true;
                        }
                    }
                }
            }
        }


        return false;
    }

    public int result(long white, long black){
        if(Long.lowestOneBit(~(black | white)) == 0){
            return 0;
        }

        for(int[] array: fours){

            if ((white & (1L << array[3])) >>> array[3] == 1L) {
                if ((white & (1L << array[2])) >>> array[2] == 1L) {
                    if ((white & (1L << array[1])) >>> array[1] == 1L) {
                        if ((white & (1L << array[0])) >>> array[0] == 1L) {
                            return 2;
                        }
                    }
                }
            }
            if ((black & (1L << array[3])) >>> array[3] == 1L) {
                if ((black & (1L << array[2])) >>> array[2] == 1L) {
                    if ((black & (1L << array[1])) >>> array[1] == 1L) {
                        if ((black & (1L << array[0])) >>> array[0] == 1L) {
                            return -2;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public void printBoard(){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 64; i++){
            if((white & (1L << i)) >>> i == 1L){
                sb.append("X");
            }
            else if((black & (1L << i)) >>> i == 1L){
                sb.append("O");
            } else {
                sb.append("-");
            }
        }

        String board = sb.toString();

        for(int i = 0; i < 4; i++){
            System.out.println("");
            System.out.println(board.substring(i*16 , i*16 + 4));
            System.out.println(board.substring(i*16 + 4, i*16 + 8));
            System.out.println(board.substring(i*16 + 8, i*16 + 12));
            System.out.println(board.substring(i*16 + 12, i*16 + 16));
        }
    }

    public static void main(String[] args){

        Random rand = new Random();

        double time = System.currentTimeMillis();
        int k = 0;
        for(int i = 0; i < 10000; i++){
            Game game = new Game(1);


            while(!game.isTerminal(game.white, game.black)){
                List<Integer> moves = getMoves(game.white, game.black);
                game.makeMove(moves.get(rand.nextInt(moves.size())));
                k++;
            }

        }
        System.out.println(k/100000);
        System.out.println((System.currentTimeMillis() - time));

        /*

         */
    }

    public List<Integer> get_moves(){
        long com = (white | black);
        int size = 16-Long.bitCount(com >>> 48);
        List<Integer> moves = new ArrayList<>(size);
        com = ~com;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 4; j++){
                int shift = i + 16*j;
                if((com & (1L << shift)) >>> shift == 1L){
                    moves.add(shift);
                    break;
                }
            }
        }
        return moves;
    }


}
