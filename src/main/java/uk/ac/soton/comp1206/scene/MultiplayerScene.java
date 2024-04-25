package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
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

  @Override
  public VBox createScore(){
    var scoreBox = new VBox();
    communicator.addListener((message) -> Platform.runLater(() -> {
      if (message.split(" ")[0].equals("SCORES")) {
        logger.info(message);
        //Remove the message header and split it into separate lines
        String scores = message.replaceFirst("SCORES", "");
        String[] lines = scores.split("\n");

        //Go through the message line by line and add it to a VBox to be displayed
        for (String line : lines) {
          String[] parts = line.split(":");
          String name = parts[0];
          int score = Integer.parseInt(parts[1]);
          String lives = parts[2];
          Text scoreItem = new Text(name + ":" + score + ":" + lives);
          scoreItem.getStyleClass().add("heading");
          scoreBox.getChildren().clear();
          scoreBox.getChildren().addAll(scoreItem);
        }
      }else{
        logger.info("Error in setting multiplayer score");
      }


    }));
    communicator.send("SCORES");
    return scoreBox;
  }
}
