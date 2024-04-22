package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multiplayer Lobby
 */
public class LobbyScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(LobbyScene.class);

  /**
   * The communicator associated with the lobby
   */
  private final Communicator communicator;

  /**
   * The main pain of the lobby scene
   */
  private BorderPane mainPane;

  /**
   * A list of available channels to be joined
   */
  private VBox channelList;

  /**
   * The list of all current users in a channel
   */
  private VBox userList;

  /**
   * True when in a channel false otherwise
   */
  private Boolean inChannel = false;

  /**
   * Holds the messages received and sent in the chat box
   */
  private final ListView<String> messages = new ListView<>();

  /**
   * Create a new Multiplayer Lobby
   * @param gameWindow the Game Window associated with the lobby
   */
  public LobbyScene(GameWindow gameWindow, Communicator communicator) {
    super(gameWindow);
    this.communicator = communicator;
    communicator.send("PART");
    logger.info("Creating Multiplayer Menu");
  }

  /**
   * Build the lobby layout
   */
  @Override
  public void build() {
    logger.info("Building {}", this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var multiplayerPane = new StackPane();
    multiplayerPane.setMaxWidth(gameWindow.getWidth());
    multiplayerPane.setMaxHeight(gameWindow.getHeight());
    multiplayerPane.getStyleClass().add("menu-background");
    root.getChildren().add(multiplayerPane);

    mainPane = new BorderPane();
    multiplayerPane.getChildren().add(mainPane);
    mainPane.setFocusTraversable(true);
    mainPane.setOnKeyPressed(e->{
      if(e.getCode()==KeyCode.ESCAPE){
        if(inChannel){
          communicator.send("PART");
          inChannel=false;
          communicator.send("LIST");
          userList.getChildren().clear();
          mainPane.setRight(null);
        }else{
          gameWindow.startMenu(true);
        }
      }
    });

    communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));

    mainPane.setLeft(createLeftBox());


  }

  /**
   * Create the left ui components of the lobby scene
   * @return The VBox containing the ui components
   * to be displayed on the left of the lobby scene
   */
  private VBox createLeftBox() {
    var textField = new TextField();
    textField.setPromptText("HOST NEW GAME");
    var sendButton = new Button("Send");
    sendButton.setOnAction(e-> {
      communicator.send("CREATE "+textField.getText());
      communicator.send("LIST");
      textField.clear();
    });
    textField.setOnKeyPressed(e->{
      if(e.getCode()== KeyCode.ENTER){
        communicator.send("CREATE "+textField.getText());
        communicator.send("LIST");
        textField.clear();
      }
    });
    var messageBox = new HBox(textField,sendButton);

    channelList = new VBox();
    var title = new Text("OPEN CHANNELS");
    title.getStyleClass().add("title");
    channelList.getChildren().add(title);

    userList = new VBox();


    communicator.send("LIST");

    var leftBox = new VBox(channelList,messageBox,userList);
    leftBox.setPadding(new Insets(0, 0, 0, 10));
    return leftBox;
  }

  /**
   * Create the right ui components of the lobby scene
   * @return The VBox containing the ui components
   * to be displayed on the right of the lobby scene
   */
  private VBox createRightBox(){
    var messageField = new TextField();
    messageField.setPromptText("Send Message");
    messageField.setOnKeyPressed(e->{
      if(e.getCode()==KeyCode.ENTER){
        if(Objects.equals(messageField.getText(), "/nick")){
          Dialog<String> nicknameDialog = new Dialog<>();
          TextField nickname = new TextField();
          nickname.setPromptText("Enter your nickname");
          nickname.setFocusTraversable(false);
          Button nicknameSend = new Button("Confirm");

          VBox vBox = new VBox(10);
          vBox.getChildren().addAll(nickname, nicknameSend);

          nicknameDialog.setTitle("Change Nickname");
          nicknameDialog.getDialogPane().setContent(vBox);
          nicknameDialog.show();

          nicknameSend.setOnAction((sent) -> {
            communicator.send("NICK "+ nickname.getText());
            nicknameDialog.setResult(nickname.getText());
            nickname.clear();
            messageField.clear();
            nicknameDialog.close();
          });

          nickname.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
              communicator.send("NICK "+ nickname.getText());
              nicknameDialog.setResult(nickname.getText());
              nickname.clear();
              messageField.clear();
              nicknameDialog.close();
            }
          });
        }else{
          communicator.send("MSG "+messageField.getText());
          messageField.clear();
        }
      }
    });

    var sendButton = new Button("SEND");
    sendButton.setOnAction(e->{
      communicator.send("MSG "+ messageField.getText());
      messageField.clear();
    });

    var startButton = new Button("START GAME");
    startButton.setOnAction(e->{
      communicator.send("START");
      gameWindow.startMultiplayerGame(communicator);
    });

    var rightBox = new VBox(messages, new HBox(startButton,messageField,sendButton));
    messages.getStyleClass().add("messagePane");
    messages.setOpacity(0.4);
    return rightBox;
  }

  /**
   * Handles the receiving of communications from the communicator
   * @param communication The communication received
   */
  private void receiveMessage(String communication){
    if (communication.split(" ")[0].equals("CHANNELS")&&!inChannel) {
      channelList.getChildren().clear();
      logger.info("Channel List Cleared");
      var title = new Text("OPEN CHANNELS");
      title.getStyleClass().add("title");
      channelList.getChildren().add(title);
      logger.info("Title added");
      String channels = communication.replaceFirst("CHANNELS ", "");
      logger.info(channels);
      String[] lines = channels.split("\n");

      for (String line : lines) {
        logger.info("{} is a channel", line);
        var channel = new Text(line);
        channel.getStyleClass().add("lobbyitem");
        channel.setOnMouseClicked(e-> communicator.send("JOIN "+channel.getText()));
        channel.setOnMouseEntered(event -> channel.getStyleClass().add("lobbyitem:hover"));
        channelList.getChildren().add(channel);
      }
    }

    if (communication.split(" ")[0].equals("USERS")) {
      userList.getChildren().clear();
      logger.info("User List Cleared");
      var userTitle = new Text("USERS");
      userTitle.getStyleClass().add("title");
      userList.getChildren().add(userTitle);
      logger.info("Title added");
      String users = communication.replaceFirst("USERS ", "");
      logger.info(users);
      String[] lines = users.split("\n");

      for (String line : lines) {
        logger.info("{} is a user", line);
        var user = new Text(line);
        user.getStyleClass().add("lobbyitem2");
        userList.getChildren().add(user);
      }
    }

    if (communication.split(" ")[0].equals("JOIN")) {
      inChannel = true;
      mainPane.setRight(createRightBox());
      channelList.getChildren().clear();
      logger.info("Channel List Cleared");
      var channelTitle = new Text("CURRENT CHANNEL");
      channelTitle.getStyleClass().add("title");
      channelList.getChildren().add(channelTitle);
      logger.info("Title added");
      String channel = communication.replaceFirst("JOIN ", "");

      logger.info("{} is the current channel", channel);
      var currentChannel = new Text(channel);
      currentChannel.getStyleClass().add("scoreitem");
      channelList.getChildren().add(currentChannel);
    }

    if (communication.split(" ")[0].equals("MSG")) {
      var message = communication.replaceFirst("MSG ", "");
      logger.info(message);
      messages.getItems().add(message);
    }

    if (communication.split(" ")[0].equals("ERROR")) {
      var errorMessage = communication.replaceFirst("ERROR ", "");
      logger.error(errorMessage);
    }
  }

  /**
   * Initialize the multiplayer lobby
   */
  @Override
  public void initialise() {

  }
}
