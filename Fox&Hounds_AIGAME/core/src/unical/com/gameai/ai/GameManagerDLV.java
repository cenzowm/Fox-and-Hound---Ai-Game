package unical.com.gameai.ai;

import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv.desktop.DLVDesktopService;
import unical.com.gameai.logicgraphics.Fox;
import unical.com.gameai.logicgraphics.Hound;
import unical.com.gameai.logicgraphics.MyGdxGame;

public class GameManagerDLV {

	/* EmbASP Elements */
	private Handler handler;
	private InputProgram facts;
	private InputProgram encoding;
	private String encodingFile = "encodings/Fox_encoding";
	private MyGdxGame gdxGame;

	public GameManagerDLV(MyGdxGame game) {
		gdxGame = game;
		handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
		encoding = new ASPInputProgram();
		facts = new ASPInputProgram();
		OptionDescriptor optionDescriptor1 = new OptionDescriptor();
		optionDescriptor1.setOptions("-nofacts");
		optionDescriptor1.setOptions("-FDsingle");
		optionDescriptor1.setOptions("-nofinitecheck");

		handler.addOption(optionDescriptor1);
	}

	public void muoviPezzo() throws Exception {

		// add facts to dlv
		handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
		encoding = new ASPInputProgram();
		facts = new ASPInputProgram();
		OptionDescriptor optionDescriptor1 = new OptionDescriptor();
		optionDescriptor1.setOptions("-nofacts");
		optionDescriptor1.setOptions("-nofinitecheck");

		handler.addOption(optionDescriptor1);
		int idPecora = 0;
		for (int x = 0; x < MyGdxGame.BOARD_WIDTH; x++) {
			for (int y = 0; y < MyGdxGame.BOARD_HEIGHT; y++) {
				if (MyGdxGame.matrix[x][y] == 1) {

					facts.addObjectInput(new Fox(x, y, 0));
				} else if (MyGdxGame.matrix[x][y] == 2) {
					idPecora++;
					facts.addObjectInput(new Hound(0, x, y, idPecora));
				}
			}
		}

		// print facts
		System.out.println("################ Fatti ################");
		String[] fatti = facts.getPrograms().split(" ");
		for (int i = 0; i < fatti.length; i++)
			System.out.println(fatti[i]);

		handler.addProgram(facts);
		encoding.addFilesPath(encodingFile);
		handler.addProgram(encoding);

		// taking answerset from dlv
		Output o = handler.startSync();
		AnswerSets answers = (AnswerSets) o;

		for (AnswerSet a : answers.getAnswersets()) {
			System.out.println("################ Answer set ################");
			System.out.println(a.toString());
			try {
				for (Object obj : a.getAtoms()) {

					if (!(obj instanceof Fox))
						continue;

					Fox fox = (Fox) obj;
					// previousFox = null;
					if (fox.getT() == 1) {
						if (fox.moveTo(fox.getX(), fox.getY(), MyGdxGame.getPieces())) {
							gdxGame.touchDown(52 + fox.getX() * 50, 582 - 50 * fox.getY(), 0, 0);

						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
