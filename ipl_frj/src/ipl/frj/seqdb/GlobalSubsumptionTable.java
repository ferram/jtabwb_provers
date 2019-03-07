package ipl.frj.seqdb;

import java.util.HashMap;

import ipl.frj.sequent.FrjIrregularSequent;

public class GlobalSubsumptionTable {

  public GlobalSubsumptionTable() {
    this.irregularSubsumedMap = new HashMap<FrjIrregularSequent, FrjIrregularSequent>();
  }

  // map an Irregular sequent to the sequent subsuming it
  private HashMap<FrjIrregularSequent, FrjIrregularSequent> irregularSubsumedMap;

  /**
   * Add a pair to the irregular subsumed map.
   * 
   * @param subsumed the subsumed sequent.
   * @param subsuming the sequent subsuming it.
   */
  void putSubsumed(FrjIrregularSequent subsumed, FrjIrregularSequent subsuming) {
    this.irregularSubsumedMap.put(subsumed, subsuming);
  }

}
