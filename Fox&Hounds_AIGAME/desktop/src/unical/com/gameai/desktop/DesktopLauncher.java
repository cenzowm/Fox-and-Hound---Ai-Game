package unical.com.gameai.desktop;

import java.rmi.RemoteException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import unical.com.gameai.logicgraphics.MyGdxGame;

public class DesktopLauncher {

	public static final String GAME_NAME = "foxgame";

	public static final int SCREEN_HEIGHT = 640;
	public static final int SCREEN_WIDTH = 800;

	public static void main(String[] arg) throws RemoteException {

		MyGdxGame gameInstance = new MyGdxGame(SCREEN_HEIGHT, SCREEN_WIDTH);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "FOX AND HOUNDS - AI GAME PROJECT";
		config.height = SCREEN_HEIGHT;
		config.width = SCREEN_WIDTH;
		new LwjglApplication(gameInstance, config);
	}

}
