package uk.ac.soton.comp1206.event;

/**
 * The Swap Piece listener is used
 * to handle the event when the current and next piece is to be swapped.
 */
public interface SwapPieceListener {

  /**
   * Handle the swapping of the current and next piece
   */
  void swapPiece();
}
