package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model that holds the state of a game board.
 * It is made up of a set of Integer values arranged in a 2D
 * array, with rows and columns.
 * Each value inside the Grid is an IntegerProperty can be bound
 * to enable modification and display the contents of
 * the grid.
 * The Grid contains functions related to modifying the model,
 * for example, placing a piece inside the grid.
 * The Grid should be linked to a GameBoard for its display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialize them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[rows][cols];

        //Add a SimpleIntegerProperty to every block in the grid
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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

    /**
     * Checks whether a piece can be played or not
     * @param piece The piece to be checked
     * @param placeX The x coordinate of the piece
     * @param placeY The y coordinate of the piece
     * @return True if a piece can be played
     */
    public boolean canPlayPiece(GamePiece piece, int placeX, int placeY){
        logger.info("Checking if piece {} can be played at {} {}", piece, placeX, placeY);



        int[][] blocks = piece.getBlocks();
        for(var blockX = 0; blockX < blocks.length; blockX++){
            for(var blockY = 0; blockY < blocks.length; blockY++){
                var blockValue = blocks[blockX][blockY];
                logger.info("Block location {},{} with a value of {} is being checked", blockX,
                    blockY, blockValue);

                if (blockValue > 0) {
                    var gridValue = get(placeX + blockX - 1, placeY + blockY - 1);
                    logger.info("Value of grid position {},{} is {}", placeX + blockX - 1,
                        placeY + blockY - 1, gridValue);

                    if (gridValue != 0) {
                        logger.info("Block cannot be placed");
                        return false;
                    } else {
                        logger.info("Block can be placed");
                    }

                }
            }
        }
        return true;
    }

    /**
     * Play a piece on the grid by updating it value
     * @param piece The piece to be played
     * @param placeX The x coordinate of the piece
     * @param placeY The y coordinate of the piece to be played
     */
    public void playPiece(GamePiece piece, int placeX, int placeY){
        logger.info("Attempting to play piece {} at {} {}.", piece, placeX, placeY);
        int value = piece.getValue();
        int[][] blocks = piece.getBlocks();


        for(var blockX = 0; blockX < blocks.length; blockX++){
            for(var blockY = 0; blockY < blocks.length; blockY++){
                var blockValue = blocks[blockX][blockY];
                if(blockValue>0) {
                    set(placeX + blockX -1, placeY + blockY -1, value);
                }
            }
        }
    }

}
