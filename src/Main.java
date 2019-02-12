import java.util.*;

public class Main {

    public static void main(String[] args){

        if(args.length != 2){
            System.err.println("Two Arguments Required:");
            System.err.println("       1: true if A.I. is to play second, otherwise false");
            System.err.println("       2: Amount of thinking time per move for A.I.");
            System.exit(1);
        }

        boolean goFirst;
        goFirst = Boolean.parseBoolean(args[0]);

        double thinkingTime = 0;
        try {
            thinkingTime = Double.parseDouble(args[1]);
        } catch(NumberFormatException e){
            System.err.println("Argument 2 illegal");
            System.exit(1);
        }

        Game game = new Game();
        UCT uct = new UCT(game);

        boolean first = true;
        int move1 = -1;
        int move2 = -1;

        if(goFirst){
            game.printBoard();
            Scanner in = new Scanner(System.in);
            System.out.println("Enter a move: ");
            move2 = in.nextInt();
            game.makeMove(move2);
        }

        while(!game.isTerminal(game.white, game.black)){
            if(first){
                move1 = uct.runUCT(thinkingTime, null);
                first = false;
            } else {
                move1 = uct.runUCT(thinkingTime, new int[]{move1, move2});
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

