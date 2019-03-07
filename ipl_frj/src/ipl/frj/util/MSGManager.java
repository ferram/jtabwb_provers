package ipl.frj.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TODO: doc Build a simple message manager.
 * 
 * @author Mauro Ferrari
 *
 */
public class MSGManager {

  public MSGManager(String prefix) {
    super();
    this.PREFIX = prefix;
  }

  private final String PREFIX;

  public void print(StringBuilder sb) {
    String[] lines = sb.toString().split("\\n");
    for (String line : lines)
      System.out.println(PREFIX + line);
  }

  public void print(String msg, Object... args) {
    String str = String.format(msg, args);
    String[] lines = str.toString().split("\\n");
    for (String line : lines)
      System.out.println(PREFIX + line);
  }

  private static String ERROR = "ERROR -- ";
  private static String DEBUG = "DEBUG -- ";
  //private static String PROVER_INFO = "** ";

  public static void info(String key, Object... args) {
    System.out.println(String.format(key, args));
  }

  public static void infoNoLn(String key, Object... args) {
    System.out.print(String.format(key, args));
  }

  public static void debug(String key, Object... args) {
    System.out.println(DEBUG + String.format(key, args));
  }

  public static void debugNoLn(String key, Object... args) {
    System.out.print(DEBUG + String.format(key, args));
  }

  
  //  public static void prover_info(String key, Object... args) {
  //    System.out.println(PROVER_INFO + String.format(key, args));
  //  }

  public static void error(String key, Object... args) {
    System.out.println(ERROR + String.format(key, args));
  }

  /**
   * Write on <code>file</code> the given message; if the file exists its
   * content is overwritten.
   * 
   * @param file the output file.
   * @param msg the message to write.
   */
  static public void writeToFile(File file, String msg) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
      bw.write(msg);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Write the specified message at the end of <code>file</code>.
   * 
   * @param file the output file.
   * @param msg the message to write.
   */
  public void addToFile(File file, String msg) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
      bw.write(msg);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

}
