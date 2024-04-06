package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * The game associated with the challenge window
     */
    protected Game game;

    /**
     * The PieceBoard containing the current piece to be played
     */
    private PieceBoard currentPieceBoard;

    /**
     * The PieceBoard containing the next piece
     */
    private PieceBoard nextPieceBoard;


    /**
     * Create a new Single Player challenge scene
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

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var boardDimensions = gameWindow.getWidth() / 2;
        var board = new GameBoard(game.getGrid(), boardDimensions, boardDimensions);
        board.setAlignment(Pos.CENTER);
        mainPane.setCenter(board);

        mainPane.setTop(createTopInfoPanel());
        mainPane.setLeft(createLeftInfoPanel());
        mainPane.setRight(createRightInfoPanel());

        //Handle block on game-board grid being clicked
        board.setOnBlockClick(this::blockClicked);

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

        rightInfoPanel.getChildren()
            .addAll(livesBox, levelBox, multiplierBox, currentPieceBoard, nextPieceBoard);
        return rightInfoPanel;
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
        currentPieceBoard.rotatePiece();
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


}
