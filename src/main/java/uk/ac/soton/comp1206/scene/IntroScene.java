package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The introduction of the application
 */
public class IntroScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(IntroScene.class);

  /**
   * True when the intro has been skipped
   */
  private Boolean isSkipped = false;

  /**
   * Create an intro scene
   * @param gameWindow the Game Window this will be displayed in
   */
  public IntroScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");

    Multimedia.playAudio("intro.mp3");

  }

  /**
   * Build the intro layout
   */
  @Override
  public void build() {
    logger.info("Building {}", this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var introPane = new StackPane();
    introPane.setMaxWidth(gameWindow.getWidth());
    introPane.setMaxHeight(gameWindow.getHeight());
    introPane.getStyleClass().add("intro");
    root.getChildren().add(introPane);

    var mainPane = new BorderPane();
    introPane.getChildren().add(mainPane);

    introPane.requestFocus();
    introPane.setOnMouseClicked(e->{
      Multimedia.stopAudio();
      gameWindow.startMenu(false);
      isSkipped = true;
    });



    var introPic = new ImageView(new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/ECSGames.png")).toExternalForm()));
    introPic.setFitWidth(600);
    introPic.setFitHeight(400);
    mainPane.setCenter(introPic);


    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);
    fadeTransition.setDuration(Duration.seconds(6));
    fadeTransition.setCycleCount(1);
    fadeTransition.setNode(introPic);
    fadeTransition.play();

    fadeTransition.setOnFinished(e->{
      if(!isSkipped){
        gameWindow.startMenu(false);
      }
    });
  }

  /**
   * Initialize the intro
   */
  @Override
  public void initialise() {

  }
}
