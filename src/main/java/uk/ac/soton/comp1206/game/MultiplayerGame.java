package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * The multiplayer game, which allows two or more users to play together
 */
public class MultiplayerGame extends Game{
  private final Logger logger = LogManager.getLogger(MultiplayerGame.class);

  /**
   * The value of the next piece got from the communicator
   */
  private int generatedPieceValue;

  /**
   * The communicator associated with the multiplayer game
   */
  private final Communicator communicator;


  /**
   * Create a new multiplayer game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows, Communicator communicator) {
    super(cols, rows);
    this.communicator = communicator;
    logger.info(communicator);
    communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));

    currentPiece = spawnPiece();
    nextPiece = spawnPiece();
  }

  /**
   * Initialize the multiplayer game
   */
  @Override
  public void initialiseGame() {
    logger.info("Initialising multiplayer game");    startTimeline();
  }

  /**
   * Update the score
   * @param points The amount by which the score should be incremented
   */
  @Override
  public void updateScore(int points) {
    super.updateScore(points);
    communicator.send("SCORE "+getScore());
  }

  /**
   * Creates a piece based on a given value from the communicator
   * @return The piece created
   */
  @Override
  public GamePiece spawnPiece() {
    communicator.send("PIECE");
    StringBuilder boardState = new StringBuilder();
    for (int gridY = 0; gridY < getCols(); gridY++) {
      for (int gridX = 0; gridX < getRows(); gridX++) {
        boardState.append(" ").append(getGrid().get(gridX, gridY));
      }
    }
    communicator.send("BOARD"+boardState);

    return GamePiece.createPiece(generatedPieceValue);
  }

  /**
   * Handles the receiving of communications from the communicator
   * @param communication The communication received
   */
  private void receiveMessage(String communication){
    logger.info(communication);
    if (communication.split(" ")[0].equals("PIECE")) {

      String message = communication.replaceFirst("PIECE ", "");
      generatedPieceValue = Integer.parseInt(message);
    }
  }

  /**
   * Handles the game looping when the player has run out of time
   */
  @Override
  public void gameLoop() {
    super.gameLoop();
    if(getLives()<0) communicator.send("DIE");
    communicator.send("LIVES "+getLives());
  }


}
