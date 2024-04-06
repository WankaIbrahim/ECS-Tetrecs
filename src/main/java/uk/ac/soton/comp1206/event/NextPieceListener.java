package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener is used to handle the event when the current piece is changed.
 */
public interface NextPieceListener {

  /**
   * Handle the updating of the piece board
   *
   * @param piece The new piece
   */
  void nextPiece(GamePiece piece);
}
