package uk.ac.soton.comp1206.scene;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The MultiplayerScene
 */
public class MultiplayerMenu extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MultiplayerMenu.class);

  /**
   * Create a new MultiplayerScene
   * @param gameWindow the Game Window this will be displayed in
   */
  public MultiplayerMenu(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Multiplayer Menu");
  }

  /**
   * Build the multiplayer layout
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var multiplayerPane = new StackPane();
    multiplayerPane.setMaxWidth(gameWindow.getWidth());
    multiplayerPane.setMaxHeight(gameWindow.getHeight());
    multiplayerPane.getStyleClass().add("menu-background");
    root.getChildren().add(multiplayerPane);

    var mainPane = new BorderPane();
    multiplayerPane.getChildren().add(mainPane);
  }

  /**
   * Initialize the multiplayerScene
   */
  @Override
  public void initialise() {

  }
}
