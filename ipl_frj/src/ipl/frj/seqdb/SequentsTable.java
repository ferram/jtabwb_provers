package ipl.frj.seqdb;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic._SequentsTable;
import ipl.frj.util.MSGManager;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.formula.Formula;

/**
 * @author Mauro Ferrari
 *
 */
class SequentsTable implements _SequentsTable {

  private boolean DEBUG = false;

  public SequentsTable(LauncherExecConfiguration configuration) {
    super();
    this.DEBUG = configuration.debugMode();
    this.irregular = new HashMap<Formula, HashSet<FrjIrregularSequent>>();
    this.regular = new HashMap<Formula, HashSet<FrjRegularSequent>>();
    this.allIrregular = new LinkedList<FrjIrregularSequent>();
    this.allRegular = new LinkedList<FrjRegularSequent>();
  }

  int provedRegular = 0, provedIrregular = 0;

  HashMap<Formula, HashSet<FrjIrregularSequent>> irregular;
  HashMap<Formula, HashSet<FrjRegularSequent>> regular;
  LinkedList<FrjIrregularSequent> allIrregular;
  LinkedList<FrjRegularSequent> allRegular;

  /**
   * This method inserts the sequent <code>seq</code> in this table. This method
   * does not check if <code>seq</code> is already in the table or it is
   * subsumed by some method in the table; thus to avoid redundancies you should
   * check these facts with the methods {@link #contains(_FrjSequent)} and
   * {@link #subsumes(_FrjSequent)}.
   * 
   * @param seq the sequent to insert.
   */
  @Override
  public void insert(_FrjSequent provedSequent) {
    Formula right = provedSequent.right();
    switch (provedSequent.type()) {
    case IRREGULAR: {
      HashSet<FrjIrregularSequent> set = irregular.get(right);
      if (set == null) {
        set = new HashSet<FrjIrregularSequent>();
        irregular.put(right, set);
      }
      set.add((FrjIrregularSequent) provedSequent);
      allIrregular.add((FrjIrregularSequent) provedSequent);
      provedIrregular++;
      return;
    }
    case REGULAR: {
      HashSet<FrjRegularSequent> set = regular.get(right);
      if (set == null) {
        set = new HashSet<FrjRegularSequent>();
        regular.put(right, set);
      }
      set.add((FrjRegularSequent) provedSequent);
      allRegular.add((FrjRegularSequent) provedSequent);
      provedRegular++;
      return;
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + provedSequent.type());
    }
  }

