package uk.ac.soton.comp1206.scene;

import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The Single Player challenge scene.
 * Holds the UI for the single player challenge mode in the
 * game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

  /**
   * The game associated with the current challenge
   */
  private Game game;

  /**
   * The GameBoard associated with the current challenge
   */
  private GameBoard board;

  /**
   * The PieceBoard containing the current piece to be played
   */
  private PieceBoard currentPieceBoard;

  /**
   * The PieceBoard containing the next piece
   */
  private PieceBoard nextPieceBoard;

  /**
   * Keeps track of the current block highlighted by the keyboard
   */
  private GameBlockCoordinate coordinate = new GameBlockCoordinate(2,2);

  /**
   * The timebar
   */
  private Rectangle timeBar;

  /**
   * The width of the timebar
   */
  private long barWidth = gameWindow.getWidth();

  /**
   * The Timeline used for animating the timebar
   */
  private Timeline timeline;


  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
    Multimedia.stopBackgroundMusic();
    Multimedia.playBackgroundMusic("game.mp3");

  }


  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    var boardDimensions = gameWindow.getWidth() / 2;
    board = new GameBoard(game.getGrid(), boardDimensions, boardDimensions);
    board.setAlignment(Pos.CENTER);
    board.setFocusTraversable(true);


    mainPane.setCenter(board);

    mainPane.setTop(createTopInfoPanel());
    mainPane.setLeft(createLeftInfoPanel());
    mainPane.setRight(createRightInfoPanel());
    mainPane.setBottom(createTimeBar());

    board.setOnBlockClick(this::blockClicked);
    board.setOnKeyPressed(keyEvent -> {
      logger.info("Key pressed: {}", keyEvent.getText());
      keyPressed(keyEvent);
    });

    currentPieceBoard.setOnRotatePieceListener(this::rotatePiece);
    nextPieceBoard.setOnSwapPieceListener(this::swapPiece);
  }


  /**
   * Create the top info panel
   *
   * @return The HBox containing the UI elements of the top info panel
   */
  private HBox createTopInfoPanel() {
    var topInfoPanel = new HBox();
    topInfoPanel.toBack();
    topInfoPanel.alignmentProperty().set(Pos.BOTTOM_CENTER);

    var title = new Text("Challenge Scene");
    title.getStyleClass().add("title");

    topInfoPanel.getChildren().add(title);
    return topInfoPanel;
  }

  /**
   * Create the left information panel
   *
   * @return The VBox containing the UI elements in the left information panel
   */
  private VBox createLeftInfoPanel() {
    var leftInfoPanel = new VBox(10);
    leftInfoPanel.setPadding(new Insets(-30, 10, 5, 20));
    leftInfoPanel.setSpacing(5);
    leftInfoPanel.setMaxWidth(80);
    leftInfoPanel.setMinWidth(80);
    leftInfoPanel.setPrefWidth(80);
    leftInfoPanel.setAlignment(Pos.TOP_LEFT);

    var scoreBox = new VBox();
    scoreBox.setAlignment(Pos.TOP_CENTER);
    var scoreLabel = new Text("SCORE");
    var score = new Text();
    score.textProperty().bind(game.scoreProperty().asString());
    scoreLabel.getStyleClass().add("heading");
    score.getStyleClass().add("score");
    scoreBox.getChildren().addAll(scoreLabel, score);

    leftInfoPanel.getChildren().addAll(scoreBox);
    return leftInfoPanel;
  }

  /**
   * Create the right info panel
   *
   * @return The VBOx containing the UI elements in the right info panel
   */
  private VBox createRightInfoPanel() {
    var rightInfoPanel = new VBox();
    rightInfoPanel.setPadding(new Insets(-30, 20, 5, 10));
    rightInfoPanel.setSpacing(10);
    rightInfoPanel.setAlignment(Pos.TOP_CENTER);

    var livesBox = new VBox();
    livesBox.setSpacing(2);
    livesBox.setAlignment(Pos.CENTER);
    var livesLabel = new Text("LIVES");
    var lives = new Text();
    lives.textProperty().bind(game.livesProperty().asString());
    livesLabel.getStyleClass().add("heading");
    lives.getStyleClass().add("lives");
    livesBox.getChildren().addAll(livesLabel, lives);

    var levelBox = new VBox();
    levelBox.setSpacing(2);
    levelBox.setAlignment(Pos.CENTER);
    var levelLabel = new Text("LEVEL");
    var level = new Text();
    level.textProperty().bind(game.levelProperty().asString());
    levelLabel.getStyleClass().add("heading");
    level.getStyleClass().add("level");
    levelBox.getChildren().addAll(levelLabel, level);

    var multiplierBox = new VBox();
    multiplierBox.setSpacing(2);
    multiplierBox.setAlignment(Pos.CENTER);
    var multiplierLabel = new Text("MULTIPLIER");
    var multiplier = new Text();
    multiplier.textProperty().bind(game.multiplierProperty().asString());
    multiplierLabel.getStyleClass().add("heading");
    multiplier.getStyleClass().add("multiplier");
    multiplierBox.getChildren().addAll(multiplierLabel, multiplier);

    var boardDimensions = gameWindow.getWidth() / 5;
    currentPieceBoard = new PieceBoard(boardDimensions, true, false);
    currentPieceBoard.displayPiece(game.getCurrentPiece());
    nextPieceBoard = new PieceBoard((double) boardDimensions / 2, false, true);
    nextPieceBoard.displayPiece(game.getNextPiece());
    nextPieceBoard.setAlignment(Pos.CENTER);

    game.setCurrentPieceListener(this::currentPiece);
    game.setNextPieceListener(this::nextPiece);
    game.setLineClearedListener(this::blockCleared);
    game.setGameLoop(this::resetTimeBar);

    rightInfoPanel.getChildren().addAll(livesBox, levelBox, multiplierBox, currentPieceBoard, nextPieceBoard);
    return rightInfoPanel;
  }

  private Rectangle createTimeBar(){
    timeBar = new Rectangle();
    timeBar.setWidth(barWidth);
    timeBar.setHeight(20);
    timeBar.setFill(Color.LIGHTGREEN);

    KeyValue start = new KeyValue(timeBar.widthProperty(),barWidth);
    KeyValue end = new KeyValue(timeBar.widthProperty(), 1);
    KeyFrame frame = new KeyFrame(Duration.millis(game.getTimerDelay()),start,end);
    timeline = new Timeline(frame);
    timeline.play();

    return timeBar;
  }

  /**
   * Set up the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start a new game
    game = new Game(5, 5);
  }

  /**
   * Initialize the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    game.start();
  }


  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(MouseEvent event, GameBlock gameBlock) {
    if (event.getButton() == MouseButton.SECONDARY) {
      swapPiece();
    } else {
      game.blockClicked(gameBlock);
    }

  }

  /**
   * Handles the rotating of a piece held in the current piece board
   */
  private void rotatePiece() {
    currentPieceBoard.rotatePieceRight();
  }

  /**
   * Handle the swapping of the current and next PieceBoards
   */
  private void swapPiece() {
    game.swapPiece();

    var tempPiece = currentPieceBoard.getPiece();
    currentPieceBoard.displayPiece(nextPieceBoard.getPiece());
    nextPieceBoard.displayPiece(tempPiece);
  }

  /**
   * Handles when a key is pressed
   * @param keyEvent The KeyEvent to be handled
   */
  private void keyPressed(KeyEvent keyEvent) {
    var keyCode = keyEvent.getCode();

    switch (keyCode){
      case ESCAPE: {
        //TODO Set this to take you to the end game screen

        break;
      }

      case ENTER, X:
        game.blockClicked(board.getBlock(coordinate.getX(), coordinate.getY()));
        break;

      case UP, W:
        if(coordinate.getY()==0){
          logger.info("Grid border reached, cannot move up");
          break;
        }
        board.getBlock(coordinate.getX(),coordinate.getY()).paintEmpty();
        coordinate = coordinate.subtract(0,1);
        board.getBlock(coordinate.getX(),coordinate.getY()).paintHover();

        logger.info("Moving pointer up, pointer now at {},{}", coordinate.getX(), coordinate.getY());
        break;

      case DOWN, S:
        if(coordinate.getY()==4){
          logger.info("Grid border reached, cannot move down");
          break;
        }
        board.getBlock(coordinate.getX(),coordinate.getY()).paintEmpty();
        coordinate = coordinate.add(0,1);
        board.getBlock(coordinate.getX(),coordinate.getY()).paintHover();

        logger.info("Moving pointer down, pointer now at {},{}", coordinate.getX(), coordinate.getY());
        break;

      case RIGHT, D:
        if(coordinate.getX()==4){
          logger.info("Grid border reached, cannot move right");
          break;
        }
        board.getBlock(coordinate.getX(),coordinate.getY()).paintEmpty();
        coordinate = coordinate.add(1,0);
        board.getBlock(coordinate.getX(),coordinate.getY()).paintHover();

        logger.info("Moving pointer to the right, pointer now at {},{}", coordinate.getX(), coordinate.getY());
        break;

      case LEFT, A:
        if(coordinate.getX()==0){
          logger.info("Grid border reached, cannot move left");
          break;
        }
        board.getBlock(coordinate.getX(),coordinate.getY()).paintEmpty();
        coordinate = coordinate.subtract(1,0);
        board.getBlock(coordinate.getX(),coordinate.getY()).paintHover();

        logger.info("Moving pointer to the left, pointer now at {},{}", coordinate.getX(), coordinate.getY());
        break;

      case SPACE, R:
        swapPiece();
        break;

      case Q, Z, OPEN_BRACKET:
        rotatePiece();
        break;

      case E, C, CLOSE_BRACKET:
        rotatePiece();
        rotatePiece();
        rotatePiece();
        break;
    }
  }




  /**
   * Displays a GamePiece in the current PieceBoard
   *
   * @param piece The GamePiece to be displayed
   */
  private void currentPiece(GamePiece piece) {
    currentPieceBoard.displayPiece(piece);
  }

  /**
   * Displays a GamePiece in the next PieceBoard
   *
   * @param piece The GamePiece to be displayed
   */
  private void nextPiece(GamePiece piece) {
    nextPieceBoard.displayPiece(piece);
  }

  /**
   * Sets an animation on all blocks that have been cleared
   * @param blocksCleared A set of coordinates for the blocks that have been cleared
   */
  private void blockCleared(Set<GameBlockCoordinate> blocksCleared){
    for(GameBlockCoordinate c: blocksCleared){
      board.getBlock(c.getX(), c.getY()).fadeOut();
    }
  }

  /**
   * Resets the timebar
   */
  private void resetTimeBar(){
    timeBar.setWidth(gameWindow.getWidth());
    barWidth = gameWindow.getWidth();
    timeline.stop();
    KeyValue start = new KeyValue(timeBar.widthProperty(),barWidth);
    KeyValue end = new KeyValue(timeBar.widthProperty(), 1);
    KeyFrame frame = new KeyFrame(Duration.millis(game.getTimerDelay()),start,end);
    timeline = new Timeline(frame);
    timeline.play();
  }
}
