package uk.ac.soton.comp1206.scene;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;
import uk.ac.soton.comp1206.utilities.ResourceBundleHolder;

/**
 * The settings menu of the game
 */
public class SettingScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);


  /**
   * Create a new setting menu
   * @param gameWindow the Game Window this will be displayed in
   */
  public SettingScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Setting Scene");
  }

  /**
   * Build the menu layout
   */
  @Override
  public void build() {
    logger.info("Building {}", this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    mainPane.setFocusTraversable(true);
    mainPane.setOnKeyPressed(e->{
      if(e.getCode()== KeyCode.ESCAPE){
        gameWindow.startMenu(true);
      }
    });

    Text settings = new Text(ResourceBundleHolder.getResourceBundle().getString("settings"));
    settings.getStyleClass().add("heading");
    var titleBox = new HBox(settings);
    titleBox.setAlignment(Pos.CENTER);
    mainPane.setTop(titleBox);

    Text volumeBarHeader = new Text(ResourceBundleHolder.getResourceBundle().getString("volumeBar"));
    volumeBarHeader.getStyleClass().add("channelItem");
    Slider volumeSlider = new Slider(0, 1, 0.5);
    volumeSlider.setBlockIncrement(0.05);
    volumeSlider.setPrefWidth(200);
    volumeSlider.setMaxWidth(200);
    volumeSlider.setMinWidth(200);
    volumeSlider.valueProperty().bindBidirectional(Multimedia.volumeProperty());
    volumeSlider.valueProperty().addListener(e->{
      Multimedia.resetVolume();
    });
    var volumeBox = new VBox();
    volumeBox.getChildren().addAll(volumeBarHeader,volumeSlider);
    volumeBox.setAlignment(Pos.CENTER);


    Text changeLanguage = new Text(ResourceBundleHolder.getResourceBundle().getString("changeLanguage"));
    changeLanguage.getStyleClass().add("channelItem");
    ObservableList<String> languages = FXCollections.observableArrayList("en", "pl", "fr", "de", "es");
    ComboBox<String> languageDropdown = new ComboBox<>(languages);
    languageDropdown.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
      ResourceBundleHolder.setLanguage(newValue);
      gameWindow.startSettingMenuScene();
    });
    var changeLanguageBox = new VBox(changeLanguage, languageDropdown);
    changeLanguageBox.setAlignment(Pos.CENTER);

    var centerBox = new VBox(volumeBox,changeLanguageBox);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setSpacing(30);
    mainPane.setCenter(centerBox);
  }

  /**
   * Initialize the setting menu
   */
  @Override
  public void initialise() {

  }
}
