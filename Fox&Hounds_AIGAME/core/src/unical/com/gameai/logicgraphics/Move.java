package unical.com.gameai.logicgraphics;

public class Move {

    private int fromX_;
    private int fromY_;

    private int toX_;
    private int toY_;

    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX_ = fromX;
        this.fromY_ = fromY;
        this.toX_ = toX;
        this.toY_ = toY;
    }
    public Move() {
		// TODO Auto-generated constructor stub
	}


    public int getFromX() {
        return fromX_;
    }

    public int getFromY() {
        return fromY_;
    }

    public int getToX() {
        return toX_;
    }

    public int getToY() {
        return toY_;
    }

    @Override
    public String toString() {
        return "Move{" +
                "fromX=" + fromX_ +
                ", fromY=" + fromY_ +
                ", toX=" + toX_ +
                ", toY=" + toY_ +
                '}';
    }
}
