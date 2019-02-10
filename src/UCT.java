public class UCT {

    private Game game;
    private Node root;

    public UCT(Game game){
        this.game = game;
    }

    public int runUCT(double allowedTime, int[] moves){

        int nodes = 0;
        if(moves == null){
            root = new Node(game.white, game.black);
        } else {
            for(int i = 0; i < moves.length; i++){
                root = root.getChild(moves[i]);
            }
            if(root == null){
                root = new Node(game.white, game.black);
            } else {
                root.parent = null;
                nodes = (int)root.visits;
            }

        }

        double time = System.currentTimeMillis();
        double duration = 0;
        int it = 0;

        int maxDepth = 0;
        while( duration <= allowedTime){
            it++; // O(1)
            duration = (System.currentTimeMillis() - time)/1000; // O(1)
            Node node = root; // O(1)

            int depth = 0;
            while(!node.isExpandable()){ // O(1)
                node = node.selectChild(); // O(n) n = 16
                depth++;
                if(node.isTerminal()){ // if is terminal O(1)
                    break;
                }
            }
            if(depth > maxDepth){
                maxDepth = depth;
            }

            if(node != root){
                if(node.isTerminal()){ // recall value O(1)
                    node.update(node.getTerminalValue()); // O(1)
                    continue;
                }
            }

            Node expanded = node.makeMove(node.getRandomMove()); // get is O(n)

            int result = game.isTerminal_lastMove(expanded.b.white, expanded.b.black, expanded.b.toMove, expanded
                    .lastMove);

            if(result != -1){
                expanded.setTerminal(true);
                expanded.setTerminalValue(result, 1, this);
            } else {
                result = game.simulate(expanded.b.white, expanded.b.black, expanded.b.toMove, expanded.lastMove,
                        expanded
                        .MOVES);
            }
            expanded.update(result);

        }

        //// PRINTS OUT TURN INFORMATION ///

        System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-");
        System.out.println("\nNodes: "+it+"  Max-depth: "+maxDepth+"  Nodes/sec: "+((nodes+it)/allowedTime) + "  " +
                "Duration: "+allowedTime);
        System.out.println("----------------------------------------------------------------------------------");

        Node child = root.getChildren().get(0);
        double largest = child.wins/child.visits;
        Node bestChild = null;
        for(Node c: root.getChildren()){

            double q = c.wins/c.visits;
            System.out.println(String.format("%-" + 4 + "s", c.getLastMove()+":")+ " q-value: " + String.format("%-8.3f",
                    q) + "~ " + "visits: "+String.format("%-" + 10 + "s", c.visits)+", wins: "+String.format("%-" + 10 +
                    "s", c.wins)+"" + " " + ", Tval: "+String.format("%-" + 4 + "s", c.terminalValue)+ ", Tdepth: "+c.terminalDepth);

            if(q >= largest){
                largest = q;
                bestChild = c;
            }
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println(String.format("%-" + 4 + "s", bestChild.getLastMove()+":")+ " q-value: " + String.format
                ("%-8.3f", bestChild.wins/bestChild.visits));
        System.out.println("\n#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-");

        /////////////////////////////////////

        return bestChild.getLastMove();
    }
}
