import java.util.*;

public class Main {

    public static void main(String[] args){

        Game game = new Game();
        UCT uct = new UCT(game);

        boolean first = true;
        int move1 = -1;
        int move2 = -1;

        while(!game.isTerminal(game.white, game.black)){
            if(first){
                move1 = uct.runUCT(1, null);
                first = false;
            } else {
                move1 = uct.runUCT(5, new int[]{move1, move2});
            }
            game.makeMove(move1);
            game.printBoard();
            Scanner in = new Scanner(System.in);
            System.out.println("Enter a move: ");
            move2 = in.nextInt();
            game.makeMove(move2);
        }
    }
}

