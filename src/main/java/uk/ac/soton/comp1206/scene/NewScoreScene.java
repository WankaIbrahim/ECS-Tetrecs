package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.ResourceBundleHolder;

/**
 * THe NewScoreScene
 */
public class NewScoreScene extends BaseScene{

  private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.scene.ScoresScene.class);

  /**
   * The game the scores are taken from
   */
  private final Game game;

  /**
   * Create a new ScoreScene
   * @param gameWindow the Game Window this will be displayed in
   */
  public NewScoreScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;

    logger.info("Creating a Score Scene");
  }

  /**
   * Build the layout for the new score scene
   */
  @Override
  public void build() {
    logger.info("Building {}", this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var scorePane = new StackPane();
    scorePane.setMaxWidth(gameWindow.getWidth());
    scorePane.setMaxHeight(gameWindow.getHeight());
    scorePane.getStyleClass().add("menu-background");
    root.getChildren().add(scorePane);

    var mainPane = new BorderPane();
    scorePane.getChildren().add(mainPane);
    mainPane.setFocusTraversable(true);

    var text = new Text(ResourceBundleHolder.getResourceBundle().getString("enterYourName"));
    text.getStyleClass().add("heading");
    var enterName = new TextField();
    enterName.setMaxWidth(150);
    Platform.runLater(enterName::requestFocus);

    var title = new Text(ResourceBundleHolder.getResourceBundle().getString("gameOver"));
    title.getStyleClass().add("massivetitle");
    var titleBox  = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);

    var box = new VBox(text, enterName);
    box.setAlignment(Pos.CENTER);

    mainPane.setTop(titleBox);
    mainPane.setCenter(box);

    mainPane.setOnKeyPressed(e->{
      if(e.getCode()== KeyCode.ENTER){
        gameWindow.startScoresScene(game, enterName.getText());
      }
    });

  }

  /**
   * Initialize the NewScoreScene
   */
  @Override
  public void initialise() {

  }
}

