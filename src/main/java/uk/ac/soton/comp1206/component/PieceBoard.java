package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.RotatePieceListener;
import uk.ac.soton.comp1206.event.SwapPieceListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * A PieceBoard is a visual component to represent the visual PieceBoard. It extends a GameBoard to
 * hold a grid of GameBlocks. The PieceBoard holds an internal grid of its own for displaying game
 * pieces.
 */
public class PieceBoard extends GameBoard {

  private static final Logger logger = LogManager.getLogger(PieceBoard.class);

  /**
   * The number of rows in the board
   */
  private final int rows;

  /**
   * The number of columns in the board
   */
  private final int cols;

  /**
   * The width of the board
   */
  private final double width;

  /**
   * The height of the board
   */
  private final double height;
  /**
   * To determine if it is a current board
   */
  private final boolean isCurrentBoard;
  /**
   * To determine if it is a next board
   */
  private final boolean isNextBoard;
  /**
   * THe piece being displayed by the board
   */
  private GamePiece piece;
  /**
   * The listener to call when a piece is to be rotated
   */
  private RotatePieceListener rotatePieceListener;
  /**
   * The listener to call when a piece is to be swapped
   */
  private SwapPieceListener swapPieceListener;

  /**
   * Create a new PieceBoard based of the dimension
   *
   * @param dimension      The width and height of the PieceBoard
   * @param isCurrentBoard If it is a current board
   * @param isNextBoard    If it is a next board
   */
  public PieceBoard(double dimension, boolean isCurrentBoard, boolean isNextBoard) {
    super(3, 3, dimension, dimension, isCurrentBoard, isNextBoard);

    this.rows = 3;
    this.cols = 3;
    this.width = dimension;
    this.height = dimension;
    this.isCurrentBoard = isCurrentBoard;
    this.isNextBoard = isNextBoard();

    build();
  }

  /**
   * Fill in the piece board with empty game blocks
   */
  @Override
  protected void build() {
    logger.info("Building grid: {} x {}", rows, cols);

    setPrefSize(width, height);

    setGridLinesVisible(true);

    blocks = new GameBlock[rows][cols];

    for (var x = 0; x < rows; x++) {
      for (var y = 0; y < cols; y++) {
        createBlock(x, y);
      }
    }
  }

  /**
   * Create a block at the given x and y position in the piece board
   *
   * @param x column
   * @param y row
   */
  @Override
  protected void createBlock(int x, int y) {
    var blockWidth = width / cols;
    var blockHeight = height / rows;

    //Create a new GameBlock UI component
    GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

    //Add to the GridPane
    add(block, x, y);

    //Add to our block directory
    blocks[x][y] = block;

    //Link the GameBlock component to the corresponding value in the Grid
    block.bind(grid.getGridProperty(x, y));
  }

  /**
   * Clear the grid
   */
  public void clearGrid() {
    for (var blockX = 0; blockX < blocks.length; blockX++) {
      for (var blockY = 0; blockY < blocks.length; blockY++) {
        getGrid().set(blockX, blockY, 0);
      }
    }
  }

  /**
   * Display a given piece in the PieceBoard
   *
   * @param piece The piece to be rotated
   */
  public void displayPiece(GamePiece piece) {
    this.piece = piece;
    clearGrid();
    int[][] blocks = piece.getBlocks();
    for (var blockX = 0; blockX < blocks.length; blockX++) {
      for (var blockY = 0; blockY < blocks.length; blockY++) {
        var blockValue = blocks[blockX][blockY];
        if (blockValue > 0) {
          getGrid().set(blockX, blockY, blockValue);
          if (isNextBoard) {
            getBlock(blockX, blockY).setOnMouseClicked((e) -> setSwapPieceListener());
          }
        }
      }
    }
    if (isCurrentBoard) {
      getBlock(1, 1).setOnMouseClicked((e) -> setRotatePieceListener());
    }


  }

  /**
   * Rotate the piece held in the PieceBoard to the right
   */
  public void rotatePieceRight() {
    Multimedia.playAudio("rotate.wav");
    piece.rotate();
    displayPiece(piece);
  }

  /**
   * Rotate the piece held in the PieceBoard to the left
   */
  public void rotatePieceLeft() {
    Multimedia.playAudio("rotate.wav");
    piece.rotate();
    piece.rotate();
    piece.rotate();
    displayPiece(piece);
  }


  /**
   * Set the listener to handle when a piece is to be rotated
   *
   * @param listener The listener to be set
   */
  public void setOnRotatePieceListener(RotatePieceListener listener) {
    this.rotatePieceListener = listener;
  }

  /**
   * Set the listener to handle when a piece is to be swapped
   *
   * @param listener The listener to be set
   */
  public void setOnSwapPieceListener(SwapPieceListener listener) {
    this.swapPieceListener = listener;
  }

  /**
   * Handles the rotating of pieces
   */
  void setRotatePieceListener() {
    logger.info("Rotating piece");

    if (rotatePieceListener != null) {
      rotatePieceListener.rotatePiece();
    }
  }

  /**
   * Handle the swapping of pieces
   */
  void setSwapPieceListener() {
    logger.info("Swapping pieces");

    if (swapPieceListener != null) {
      swapPieceListener.swapPiece();
    }
  }


  /**
   * Get the GamePiece held by the PieceBoard
   *
   * @return The GamePiece held by the PieceBoard
   */
  public GamePiece getPiece() {
    return piece;
  }
}
