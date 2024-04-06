package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * The Visual User Interface component representing a single block in the grid.
 * Extends Canvas and is responsible for drawing itself.
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

  /**
   * The board that holds the block
   */
    private final GameBoard gameBoard;

  /**
   * The width of the block
   */
  private final double width;
    /**
     * The height of the block
     */
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (zero = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

      setOnMouseEntered((e) -> paintHover());

      setOnMouseExited((e) -> paintEmpty());

      //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated.
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }


  /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
      if (value.get() == 0) {
        paintEmpty();
      }

      //If the block is not empty, paint with the colour represented by the value
      if (value.get() != 0) {
        paintColor(COLOURS[value.get()]);
      }
    }


  /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
      if (value.get() != 0) {
        return;
      }
      var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
      Color c = new Color(0, 0, 0, 0.24);
      gc.setFill(c);
      gc.fillRect(0, 0, width, height);

      //Border
      gc.setStroke(Color.color(1, 1, 1, 0.5));
      gc.strokeRect(0,0,width,height);

    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    void paintColor(Paint colour) {
      var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);

      //Colour highlights
      gc.setFill(Color.color(1, 1, 1, 0.5));
      gc.fillRect(0, 0, width, 2);
      gc.fillRect(0, 0, 2, height);

      //Colour shadows
      gc.setFill(Color.color(0, 0, 0, 0.5));
      gc.fillRect(width - 2, 0, width, height);
      gc.fillRect(0, height - 2, width, height);

      //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

      if (getX() == 1 && getY() == 1 && gameBoard.isCurrentBoard()) {
        Color b = new Color(0.2, 0.2, 0.1, 0.80);
        gc.setFill(b);
        gc.fillOval(0, 0, width, height);
      }
    }

  /**
   * Paint this canvas when it is hovered over
   */
  private void paintHover() {
    if (value.get() != 0) {
      return;
    }
    if (gameBoard.isGameBoard()) {
      var gc = getGraphicsContext2D();

      gc.setFill(Color.color(1, 1, 1, 0.5));
      gc.fillRect(0, 0, width, height);
    }
  }


  /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

  /**
   * Get the string representation of the block properties
   * @return The block properties
   */
  @Override
    public String toString() {
        return "GameBlock{" +
            "x=" + x +
            ", y=" + y +
            ", value=" + value +
            '}';
    }
}