  /**
   * Returns true iff this table contains the specified sequent.
   * 
   * @param sequent the sequant to search.
   * @return true iff this table contains the specified sequent.
   */
  @Override
  public boolean contains(_FrjSequent sequent) {
    switch (sequent.type()) {
    case IRREGULAR: {
      FrjIrregularSequent seq = (FrjIrregularSequent) sequent;
      HashSet<FrjIrregularSequent> set = irregular.get(seq.right());
      if (set == null)
        return false;
      else
        return set.contains(seq);
    }
    case REGULAR: {
      FrjRegularSequent seq = (FrjRegularSequent) sequent;
      HashSet<FrjRegularSequent> set = regular.get(seq.right());
      if (set == null)
        return false;
      else
        return set.contains(seq);
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  /**
   * Returns the irregular sequents stored in this table.
   * 
   * @return the irregular sequents stored in this table.
   */
  @Override
  public Collection<FrjIrregularSequent> irregular() {
    return allIrregular.size() == 0 ? null : allIrregular;
  }

  /**
   * Returns the regular sequents stored in this table.
   * 
   * @return the regular sequents stored in this table.
   */
  @Override
  public Collection<FrjRegularSequent> regular() {
    return allRegular.size() == 0 ? null : allRegular;
  }

  /**
   * Returns the irregular sequents with the specified right formula stored in
   * this table.
   * 
   * @return the irregular sequents with the specified right formula stored in
   * this table.
   */
  @Override
  public Collection<FrjIrregularSequent> irregularWithRight(Formula wff) {
    return irregular.get(wff);
  }

  /**
   * Returns the regular sequents with the specified right formula stored in
   * this table.
   * 
   * @return the regular sequents with the specified right formula stored in
   * this table.
   */
  @Override
  public Collection<FrjRegularSequent> regularWithRight(Formula wff) {
    return regular.get(wff);
  }

  /**
   * Returns the number of sequents (regular and irregular) stored in this
   * table.
   * 
   * @return the number of sequents stored in this table.
   */
  @Override
  public int numberOfProvedSequents() {
    return provedIrregular + provedRegular;
  }

  @Override
  public Collection<FrjIrregularSequent> subsumedIrregular(FrjIrregularSequent seq) {
    LinkedList<FrjIrregularSequent> result = new LinkedList<FrjIrregularSequent>();
    for (FrjIrregularSequent old : allIrregular)
      if (seq.subsumes(old))
        result.add(old);

    return result.size() == 0 ? null : result;
  }

  @Override
  public Collection<FrjRegularSequent> subsumedRegular(FrjRegularSequent seq) {
    LinkedList<FrjRegularSequent> result = new LinkedList<FrjRegularSequent>();
    for (FrjRegularSequent old : allRegular)
      if (seq.subsumes(old))
        result.add(old);

    return result.size() == 0 ? null : result;
  }

  @Override
  public boolean subsumes(_FrjSequent newSequent) {
    switch (newSequent.type()) {
    case IRREGULAR: {
      FrjIrregularSequent newSeq = (FrjIrregularSequent) newSequent;
      HashSet<FrjIrregularSequent> set = irregular.get(newSeq.right());
      if (set == null)
        return false;
      else {
        for (FrjIrregularSequent inTable : set) {
          if (inTable.subsumes(newSeq))
            return true;
        }
        return false;
      }
    }
    case REGULAR: {
      FrjRegularSequent newSeq = (FrjRegularSequent) newSequent;
      HashSet<FrjRegularSequent> set = regular.get(newSeq.right());
      if (set == null)
        return false;
      else {
        for (FrjRegularSequent inTable : set)
          if (inTable.subsumes(newSeq))
            return true;
        return false;
      }
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

  public boolean remove(_FrjSequent seq) {
    switch (seq.type()) {
    case REGULAR: {
      boolean isRemoved = allRegular.remove(seq);
      regular.get(seq.right()).remove(seq);
      if (isRemoved)
        provedRegular--;
      if (DEBUG)
        MSGManager.debug(MSG.BW_ITERATION_TABLE.DEBUG.SUBSUMED_REMOVED, seq.format());
      return isRemoved;
    }
    case IRREGULAR: {
      boolean isRemoved = allIrregular.remove(seq);
      irregular.get(seq.right()).remove(seq);
      if (isRemoved)
        provedIrregular--;
      if (DEBUG)
        MSGManager.debug(MSG.BW_ITERATION_TABLE.DEBUG.SUBSUMED_REMOVED, seq.format());
      return isRemoved;
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + seq.type());
    }

  }

  /**
   * Removes from this table all its sequents that are contained in the
   * specified collection.
   * 
   * @param sequents the collection containing the sequents to be removed from
   * this tale.
   * @return the number of sequents removed from this table.
   */
  public int removeAll(Collection<_FrjSequent> sequents) {
    int result = 0;
    for (_FrjSequent seq : sequents)
      result += remove(seq) ? 1 : 0;
    return result;
  }

  @Override
  public String toString() {
    return toStringBuilderIrregular().toString() + //
        toStringBuilderRegular().toString(); //
  }

  private StringBuilder toStringBuilderRegular() {
    StringBuilder strb = new StringBuilder();
    if (!regular.isEmpty()) {
      for (Formula wff : regular.keySet())
        for (FrjRegularSequent seq : regular.get(wff))
          strb.append(sequentDetails(seq) + "\n");
    }
    return strb;
  }

  private StringBuilder toStringBuilderIrregular() {
    StringBuilder strb = new StringBuilder();
    if (!irregular.isEmpty()) {
      for (Formula wff : irregular.keySet())
        for (FrjIrregularSequent seq : irregular.get(wff))
          strb.append(sequentDetails(seq) + "\n");
    }
    return strb;
  }

  private String sequentDetails(_FrjSequent seq) {
    return String.format(MSG.VERBOSE.SEQUENT_DETAILS, seq.generatingRule().name(), seq.format());
  }

}
