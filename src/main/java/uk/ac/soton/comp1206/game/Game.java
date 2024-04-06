package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
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
    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;
    private final Random random = new Random();

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
    private GamePiece currentPiece;

    /**
     * The PieceBoard that holds the next piece
     */
    private GamePiece nextPiece;

    /**
     * The listener called when the next piece is updated
     */
    private NextPieceListener nextPieceListener;

    /**
     * The listener call when the current piece is updated
     */
    private NextPieceListener currentPieceListener;


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
        nextPiece = GamePiece.createPiece(random.nextInt(GamePiece.PIECES));
        nextPiece();
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
    }

    /**
     * Updates the current and next piece
     */
    public void nextPiece() {
        currentPiece = nextPiece;
        nextPiece = spawnPiece();
        while (nextPiece.getValue() == currentPiece.getValue()) {
            nextPiece = spawnPiece();
        }

        currentPiece(currentPiece);
        nextPiece(nextPiece);
        logger.info("The next piece is: {}", currentPiece);
    }

    /**
     * Create a new random piece
     * @return The new random piece created
     */
    public GamePiece spawnPiece() {
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("Picking a random piece: {}", randomPiece);

        return GamePiece.createPiece(randomPiece);
    }

    /**
     * Handles the operations to be done after a piece is played.
     * This includes clearing full rows and columns and incrementing the score.
     */
    public void afterPiece() {
        int numberOfLines = 0;
        boolean toIncrementMultiplier = false;
        Set<GameBlockCoordinate> blocksToBeCleared = new HashSet<>();

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
                    blocksToBeCleared.add(new GameBlockCoordinate(gridX, gridY));
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
                    blocksToBeCleared.add(new GameBlockCoordinate(gridX, gridY));
                }
            }
        }

        if(!blocksToBeCleared.isEmpty()) {
            for (GameBlockCoordinate block : blocksToBeCleared) {
                grid.set(block.getX(), block.getY(), 0);
            }
            Multimedia.playAudio("clear.wav");
        }

        score(numberOfLines, blocksToBeCleared.size());

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
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        int placeX = gameBlock.getX();
        int placeY = gameBlock.getY();
        logger.info("Checking if piece {} can be played at {} {}", currentPiece, placeX, placeY);
        if (grid.canPlayPiece(currentPiece, placeX, placeY)) {
            grid.playPiece(currentPiece, placeX, placeY);
            nextPiece();

            Multimedia.playAudio("place.mp3");
        } else {
            logger.error("Unable to place piece: {} at {} {}", currentPiece, placeX, placeY);
            Multimedia.playAudio("fail.wav");
        }

        afterPiece();

    }

    /**
     * Swaps the current piece and next piece
     */
    public void swapPiece() {
        Multimedia.playAudio("swappiece.mp3");
        var tempPiece = currentPiece;
        currentPiece = nextPiece;

        nextPiece = tempPiece;
    }


    /**
     * Set a listener to handle updating the current piece
     *
     * @param currentPieceListener The listener to be set
     */
    public void setCurrentPieceListener(NextPieceListener currentPieceListener) {
        this.currentPieceListener = currentPieceListener;
    }

    /**
     * Set a listener to handle updating the next piece
     *
     * @param nextPieceListener The listener to be set
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    /**
     * Triggered when a piece is played
     *
     * @param piece The new current piece
     */
    void currentPiece(GamePiece piece) {
        if (currentPieceListener != null) {
            currentPieceListener.nextPiece(piece);
        }
    }

    /**
     * Triggered when a piece is placed
     *
     * @param piece The next piece
     */
    void nextPiece(GamePiece piece) {
        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(piece);
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
