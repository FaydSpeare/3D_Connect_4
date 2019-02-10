import java.util.*;

public class Node implements Comparable<Node> {

    private static SplittableRandom rand = new SplittableRandom();

    // Value
    public double wins;
    public double visits;

    // Move record
    public List<Integer> MOVES;
    private List<Integer> toExplore;

    // Children
    private List<Node> children;

    // Parent
    public Node parent;
    public int lastMove;

    // Terminal Nodes
    private boolean terminal = false;
    public int terminalValue;

    // Board Representation
    public Board b;

    public Node(Node parent, int move){
        this.parent = parent;
        this.lastMove = move;

        this.MOVES = new ArrayList<>();
        for(int i = 0; i < parent.MOVES.size(); i++){
            int m = parent.MOVES.get(i);
            if(m != move){
                this.MOVES.add(m);
            }
        }

        this.toExplore = new ArrayList<>(this.MOVES);

        this.b = new Board(parent.b);

        this.children = new ArrayList<>();
    }

    public void update(int value){
        this.wins += value;
        this.visits++;

        if(parent != null){
            parent.update(value);
        }
    }

    public Node selectChild(){
        Node best = null;
        double bestUCT = children.get(0).uct();

        if(!b.toMove){
            for(Node child: children){
                double uct = child.uct();
                if(uct <= bestUCT){
                    bestUCT = uct;
                    best = child;
                }
            }
        } else {
            for(Node child: children){
                double uct = child.uct();
                if(uct >= bestUCT){
                    bestUCT = uct;
                    best = child;
                }
            }
        }

        return best;
    }

    private double uct(){
        double expand = Math.sqrt(3*Math.log(parent.visits)/this.visits);
        if(b.toMove){
            expand *= -1;
        }
        return this.wins/this.visits + expand;
    }

    public boolean isExpandable(){
        return !toExplore.isEmpty();
    }

    public Node makeMove(int move){
        Node creation = new Node(this, move);

        if(move < 48) {
            int newMove = move + 16;
            creation.toExplore.add(newMove);
            creation.MOVES.add(newMove);
        }

        //make move on bit board
        if(this.b.toMove){
            creation.b.white |= (1L << move);
        } else {
            creation.b.black |= (1L << move);
        }

        // change turn
        creation.b.toMove = !this.b.toMove;

        this.children.add(creation);
        this.toExplore.remove(toExplore.size()-1);

        return creation;
    }

    public int getRandomMove(){
        int index = rand.nextInt(toExplore.size());
        int move = toExplore.get(index);
        toExplore.set(index, toExplore.get(toExplore.size()-1));
        return move;
    }

    public Node(long white, long black){
        this.b = new Board(white, black, true);

        this.parent = null;

        MOVES = new ArrayList<>();

        MOVES = Game.getMoves(white, black);

        toExplore = new ArrayList<>(MOVES);


        this.children = new ArrayList<>();

    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public int getTerminalValue() {
        return terminalValue;
    }

    public boolean isTerminal() {
        return terminal;
    }

    private int childrenTerminalSum;
    public int terminalDepth;

    public void setTerminalValue(int terminalValue, int depth, UCT u) {
        this.terminalValue = terminalValue;
        this.terminalDepth = depth;
        this.terminal = true;

        boolean top = true;



        if(parent != null){
            depth++;

            if(parent.b.toMove){
                if(terminalValue == 2) {
                    parent.setTerminalValue(terminalValue, depth, u);
                    top = false;

                }
                else if(terminalValue == -2){
                    parent.childrenTerminalSum += terminalValue;
                    if(parent.childrenTerminalSum / parent.children.size() == -2){
                        parent.setTerminalValue(terminalValue, depth, u);
                        top = false;
                    }
                }
            }
            else {
                if(terminalValue == -2) {
                    parent.setTerminalValue(terminalValue, depth, u);
                    top = false;
                }
                else if(terminalValue == 2){
                    parent.childrenTerminalSum += terminalValue;
                    if(parent.childrenTerminalSum / parent.children.size() == 2){
                        parent.setTerminalValue(terminalValue, depth, u);
                        top = false;
                    }
                }
            }
        }



    }

    public List<Node> getChildren() {
        return children;
    }

    public int getLastMove(){
        return lastMove;
    }

    public Node getChild(int move){
        for(Node child: children){
            if(child.lastMove == move){
                return child;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Node o) {
        return (int)(((o.wins+1)/o.visits) - ((this.wins+1)/this.visits));
    }
}
