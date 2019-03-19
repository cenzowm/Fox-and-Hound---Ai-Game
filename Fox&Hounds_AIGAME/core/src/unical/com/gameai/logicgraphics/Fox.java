package unical.com.gameai.logicgraphics;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("fox")
public class Fox extends Piece {

	public static final int MAX_MOVE_DIST = 1;

	@Param(0)
	private int x = getLocationX();
	@Param(1)
	private int y = getLocationY();
	@Param(2)
	private int t;

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

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

	public Fox(int x, int y, int t) {
		this.x = x;
		this.y = y;
		this.t = t; 
	}

	public Fox() {
		super();
		setTexture(getDefaultTexture());
	}

	/**
	 * The "Fox" is allowed to move one step at a time to any diagonal direction
	 */
	@Override
	public boolean isAllowedToMoveTo(int x, int y, List<Piece> pieces) {
		if (super.isAllowedToMoveTo(x, y, pieces) && Math.abs(getLocationX() - x) == MAX_MOVE_DIST
				&& Math.abs(getLocationY() - y) == MAX_MOVE_DIST) {
			return true;
		}
		return false;
	}

	public Texture getDefaultTexture() {
		return new Texture("fox.png");
	}

}
