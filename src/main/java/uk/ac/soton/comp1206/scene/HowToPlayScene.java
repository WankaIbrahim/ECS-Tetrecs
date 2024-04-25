package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javax.swing.undo.AbstractUndoableEdit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.ResourceBundleHolder;

/**
 * The scene with the instructions and gamePieces
 */
public class HowToPlayScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(HowToPlayScene.class);

  /**
   * Create a new howToPlayScene scene
   * @param gameWindow the Game Window this will be displayed in
   */
  public HowToPlayScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating a HowToPlay Scene");
  }

  /**
   * Build the how to play layout
   */
  @Override
  public void build() {
    logger.info("Building {}", this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var howToPlayPane = new StackPane();
    howToPlayPane.setMaxWidth(gameWindow.getWidth());
    howToPlayPane.setMaxHeight(gameWindow.getHeight());
    howToPlayPane.getStyleClass().add("menu-background");
    root.getChildren().add(howToPlayPane);

    var mainPane = new BorderPane();
    howToPlayPane.getChildren().add(mainPane);

    mainPane.setFocusTraversable(true);
    mainPane.setOnMouseClicked(e-> gameWindow.startMenu(true));

    mainPane.setOnKeyPressed(e-> gameWindow.startMenu(true));


    var mainTitle = new Text(ResourceBundleHolder.getResourceBundle().getString("instructions"));
    mainTitle.getStyleClass().add("heading");

    var brief = new Text(ResourceBundleHolder.getResourceBundle().getString("gameDescription"));
    brief.textAlignmentProperty().set(TextAlignment.CENTER);
    brief.getStyleClass().add("instructions");

    var instructions = new ImageView(new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/Instructions.png")).toExternalForm()));
    instructions.setFitHeight(330);
    instructions.setFitWidth(530);

    var subTitle = new Text(ResourceBundleHolder.getResourceBundle().getString("gamePieces"));
    subTitle.getStyleClass().add("heading");

    var firstRow = new HBox();
    firstRow.setAlignment(Pos.CENTER);
    firstRow.setSpacing(10);
    var secondRow = new HBox();
    secondRow.setAlignment(Pos.CENTER);
    secondRow.setSpacing(10);
    var thirdRow = new HBox();
    thirdRow.setAlignment(Pos.CENTER);
    thirdRow.setSpacing(10);

    for(int i = 0; i<15; i++){
      var pieceBoard = new PieceBoard(50,false,false);
      pieceBoard.displayPiece(GamePiece.createPiece(i));
      if(i<5){
        firstRow.getChildren().add(pieceBoard);
      }else if (i<10){
        secondRow.getChildren().add(pieceBoard);
      }else{
        thirdRow.getChildren().add(pieceBoard);
      }
    }

    var gamePieces = new VBox(firstRow,secondRow,thirdRow);
    gamePieces.setAlignment(Pos.CENTER);
    gamePieces.setSpacing(12);

    var topBox = new VBox(mainTitle,brief,instructions,subTitle,gamePieces);
    topBox.setAlignment(Pos.CENTER);

    mainPane.setTop(topBox);
  }

  /**
   * Initialize the howToPlayScene
   */
  @Override
  public void initialise() {

  }

}
