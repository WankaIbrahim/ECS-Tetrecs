package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Listens for when a line has been cleared
 */
public interface BlockClearedListener {

  /**
   * Called after a line has been cleared
   * @param blocksToBeCleared A set of GameBLockCoordinate's which hold the coordinates of the GameBlocks that were part of the lines cleared
   */
  void lineCleared(Set<GameBlockCoordinate> blocksToBeCleared);

}
