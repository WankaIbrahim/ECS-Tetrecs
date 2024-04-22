package uk.ac.soton.comp1206.scene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.component.ScoreList;

/**
 * The ScoreScene
 */
public class ScoresScene extends BaseScene{
  private static final Logger logger = LogManager.getLogger(ScoresScene.class);

  /**
   * The game the scores are taken from
   */
  private final Game game;

  /**
   * Holds the local scores
   */
  private SimpleListProperty<Pair<String, Integer>> localScores;

  /**
   * Holds the online scores
   */
  private SimpleListProperty<Pair<String, Integer>> onlineScores;

  /**
   * The name of the person who has set the latest score
   */
  private final String scoreHolder;

  /**
   * The communicator associated with the ScoreScene
   */
  private final Communicator communicator;


  /**
   * Create a new ScoreScene
   * @param gameWindow the Game Window this will be displayed in
   */
  public ScoresScene(GameWindow gameWindow, Game game, String scoreHolder, Communicator communicator) {
    super(gameWindow);
    this.game = game;
    this.scoreHolder = scoreHolder;
    this.communicator = communicator;

    logger.info("Creating a Score Scene");
  }

  /**
   * Build the layout for the score scene
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

    mainPane.setOnKeyPressed(e->{
      if(e.getCode()== KeyCode.ENTER){
        gameWindow.startMenu(false);
      }
    });

    mainPane.setTop(createTopBox());

    mainPane.setCenter(createScoreBox());

  }

  /**
   * Create the VBox which holds the scores
   * @return The VBox which holds the scores
   */
  private VBox createScoreBox() {
    ScoreList localScoresBox = new ScoreList();
    localScoresBox.setAlignment(Pos.TOP_LEFT);
    ArrayList<Pair<String,Integer>> localScoresArrayList = new ArrayList<>();
    localScores = new SimpleListProperty<>(FXCollections.observableArrayList(localScoresArrayList));
    localScoresBox.scoresProperty().bind(localScores);
    if(!Objects.equals(scoreHolder, "")){
      localScores.add(new Pair<>(scoreHolder, game.getScore()));
    }
    loadLocalScores();
    localScores.sort((p1, p2) -> p2.getValue() - p1.getValue());
    writeScores();

    ScoreList onlineScoresBox = new ScoreList();
    onlineScoresBox.setAlignment(Pos.TOP_RIGHT);
    ArrayList<Pair<String,Integer>> onlineScoresArrayList = new ArrayList<>();
    onlineScores = new SimpleListProperty<>(FXCollections.observableArrayList(onlineScoresArrayList));
    onlineScoresBox.scoresProperty().bind(onlineScores);


    loadOnlineScores();
    onlineScores.sort((p1, p2) -> p2.getValue() - p1.getValue());




    var localHeading = new Text("Local Scores");
    localHeading.getStyleClass().add("heading");
    localHeading.setTextAlignment(TextAlignment.LEFT);

    var onlineHeading = new Text("Online Scores");
    onlineHeading.getStyleClass().add("heading");
    onlineHeading.setTextAlignment(TextAlignment.RIGHT);

    var headingBox = new HBox(localHeading, onlineHeading);
    headingBox.setAlignment(Pos.CENTER);
    headingBox.setSpacing(270);

    var scoreBox = new HBox(localScoresBox, onlineScoresBox);
    scoreBox.setSpacing(100);
    scoreBox.setAlignment(Pos.CENTER);

    var centerBox = new VBox(headingBox, scoreBox);
    centerBox.setSpacing(20);
    centerBox.setAlignment(Pos.CENTER);

    return centerBox;
  }

  /**
   * Create the VBox which holds the ui components
   * to be displayed at the top of the scene
   * @return The VBox to be displayed at the top of the scene
   */
  private VBox createTopBox() {
    var title = new ImageView(new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/TetrECS.png")).toExternalForm()));
    title.setFitWidth(600);
    title.setFitHeight(120);

    HBox titleBox = new HBox(title);
    titleBox.setPadding(new Insets(80,0,0,0));
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setPadding(new Insets(20,0,0,0));

    RotateTransition rotate = new RotateTransition();
    rotate.setAxis(Rotate.Z_AXIS);
    rotate.setFromAngle(3);
    rotate.setToAngle(-3);
    rotate.setRate(0.2);
    rotate.setCycleCount(Animation.INDEFINITE);
    rotate.setDuration(Duration.INDEFINITE);
    rotate.setAutoReverse(true);
    rotate.setNode(title);
    rotate.play();

    var topBox = new VBox(titleBox);
    topBox.setAlignment(Pos.CENTER);

    return topBox;
  }

  /**
   * Load the scores stored locally
   */
  private void loadLocalScores(){
    try {
      Files.lines(Paths.get("scores.txt")).forEach(line -> {
        String[] parts = line.split(":");
        if(parts.length==2){
          var name = parts[0];
          var score = Integer.parseInt(parts[1]);

          localScores.add(new Pair<>(name, score));
        }
      });
    } catch (IOException e) {
      logger.error("There is a problem in the syntax of the score text file");
    }
  }

  /**
   * Load the scores from the server
   */
  private void loadOnlineScores(){
    communicator.addListener((message) -> Platform.runLater(() -> {
        logger.info(message);
        String scores = message.replaceFirst("HISCORES ", "");
        String[] lines = scores.split("\n");

        for(String line: lines){
          String[] parts = line.split(":");
          String name = parts[0];
          int score = Integer.parseInt(parts[1]);

          onlineScores.add(new Pair<>(name, score));
        }
    }));
    communicator.send("HISCORES");
  }

  /**
   * Write scores to the locally stored file
   */
  private void writeScores(){
    try{
      Files.write(Paths.get("scores.txt"),localScores.stream().map(score -> score.getKey() + ":" + score.getValue()).collect(
          Collectors.toList()));
    } catch (IOException e) {
      logger.info("Problem writing scores");
    }
  }

  /**
   * Initialize the ScoreScene
   */
  @Override
  public void initialise() {

  }
}
