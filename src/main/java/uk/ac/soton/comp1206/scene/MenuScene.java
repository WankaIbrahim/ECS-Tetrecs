package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow, boolean isActive) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
        if(!isActive){
            Multimedia.playBackgroundMusic("menu.mp3");
        }
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);


        var title = new ImageView(new Image(
            Objects.requireNonNull(this.getClass().getResource("/images/TetrECS.png")).toExternalForm()));
        title.setFitWidth(600);
        title.setFitHeight(120);

        HBox titleBox = new HBox(title);
        titleBox.setPadding(new Insets(80,0,0,0));
        titleBox.setAlignment(Pos.BOTTOM_CENTER);
        mainPane.setTop(titleBox);

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





        var menuItemsBox = new VBox();
        menuItemsBox.setAlignment(Pos.CENTER);

        var singlePlayer = new Text("Single Player");
        singlePlayer.getStyleClass().add("menuItem");

        var multiPlayer = new Text("Multi Player");
        multiPlayer.getStyleClass().add("menuItem");

        var howToPlay = new Text("How to Play");
        howToPlay.getStyleClass().add("menuItem");

        var exit = new Text("Exit");
        exit.getStyleClass().add("menuItem");

        menuItemsBox.getChildren().addAll(singlePlayer,multiPlayer,howToPlay,exit);
        mainPane.setCenter(menuItemsBox);

        //Bind the button action to the startGame method in the menu
        singlePlayer.setOnMouseEntered(e-> singlePlayer.getStyleClass().add("menuItem:hover"));
        multiPlayer.setOnMouseEntered(e-> multiPlayer.getStyleClass().add("menuItem:hover"));
        howToPlay.setOnMouseEntered(e-> howToPlay.getStyleClass().add("menuItem:hover"));
        exit.setOnMouseEntered(e-> exit.getStyleClass().add("menuItem:hover"));

        singlePlayer.setOnMouseClicked(this::startGame);
        multiPlayer.setOnMouseClicked(this::startMultiPlayer);
        howToPlay.setOnMouseClicked(this::displayHowToPlay);
        exit.setOnMouseClicked(e-> System.exit(0));
    }

    /**
     * Initialize the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Start the challenge scene
     */
    private void startGame(MouseEvent event) {
        gameWindow.startChallenge();
    }

    /**
     * Start the multiplayerScene
     */
    private void startMultiPlayer(MouseEvent event){
        gameWindow.startMultiplayerMenu();
    }

    /**
     *Start the howToPlayscene
     */
    private void displayHowToPlay(MouseEvent event){
        gameWindow.startHowToPlayScene();
    }

}
