package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.BlockClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game.
 * Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);

  private final Random random = new Random();

  /**
   * Number of rows
   */
  private final int rows;

  /**
   * Number of columns
   */
  private final int cols;

  /**
   * The grid model linked to the game
   */
  private final Grid grid;

  /**
   * The score property
   */
  private final IntegerProperty score = new SimpleIntegerProperty(0);

  /**
   * The level property
   */
  private final IntegerProperty level = new SimpleIntegerProperty(0);

  /**
   * The lives property
   */
  private final IntegerProperty lives = new SimpleIntegerProperty(3);

  /**
   * The multiplier property
   */
  private final IntegerProperty multiplier = new SimpleIntegerProperty(1);

  /**
   * The PieceBoard that holds the current piece
   */
  protected GamePiece currentPiece;

  /**
   * The PieceBoard that holds the next piece
   */
  protected GamePiece nextPiece;

  /**
   * The listener called when the next piece is updated
   */
  private NextPieceListener nextPieceListener;

  /**
   * The listener called when the current piece is updated
   */
  private NextPieceListener currentPieceListener;

  /**
   * The listener called when blocks have been cleared
   */
  private BlockClearedListener blockClearedListener;

  /**
   * The listener called when the game loops
   */
  private GameLoopListener gameLoopListener;

  /**
   * The timeline used to time the game
   */
  private Timeline timeline;




  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
  }

  /**
   * Initialize a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");

    nextPiece = GamePiece.createPiece(random.nextInt(GamePiece.PIECES));
    nextPiece();
    startTimeline();
  }

  /**
   * Updates the current and next piece, also makes sure the current and next piece is different.
   * This makes the game slightly less frustrating
   */
  void nextPiece() {
    currentPiece = nextPiece;
    var tempPiece = spawnPiece();
    while (tempPiece.getValue() == nextPiece.getValue()) {
      tempPiece = spawnPiece();
    }

    nextPiece = tempPiece;

    currentPiece(currentPiece);
    nextPiece(nextPiece);
    logger.info("The next piece is: {}", currentPiece);
  }

  /**
   * Create a new random piece
   *
   * @return The new random piece created
   */
  public GamePiece spawnPiece() {
    var maxPieces = GamePiece.PIECES;
    var randomPiece = random.nextInt(maxPieces);

    return GamePiece.createPiece(randomPiece);
  }

  /**
   * Handles the operations to be done after a piece is played.
   * This includes clearing full rows and
   * columns and incrementing the score.
   */
  private void afterPiece() {
    int numberOfLines = 0;
    boolean toIncrementMultiplier = false;
    Set<GameBlockCoordinate> blocksCleared = new HashSet<>();

    for (int gridX = 0; gridX < getRows(); gridX++) {
      int blockCount = 0;
      for (int gridY = 0; gridY < getCols(); gridY++) {
        if (grid.get(gridX, gridY) > 0) {
          blockCount++;
        }
      }
      if (blockCount == 5) {
        numberOfLines++;
        toIncrementMultiplier = true;
        for (int gridY = 0; gridY < getCols(); gridY++) {
          blocksCleared.add(new GameBlockCoordinate(gridX, gridY));
        }
      }
    }

    for (int gridY = 0; gridY < getCols(); gridY++) {
      int blockCount = 0;
      for (int gridX = 0; gridX < getRows(); gridX++) {
        if (grid.get(gridX, gridY) > 0) {
          blockCount++;
        }
      }
      if (blockCount == 5) {
        numberOfLines++;
        toIncrementMultiplier = true;
        for (int gridX = 0; gridX < getRows(); gridX++) {
          blocksCleared.add(new GameBlockCoordinate(gridX, gridY));
        }
      }
    }

    if (!blocksCleared.isEmpty()) {
      for (GameBlockCoordinate block : blocksCleared) {
        grid.set(block.getX(), block.getY(), 0);
      }
      Multimedia.playAudio("clear.wav");
    }

    blockCleared(blocksCleared);
    score(numberOfLines, blocksCleared.size());

    multiplier(toIncrementMultiplier);

    level();
  }


  /**
   * Increments the level when necessary
   */
  private void level() {
    var scoreAsString = String.valueOf(getScore());
    if (scoreAsString.length() < 4) {
      setLevel(0);
    } else {
      var newLevel = Integer.parseInt(scoreAsString.substring(0, scoreAsString.length() - 3));
      if (newLevel > getLevel()) {
        setLevel(newLevel);
        Multimedia.playAudio("levelup.wav");
      }
    }


  }

  /**
   * Incrementing and decrementing the multiplier
   *
   * @param toIncrementMultiplier Multiplier incremented if true els it is reset
   */
  private void multiplier(boolean toIncrementMultiplier) {
    if (toIncrementMultiplier) {
      setMultiplier(getMultiplier() + 1);
    } else {
      setMultiplier(1);
    }
  }

  /**
   * We increment the current score based on the formula, number of lines cleared * number of blocks
   * cleared * 10 * the current score multiplier
   *
   * @param numberOfLines  The number of lines cleared
   * @param numberOfBlocks The number of blocks cleared
   */
  private void score(int numberOfLines, int numberOfBlocks) {
    if (numberOfLines == 0) {
      return;
    }
    int pointsScored = numberOfLines * numberOfBlocks * 10 * multiplier.getValue();
    updateScore(pointsScored);
  }




  /**
   *Handles the looping of the game
   */
  public void gameLoop(){
    if(getLives()<0){
      return;
    }
    logger.info("Could not place a piece in time");
    lives.set(getLives()-1);
    currentPiece = spawnPiece();
    currentPiece(currentPiece);

    multiplier.set(1);
    Multimedia.playAudio("lifeloss.wav");
    startTimeline();
  }

  /**
   * Reset the timeline to register the new time delay and start it
   */
  void startTimeline(){
    if(!(timeline==null)) timeline.stop();
    loop();

    timeline = new Timeline(
        new KeyFrame(
            Duration.millis(getTimerDelay()),
            e-> gameLoop())
    );

    timeline.play();
  }

  /**
   * Stop the timeline, needed for early ending of the game or for pausing
   */
  public void stopTime(){
    timeline.stop();
  }

  /**
   * Gets the delay for the timer
   * @return The delay for the timer
   */
  public long getTimerDelay(){
    int delay = 12000-(500*level.getValue());
    return Math.max(delay, 2500);
  }


  /**
   * Set the listener for updating the current piece
   *
   * @param currentPieceListener The listener to be set
   */
  public void setCurrentPieceListener(NextPieceListener currentPieceListener) {
    this.currentPieceListener = currentPieceListener;
  }

  /**
   * Set the listener for updating the next piece
   *
   * @param nextPieceListener The listener to be set
   */
  public void setNextPieceListener(NextPieceListener nextPieceListener) {
    this.nextPieceListener = nextPieceListener;
  }

  /**
   * Set the listener for lines being cleared
   * @param blockClearedListener The listener being set
   */
  public void setLineClearedListener(BlockClearedListener blockClearedListener){
    this.blockClearedListener = blockClearedListener;
  }

  /**
   * Set the listener for the game being looped
   * @param gameLoopListener The listener being set
   */
  public void setGameLoop(GameLoopListener gameLoopListener){
    this.gameLoopListener = gameLoopListener;
  }

  /**
   * When a block is clicked, check if a piece can be played there and play the piece there
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    int placeX = gameBlock.getX();
    int placeY = gameBlock.getY();
    logger.info("Checking if piece {} can be played at {} {}", currentPiece, placeX, placeY);
    if (grid.canPlayPiece(currentPiece, placeX, placeY)) {
      grid.playPiece(currentPiece, placeX, placeY);
      nextPiece();
      startTimeline();

      Multimedia.playAudio("place.mp3");
    } else {
      logger.error("Unable to place piece: {} at {} {}", currentPiece, placeX, placeY);
      Multimedia.playAudio("fail.wav");
    }

    afterPiece();
  }


  /**
   * Swap the current and next piece
   */
  public void swapPiece() {
    Multimedia.playAudio("swappiece.mp3");
    var tempPiece = currentPiece;
    currentPiece = nextPiece;

    nextPiece = tempPiece;
  }

  /**
   * Triggers the currentPieceListener when the current piece is updated
   *
   * @param piece The new current piece
   */
  void currentPiece(GamePiece piece) {
    if (currentPieceListener != null) {
      currentPieceListener.nextPiece(piece);
    }
  }

  /**
   * Triggers the nextPieceListener when the next piece is updated
   *
   * @param piece The new next piece
   */
  void nextPiece(GamePiece piece) {
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(piece);
    }
  }

  /**
   * Triggers the blockClearedListener when blocks have been cleared
   * @param blocksToBeCleared The set of coordinates for the blocks that have been cleared
   */
  void blockCleared(Set<GameBlockCoordinate> blocksToBeCleared){
    if(blockClearedListener != null){
      blockClearedListener.lineCleared(blocksToBeCleared);
    }
  }

  /**
   * Triggers the gameLoopListener when the game loops
   */
  void loop(){
    if(gameLoopListener != null){
      gameLoopListener.gameLoop();
    }
  }


  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Get the score IntegerProperty
   *
   * @return The score IntegerProperty
   */
  public IntegerProperty scoreProperty() {
    return score;
  }

  /**
   * Get the level IntegerProperty
   *
   * @return The level IntegerProperty
   */
  public IntegerProperty levelProperty() {
    return level;
  }

  /**
   * Get the lives IntegerProperty
   *
   * @return The lives IntegerProperty
   */
  public IntegerProperty livesProperty() {
    return lives;
  }

  /**
   * Get the multiplier IntegerProperty
   *
   * @return The multiplier IntegerProperty
   */
  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  /**
   * Get the value of the score
   *
   * @return The score
   */
  public int getScore() {
    return score.getValue();
  }

  /**
   * Get the value of the level
   *
   * @return The level
   */
  public int getLevel() {
    return level.getValue();
  }

  /**
   * Set the value of the level
   *
   * @param level The new level
   */
  public void setLevel(int level) {
    this.level.set(level);
  }

  /**
   * Get the value of the lives
   *
   * @return The number of lives
   */
  public int getLives() {
    return lives.getValue();
  }

  /**
   * Get the value of the multiplier
   *
   * @return The score multiplier
   */
  public int getMultiplier() {
    return multiplier.getValue();
  }

  /**
   * Set the value of the multiplier
   *
   * @param multiplier The new value of the multiplier
   */
  public void setMultiplier(int multiplier) {
    this.multiplier.set(multiplier);
  }

  /**
   * Increment the score by the parameter points
   *
   * @param points The amount by which the score should be incremented
   */
  public void updateScore(int points) {
    score.set(score.getValue() + points);
  }

  /**
   * Get the current piece to be played
   *
   * @return The current piece to be played
   */
  public GamePiece getCurrentPiece() {
    return currentPiece;
  }

  /**
   * Get the next piece to be played
   *
   * @return The next piece to be played
   */
  public GamePiece getNextPiece() {
    return nextPiece;
  }

}
