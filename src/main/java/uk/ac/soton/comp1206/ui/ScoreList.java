package uk.ac.soton.comp1206.ui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoreList.class);

  /**
   * The scores to be displayed
   */
  private final SimpleListProperty<Pair<String, Integer>> scores;

  private Double opacity = 0.0;

  /**
   * Create the VBox and add a listener to the SimpleListProperty
   */
  public ScoreList(){
    scores = new SimpleListProperty<>();
    updateDisplay();
    scores.addListener((observable, oldScores, newScores)-> updateDisplay());
  }

  public void updateDisplay(){
    getChildren().clear();
    for(Pair<String, Integer> score: scores) {
      var sc = new Text(score.getKey() + ": " + score.getValue());
      sc.getStyleClass().add("scoreitem");
      getChildren().add(sc);
    }
    reveal();
  }

  private void reveal(){
    for(Node t: getChildren()){
      AnimationTimer at = new AnimationTimer() {
        @Override
        public void handle(long l) {
          opacity+=0.0001;
          opacity=Math.min(1, opacity);
          t.opacityProperty().set(opacity);
        }
      };
      opacity=0.0;
      at.start();
    }
  }

  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }
}
