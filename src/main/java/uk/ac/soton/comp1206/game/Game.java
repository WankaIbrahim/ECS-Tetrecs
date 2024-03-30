package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    private final Random random = new Random();

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
    private GamePiece currentPiece;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    public void nextPiece(){
        currentPiece = spawnPiece();
        logger.info("The next piece is: {}", currentPiece);
    }

    public GamePiece spawnPiece(){
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("Picking a random piece: {}", randomPiece);

        return GamePiece.createPiece(randomPiece);
    }

    /**
     * Initialize a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        nextPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        int placeX = gameBlock.getX();
        int placeY = gameBlock.getY();

        if(grid.canPlayPiece(currentPiece,placeX,placeY)){
            grid.playPiece(currentPiece, placeX, placeY);
            nextPiece();
        }
        afterPiece();

    }

    public void afterPiece(){
        Set<GameBlockCoordinate> blocksToBeCleared = new HashSet<>();


        for(int gridX = 0; gridX<getRows(); gridX++){
            int blockCount = 0;
            for(int gridY = 0; gridY<getCols(); gridY++){
                if(grid.get(gridX,gridY)>0){
                    blockCount++;
                }
            }
            if (blockCount == 5) {
                for(int gridY = 0; gridY<getCols(); gridY++){
                    blocksToBeCleared.add(new GameBlockCoordinate(gridX,gridY));
                }
            }
        }

        for(int gridY = 0; gridY<getCols(); gridY++){
            int blockCount = 0;
            for(int gridX = 0; gridX<getRows(); gridX++){
                if(grid.get(gridX,gridY)>0){
                    blockCount++;
                }
            }
            if (blockCount == 5) {
                for(int gridX = 0; gridX<getRows(); gridX++){
                    blocksToBeCleared.add(new GameBlockCoordinate(gridX,gridY));
                }
            }
        }

        for(GameBlockCoordinate block: blocksToBeCleared){
            grid.set(block.getX(), block.getY(), 0);
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


}
