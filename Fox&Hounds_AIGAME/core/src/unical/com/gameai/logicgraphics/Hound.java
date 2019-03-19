package unical.com.gameai.logicgraphics;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("hound")
public class Hound extends Piece {

	private static final int MAX_MOVE_DIST = 1;
	@Param(0)
	private int t;
	
	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	@Param(1)
	private int x = getLocationX();
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Param(2)
	private int y = getLocationY();
	@Param(3)
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Hound(int t,int x, int y, int id) {
		this.t = t;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public Hound() {
		super();
		setTexture(getDefaultTexture());
	}

	/**
	 * The "Hound" is allowed to move one step at a time to any diagonal direction
	 */
	@Override
	public boolean isAllowedToMoveTo(int x, int y, List<Piece> pieces) {
		if (super.isAllowedToMoveTo(x, y, pieces) && Math.abs(getLocationX() - x) == MAX_MOVE_DIST
				&& Math.abs(getLocationY() - y) == MAX_MOVE_DIST && getLocationY() > y) {
			return true;
		}
		return false;
	}

	@Override
	public Texture getDefaultTexture() {
		return new Texture("hound.png");
	}

}
