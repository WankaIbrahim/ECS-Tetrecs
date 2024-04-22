package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The MultiplayerScene
 */
public class MultiplayerScene extends ChallengeScene{

  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

  /**
   * The communicator associated with the multiplayer scene
   */
  private final Communicator communicator;

  /**
   * Create a new MultiplayerScene
   * @param gameWindow The game window associated with the multiplayer scene
   * @param communicator The communicator associated with the multiplayer scene
   */
  public MultiplayerScene(GameWindow gameWindow, Communicator communicator) {
    super(gameWindow);
    this.communicator = communicator;
    logger.info(communicator);
  }

  /**
   * Set up the multiplayer game
   */
  @Override
  public void setupGame() {
    game = new MultiplayerGame(5,5, communicator);
  }
}
