package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 * The GameBoard can hold an internal grid of its own, for example,
 * for displaying an upcoming block.
 * It will also be linked to an external grid for the main game board.
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * Determine if it is a game board or subclass
     */
    private boolean isGameBoard = false;

    /**
     * Determine if it is a current board or other type of game board
     */
    private boolean isCurrentBoard = false;

    /**
     * Determine if it is a next board or other type of game board
     */
    private boolean isNextBoard = false;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.isGameBoard = true;
        this.grid = grid;

        //Build the GameBoard
        build();
    }


    /**
     * Create a new GameBoard with its own internal grid,
     * specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     * @param isCurrentBoard if it is a current board
     * @param isNextBoard if it is a next board
     */
    public GameBoard(int rows, int cols, double width, double height, boolean isCurrentBoard,
        boolean isNextBoard) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.isCurrentBoard = isCurrentBoard;
        this.isNextBoard = isNextBoard;
        this.grid = new Grid(rows, cols);

        //Build the GameBoard
        build();
    }


    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", rows, cols);

        setPrefSize(width, height);

        setGridLinesVisible(true);

        blocks = new GameBlock[rows][cols];

        for (var x = 0; x < rows; x++) {
            for (var y = 0; y < cols; y++) {
                createBlock(x,y);
            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected void createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));
    }


    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(event, block);
        }
    }


    /**
     * Get the grid associated with the board
     * @return The grid associated with the board
     */
    protected Grid getGrid() {
        return grid;
    }

    /**
     * Get a specific block from the GameBoard, specified by its row and column
     *
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Get if it is a game board
     *
     * @return the isGameBoard boolean
     */
    public boolean isGameBoard() {
        return isGameBoard;
    }

    /**
     * Get if it is a current board
     *
     * @return is current board boolean
     */
    public boolean isCurrentBoard() {
        return isCurrentBoard;
    }

    /**
     * Get if it is a next piece board
     *
     * @return is next board boolean
     */
    public boolean isNextBoard() {
        return isNextBoard;
    }
}
