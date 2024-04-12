package uk.ac.soton.comp1206.utilities;

import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles the playing of all media
 */
public class Multimedia {

  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  /**
   * Media player used for audio
   */
  private static MediaPlayer audioPlayer;

  /**
   * Media player used for music
   */
  private static MediaPlayer musicPlayer;

  /**
   * Plays a given audio file
   *
   * @param audioPath The path to the audio file to be played
   */
  public static void playAudio(String audioPath) {
//    String toPlay = Objects.requireNonNull(Multimedia.class.getResource("/sounds/" + audioPath))
//        .toExternalForm();
//    audioPlayer = new MediaPlayer(new Media(toPlay));
//    audioPlayer.play();
//
//    logger.info("Playing audio {}", audioPath);
  }

  /**
   * Stop the current audio
   */
  public static void stopAudio(){
//    audioPlayer.stop();
  }

  /**
   * Plays a given audio file in the background on loop
   *
   * @param audioPath The path to the audio file to be played
   */
  public static void playBackgroundMusic(String audioPath) {
//    String toPlay = Objects.requireNonNull(Multimedia.class.getResource("/music/" + audioPath))
//        .toExternalForm();
//    musicPlayer = new MediaPlayer(new Media(toPlay));
//    musicPlayer.cycleCountProperty().set(Integer.MAX_VALUE);
//    musicPlayer.play();
//    logger.info("Playing music {}", audioPath);
  }

  /**
   * Stop the audio file being played in the background
   */
  public static void stopBackgroundMusic() {
//    musicPlayer.stop();
  }

}
