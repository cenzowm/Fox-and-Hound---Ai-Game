package unical.com.gameai.logicgraphics;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import unical.com.gameai.ai.GameManagerDLV;

public class MyGdxGame implements ApplicationListener, InputProcessor {

	public enum Type {
		NONE, FOX, HOUNDS
	}

	// fox starts the game
	private static Type currentTurn_ = Type.FOX;

	public static final int BOARD_WIDTH = 8;
	public static final int BOARD_HEIGHT = 8;
	public static final int SQUARE_SIZE = 50;

	public GameManagerDLV gameManagerDLV;

	final int BOARD_MARGIN_SIDES = 50;
	final int BOARD_MARGIN_TOP_BOTT = 50;

	private int screenHeight_;
	private int screenWidth_;

	private SpriteBatch batch_;
	private OrthographicCamera camera_;

	final Rectangle[][] board_ = new Rectangle[BOARD_WIDTH][BOARD_HEIGHT];

	public static Integer[][] matrix = new Integer[BOARD_WIDTH][BOARD_HEIGHT];

	private Texture redSquare_;
	private Texture blackSquare_;

	private Piece selectedPiece_;

	private static Fox[] foxes_ = new Fox[1];

	private static Hound[] hounds_ = new Hound[4];

	private Type type_;
	private String typeText_;
	private BitmapFont typeFont_;
	private static final String YOUR_TURN_TEXT = "It's your turn!";
	private static final String WAITING_OTHER_TEXT = "FOX AND HOUNDS ";
	private static final String YOU_WIN_TEXT = "You win!";
	private static final String YOU_LOSE_TEXT = "You lose!";
	private String messageText_;
	private BitmapFont messageFont_;

	public MyGdxGame(int screenHeight, int screenWidth) {

		screenHeight_ = screenHeight;
		screenWidth_ = screenWidth;

		typeText_ = "You are playing as the hounds ";

		messageText_ = "";
	}

	@Override
	public void create() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.input.setInputProcessor(this);

