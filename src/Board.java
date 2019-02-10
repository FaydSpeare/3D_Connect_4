import java.io.Serializable;

public class Board implements Serializable {

    public long white;
    public long black;
    public boolean toMove;

    public Board(long white, long black, boolean toMove){
        this.white = white;
        this.black = black;
        this.toMove = toMove;
    }

    public Board(Board board){
        this.white = board.white;
        this.black = board.black;
        this.toMove = board.toMove;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(white)+31*Long.hashCode(black)+7*Boolean.hashCode(toMove);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Board){
            Board b = (Board)obj;
            return (white ^ b.white) == 0L && (black ^ b.black) == 0L && toMove == b.toMove;
        }
        return false;
    }
}
