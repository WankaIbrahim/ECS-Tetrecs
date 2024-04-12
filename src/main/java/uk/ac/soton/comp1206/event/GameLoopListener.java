package uk.ac.soton.comp1206.event;

/**
 * Listens for when the game loop is updated
 */
public interface GameLoopListener {

  /**
   * Called when the game loop is updated.
   */
  void gameLoop();
}
