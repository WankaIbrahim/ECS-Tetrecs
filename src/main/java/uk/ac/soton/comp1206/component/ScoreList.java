package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * Holds the scores to be displayed at the end of a single player challenge
 */
public class ScoreList extends VBox {

  /**
   * The scores to be displayed
   */
  private final SimpleListProperty<Pair<String, Integer>> scores;

  /**
   * Used to change the opacity of the nodes within
   */
  private Double opacity = 0.0;

  /**
   * Create the VBox and add a listener to the SimpleListProperty
   */
  public ScoreList(){
    scores = new SimpleListProperty<>();
    updateDisplay();
    scores.addListener((observable, oldScores, newScores)-> updateDisplay());
  }

  /**
   * Update the display when the scores have been updated
   */
  public void updateDisplay(){
    getChildren().clear();
    for(Pair<String, Integer> score: scores) {
      var sc = new Text(score.getKey() + ": " + score.getValue());
      sc.getStyleClass().add("scoreitem");
      getChildren().add(sc);
    }
    reveal();
  }

  /**
   * Adds a fade-in effect to the nodes by altering their opacity
   */
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

  /**
   * Get the SimpleListProperty that holds the scores
   * @return The SimpleListProperty that holds the scores
   */
  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }
}
