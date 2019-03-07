package ipl.frj.seqdb;

import java.util.Collection;
import java.util.HashMap;

import ipl.frj.sequent.FrjIrregularSequent;

public class IterationSubsumedTableIrregular {

  public IterationSubsumedTableIrregular() {
    this.subsumedTable = new HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>>();
    this.subsumedBy = new HashMap<FrjIrregularSequent, FrjIrregularSequent>();
  }

  // maps seq to the sequents it subsumes
  private HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>> subsumedTable;

  // map an Irregular sequent to the sequent subsuming it
  private HashMap<FrjIrregularSequent, FrjIrregularSequent> subsumedBy;

  public void addSubsumedBy(FrjIrregularSequent seq, Collection<FrjIrregularSequent> subsumed) {
    subsumedTable.put(seq, subsumed);
    // update global table
    for (FrjIrregularSequent subsumedSeq : subsumed)
      subsumedBy.put(subsumedSeq, seq);
  }

  public String toString() {
    if (subsumedTable.isEmpty())
      return "<null>";
    String s = "{";
    for (FrjIrregularSequent subsuming : subsumedTable.keySet()) {
      s += subsuming.getSequentProgessiveNumber() + " --> (";
      for (FrjIrregularSequent subsumed : subsumedTable.get(subsuming))
        s += subsumed.getSequentProgessiveNumber() + " ";
      s += ")";
    }
    return s + "}";
  }
}
