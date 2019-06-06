import java.net.URL;

import javazoom.jl.player.Player;

/**
 * Created by rain on 2017/2/24.
 */
public class PlayerMusic {

	private String filename;

	private Player player;

	public PlayerMusic(String filename) {
		this.filename = filename;
	}

	public void play() {
		try {
			URL url = new URL(filename);
			//                BufferedInputStream buffer = new BufferedInputStream(
			//                        new FileInputStream(url.openConnection().toString()));
			//                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			player = new Player(url.openStream());
			player.play();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
