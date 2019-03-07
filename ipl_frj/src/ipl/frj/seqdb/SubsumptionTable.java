package ipl.frj.seqdb;

import java.util.Collection;
import java.util.HashMap;

import ipl.frj.sequent.FrjIrregularSequent;

class SubsumptionTable {

  public SubsumptionTable() {
    subsumedIrregular = new HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>>();
  }

  private HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>> subsumedIrregular;
  
  void add(FrjIrregularSequent seq, Collection<FrjIrregularSequent> subsumed) {
    subsumedIrregular.put(seq, subsumed);
  }

  Collection<FrjIrregularSequent> getSubsumedBy(FrjIrregularSequent seq) {
    return subsumedIrregular.get(seq);
  }

  int numberOfSubsumingSequents() {
    return subsumedIrregular.size();
  }
}
