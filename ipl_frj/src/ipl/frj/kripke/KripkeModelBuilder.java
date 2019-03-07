package ipl.frj.kripke;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import ipl.frj.rules._FrjRule;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import ipl.frj.util.MSGManager;
import jtabwb.util.ImplementationError;

public class KripkeModelBuilder {

  // to generate debug info set DEBUG to true
  boolean DEBUG = false;

  private HashMap<FrjRegularSequent, World> worlds;
  private HashMap<Integer, LinkedList<World>> worldsByLevel;
  private int maxLevel = -1;
  private _ProvedSequentsDB db;
  private _ProvedSequentsDBWithBwSubsumption dbWithSubsumptions;

  public KripkeModelBuilder(_ProvedSequentsDB db) {
    this.db = db;
    this.worlds = new HashMap<FrjRegularSequent, World>();
    this.worldsByLevel = new HashMap<Integer, LinkedList<World>>();
    if (db instanceof _ProvedSequentsDBWithBwSubsumption)
      dbWithSubsumptions = (_ProvedSequentsDBWithBwSubsumption) db;
    else
      dbWithSubsumptions = null;
  }

  public KripkeModel build() {
    FrjRegularSequent provedSequent = db.getSuccessSequents().iterator().next();
    if (DEBUG)
      MSGManager.debug(MSG.KRIPKE_MODEL_BUILDER.DEBUG.BEGIN);

    LinkedList<World> built = _build(provedSequent);
    if (built.size() != 1)
      throw new ImplementationError("This list should contain only one node");

    // remove duplicate paths
    World root = built.getFirst();
    prune(root);

    LinkedList<World>[] array = new LinkedList[maxLevel + 1];
    for (int i = 0; i < array.length; i++)
      array[i] = worldsByLevel.get(i);

    KripkeModel model = new KripkeModel(built.getFirst(), array);
    if (DEBUG)
      MSGManager.debug(MSG.KRIPKE_MODEL_BUILDER.DEBUG.END, model.toString());

    return model;
  }

  // Removes duplicated path from the model with world as root.
  private void prune(World world) {
    if (world.successors() == null)
      return;
    World[] array = world.successors().toArray(new World[world.successors().size()]);
    for (int i = 0; i < array.length; i++)
      for (int j = 0; j < array.length; j++)
        if (i != j && array[j].coneWorlds() != null && array[j].coneWorlds().contains(array[i])) {
          while (world.successors().contains(array[i]))
            world.successors().remove(array[i]);
        }

    for (World succ : world.successors())
      prune(succ);
  }

  // Builds a new world. 
  private World addWorld(FrjRegularSequent sequent, HashSet<World> successors, int level) {

    World w = new World(sequent, successors, level);
    worlds.put(sequent, w);

    if (DEBUG)
      MSGManager.debug(MSG.KRIPKE_MODEL_BUILDER.DEBUG.WORLD_ADDED, w.getIdx(),
          w.getSequent().format(), w.toStringSuccessors(), level);

    LinkedList<World> worldsAtLevel = worldsByLevel.get(level);
    if (worldsAtLevel == null) {
      worldsAtLevel = new LinkedList<World>();
      worldsByLevel.put(level, worldsAtLevel);
      maxLevel = level;
    }
    worldsAtLevel.add(w);
    return w;
  }

  // recursively build the model for the specified proved
  private LinkedList<World> _build(_FrjSequent provedSequent) {
    if (DEBUG)
      MSGManager.debug(MSG.KRIPKE_MODEL_BUILDER.DEBUG.SEQUENT_DETAILS,
          provedSequent.getSequentProgessiveNumber(), provedSequent.format(),
          provedSequent.generatingRule().name());
    _FrjRule appliedRule = provedSequent.generatingRule();
    switch (appliedRule.getID()) {
    case AX_IRR:
      return new LinkedList<World>();
    case AX_REG: {
      FrjRegularSequent maximal = getMaximalSubsumingRegular((FrjRegularSequent)provedSequent);
      World w = worlds.get(maximal);
      if (w == null) {
        w = new World((FrjRegularSequent) maximal, null, 0);
        addWorld(maximal, null, 0);
      }
      LinkedList<World> result = new LinkedList<World>();
      result.add(w);
      return result;
    }
    case JOIN_ATOMIC:
    case JOIN_DISJUNCTION: {
      FrjRegularSequent maximal = getMaximalSubsumingRegular((FrjRegularSequent)provedSequent);
      World w = worlds.get(maximal);
      if (w == null) {
        HashSet<World> successors = new HashSet<World>();
        for (_FrjSequent premise : appliedRule.premises()) {
          successors.addAll(_build(premise));
        }
        int successorsMaxlevel = -1;
        for (World succW : successors)
          if (successorsMaxlevel < succW.level())
            successorsMaxlevel = succW.level();
        w = addWorld((FrjRegularSequent) maximal, successors, successorsMaxlevel + 1);
        worlds.put((FrjRegularSequent) maximal, w);

        // set this node as predecessor of its successors
        for (World succ : successors)
          succ.addPredecessor(w);

      }
      LinkedList<World> result = new LinkedList<World>();
      result.add(w);
      return result;
    }
    case AND_IRR:
    case AND_REG:
    case IMP_IRR_IN_CL:
    case IMP_REG_IN_CL:
    case IMP_REG_NOT_IN_CL:
    case OR_IRR: {
      LinkedList<World> result = new LinkedList<World>();
      for (_FrjSequent premise : appliedRule.premises())
        result.addAll(_build(premise));
      return result;
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          appliedRule.getID());
    }
  }

  private FrjRegularSequent getMaximalSubsumingRegular(FrjRegularSequent seq) {
    if (dbWithSubsumptions == null)
      return seq;
    else
      return (FrjRegularSequent) dbWithSubsumptions.getMaximalSubsuming(seq);
  }

  private FrjIrregularSequent getMaximalSubsumingIrregular(FrjIrregularSequent seq) {
    if (dbWithSubsumptions == null)
      return seq;
    else
      return (FrjIrregularSequent) dbWithSubsumptions.getMaximalSubsuming(seq);
  }

}