		foxes_[0] = new Fox();

		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				if (x == 0 && y == 0) {
					matrix[x][y] = 1;
				} else if ((x == 1 && y == 7) || (x == 3 && y == 7) || (x == 5 && y == 7) || (x == 7 && y == 7)) {
					matrix[x][y] = 2;
				} else {
					matrix[x][y] = 0;
				}
			}

		}

		for (int i = 0; i < hounds_.length; i++) {
			Hound hound = new Hound();
			hound.setLocationY(BOARD_HEIGHT - 1);
			hound.setLocationX(2 * i + 1);
			hounds_[i] = hound;
		}

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {

				Rectangle rect = new Rectangle();
				rect.setSize(SQUARE_SIZE);
				rect.setX(i * SQUARE_SIZE + BOARD_MARGIN_SIDES);
				rect.setY(j * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT);
				board_[i][j] = rect;
			}
		}

		gameManagerDLV = new GameManagerDLV(this);

		camera_ = new OrthographicCamera();
		camera_.setToOrtho(false, 800, 640);

		typeFont_ = new BitmapFont();
		typeFont_.getData().setScale(1.5f);
		typeFont_.setColor(0.0f, 0.0f, 0.0f, 1.0f);

		messageFont_ = new BitmapFont();
		messageFont_.getData().setScale(2.5f);
		messageFont_.setColor(0.0f, 0.0f, 0.0f, 1.0f);

		batch_ = new SpriteBatch();
		redSquare_ = new Texture("red.png");
		blackSquare_ = new Texture("black.png");
		// availableMoveSquare = new Texture("green.png");

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch_.begin();
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				boolean isBlack = (x + y) % 2 == 0;
				Rectangle rect = board_[x][y];
				batch_.draw(isBlack ? blackSquare_ : redSquare_, rect.x, rect.y, rect.width, rect.height);
			}
		}

		for (Piece p : concat(hounds_, foxes_)) {
			drawPiece(p);
		}

		int typeTextY = BOARD_MARGIN_TOP_BOTT + SQUARE_SIZE;
		if (type_ == Type.HOUNDS) {
			typeTextY = BOARD_HEIGHT * SQUARE_SIZE;
		}

		typeFont_.draw(batch_, typeText_, BOARD_WIDTH * SQUARE_SIZE + 2 * BOARD_MARGIN_SIDES, typeTextY);

		if (getCurrentTurn_() != Type.NONE) {
			messageText_ = (getCurrentTurn_() == type_) ? YOUR_TURN_TEXT : WAITING_OTHER_TEXT;
		}

		messageFont_.draw(batch_, messageText_, BOARD_MARGIN_SIDES,
				BOARD_HEIGHT * SQUARE_SIZE + 3.5f * BOARD_MARGIN_SIDES);

		if (getCurrentTurn_() == Type.FOX) {
			selectFox();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			try {
				gameManagerDLV.muoviPezzo();

//				for (Piece p : concat(hounds_, foxes_)) {
//					drawPiece(p);
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		batch_.end();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	private void drawPiece(Piece piece) {
		batch_.draw(piece.getTexture(),
				piece.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
				piece.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
				Piece.PIECE_SIZE, Piece.PIECE_SIZE);
	}

	@Override
	public void dispose() {

		for (Disposable disposable : new Disposable[] { batch_, redSquare_, blackSquare_ }) {
			disposable.dispose();
		}

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// System.out.println("screen x " + screenX + " screen y" + screenY);
		if (getCurrentTurn_() == Type.HOUNDS || getCurrentTurn_() == Type.FOX) {
			Gdx.app.log(this.getClass().getSimpleName(), "in touchdown");

			Piece prevSelection = selectedPiece_;

			selectPiece(null);

			// normalize screenY
			screenY = screenHeight_ - screenY;

			// process pieces
			for (Piece p : (getCurrentTurn_() == Type.HOUNDS) ? hounds_ : foxes_) {
				boolean insideVerticalEdges = (p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT <= screenY
						&& p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT + Piece.PIECE_SIZE >= screenY);
				boolean insideHorizontalEdges = (p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES <= screenX
						&& p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES + Piece.PIECE_SIZE >= screenX);

				if (insideVerticalEdges && insideHorizontalEdges) {

					// If touchdown is inside this piece.
					if (getCurrentTurn_() == Type.HOUNDS)
						selectPiece(p);

				} else {
					if (prevSelection != null) {
						// Clear any selected piece.
						selectPiece(null);
						if (getCurrentTurn_() == Type.FOX && prevSelection instanceof Fox
								|| getCurrentTurn_() == Type.HOUNDS && prevSelection instanceof Hound) {

							int fromX = prevSelection.getLocationX();
							int fromY = prevSelection.getLocationY();

							int locX = (screenX - BOARD_MARGIN_SIDES) / SQUARE_SIZE;
							int locY = (screenY - BOARD_MARGIN_TOP_BOTT) / SQUARE_SIZE;

							List<Piece> pieces = Arrays.asList(concat(foxes_, hounds_));

							if (prevSelection.moveTo(locX, locY, pieces)) {

								try {
									moveIntoMatrix(fromX, fromY, locX, locY);
									movePiece(new Move());
									try {

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									flipTurn();
								}

								flipTurn();
							}
							prevSelection = null;
						}
					}
				}
			}

			return true;
		}

		selectPiece(null);
		return false;
	}

	private void flipTurn() {
		if (!isGameEnded()) {
			if (getCurrentTurn_() == Type.FOX) {
				setCurrentTurn_(Type.HOUNDS);
			} else if (getCurrentTurn_() == Type.HOUNDS) {
				setCurrentTurn_(Type.FOX);
			}
		}
	}

	private boolean isGameEnded() {
		Type winner = Type.NONE;
		boolean gameEnd = false;

		for (Piece p : foxes_) {
			if (p.getLocationY() == BOARD_HEIGHT - 1) {
				winner = Type.FOX;
				gameEnd = true;
				break;
			}
		}

		if (winner == Type.NONE) {
			if (checkHoundsWinCondition()) {
				winner = Type.HOUNDS;
				gameEnd = true;
			}
		}

		if (gameEnd) {
			setCurrentTurn_(Type.NONE);

			if (winner.equals(type_)) {
				messageText_ = YOU_WIN_TEXT;
			} else {
				messageText_ = YOU_LOSE_TEXT;
			}
		}

		return gameEnd;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void selectPiece(Piece piece) {
		if (piece == null) {
			if (selectedPiece_ != null) {
				selectedPiece_.setSelected(false);
				selectedPiece_ = null;
			}
		} else {
			piece.setSelected(true);
			selectedPiece_ = piece;
		}

	}

	public void selectFox() {
		for (Piece p : foxes_) {
			p.setSelected(true);
			selectedPiece_ = p;

		}

	}

	public void movePiece(Move move) throws RemoteException {
		System.out.println("movePiece" + " " + move);
		for (int i = 0; i < foxes_.length; i++) {
			System.out.println(foxes_[i]);
		}
		for (int i = 0; i < hounds_.length; i++) {
			System.out.println(hounds_[i]);
		}

		List<Piece> pieces = getPieces();
		System.out.println(Arrays.toString(pieces.toArray()));

//        Piece selection = null;
//        for (Piece p : pieces) {
//            if (p.getLocationX() == move.getFromX()
//                    && p.getLocationY() == move.getFromY()) {
//                selection = p;
//            }
//        }

		// boolean moveResult = selection.moveTo(move.getToX(), move.getToY(), pieces);

		flipTurn();

	}

	public static Piece[] concat(Piece[] a, Piece[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Piece[] c = new Piece[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

//    public GameService getRemote() {
//        return remote_;
//    }
//
//    public void setRemote(GameService remote) {
//       System.out.println("setRemote");
//        this.remote_ = remote;
//    }
//
//    @Override
//    public void setRemoteService(GameService remote) throws RemoteException {
//        setRemote(remote);
//    }

	public static List<Piece> getPieces() throws RemoteException {
		return Arrays.asList(concat(foxes_, hounds_));
	}

	/**
	 * Checks whether the hounds have won the game. The hounds have won if the fox
	 * (or any of the foxes) can't move anywhere
	 * 
	 * @return true if the hounds have won the game
	 */
	private boolean checkHoundsWinCondition() {
		for (Fox fox : foxes_) {
			for (int i = 0; i < BOARD_HEIGHT; i++) {
				for (int j = 0; j < BOARD_WIDTH; j++) {
					if (fox.isAllowedToMoveTo(j, i, Arrays.asList(concat(foxes_, hounds_)))) {
						return false;
					}
					;
				}
			}
		}
		return true;
	}

	public static void moveIntoMatrix(int fromX, int fromY, int toX, int toY) {
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {

				if (matrix[fromX][fromY] == 2) {
					matrix[fromX][fromY] = 0;
					matrix[toX][toY] = 2;
				} else if (matrix[fromX][fromY] == 1) {
					matrix[fromX][fromY] = 0;
					matrix[toX][toY] = 1;
				}

			}
		}

	}

	public void printMatrix() {

		int colonna = 0;
		int riga = 0;

		while (riga < 8) {
			while (colonna < 8) {
				System.out.print(matrix[riga][colonna]);
				System.out.print(" ");
				colonna++;
			}
			riga++;
			colonna = 0;
			System.out.println();

		}
		System.out.println("---------------------------------------");
	}

	public static Type getCurrentTurn_() {
		return currentTurn_;
	}

	public void setCurrentTurn_(Type currentTurn_) {
		this.currentTurn_ = currentTurn_;
	}

}
