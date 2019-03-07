package ipl.frj.kripke;

import java.util.LinkedList;

/**
 * A Kripke model.
 * 
 * @author Mauro Ferrari
 *
 */
public class KripkeModel {

  private World root;
  private LinkedList<World>[] wordsByLevel;
  private int numberOfWorlds = 0;

  KripkeModel(World root, LinkedList<World>[] wordsByLevel) {
    this.root = root;
    this.wordsByLevel = wordsByLevel;
    for (int i = 0; i < wordsByLevel.length; i++)
      numberOfWorlds += wordsByLevel[i].size();
  }

  /**
   * Returns the max level of this model.
   * 
   * @return the max level of this model.
   */
  public int maxLevel() {
    return wordsByLevel.length - 1;
  }

  /**
   * Returns the depth of this model.
   * 
   * @return the depth of this model.
   */
  public int depth() {
    return maxLevel();
  }

  /**
   * Returns the number of worlds.
   * 
   * @return the number of worlds.
   */
  public int numberOfWorlds() {
    return numberOfWorlds;
  }

  /**
   * Returns the list of the worlds of level <code>i</code>.
   * 
   * @param i the level
   * @return the list of the worlds of level <code>i</code>.
   */
  public LinkedList<World> worldsOfLevel(int i) {
    return wordsByLevel[i];
  }

  /**
   * Returns the root of this model.
   * 
   * @return the root of this model.
   */
  public World root() {
    return root;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int level = 0; level <= maxLevel(); level++) {
      for (World w : wordsByLevel[level])
        sb.append(String.format(MSG.KRIPKE_MODEL.DEBUG.WORLD_COMPLETE, level, w.getIdx(),
            w.getSequent().format(), w.toStringSuccessors(), w.toStringPredecessors()));
    }

    return sb.toString();
  }

}
