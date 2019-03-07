package ipl.frj.seqdb;

import java.util.HashMap;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwb.util.ImplementationError;

/**
 * A sequent table storing subsumptions relations.
 * 
 * @author Mauro Ferrari
 *
 */
public class SequentsTableWithSubsumpionMaps extends SequentsTable {

  /**
   * Build an empty table.
   * 
   * @param configuration the execution configuration.
   */
  public SequentsTableWithSubsumpionMaps(LauncherExecConfiguration configuration) {
    super(configuration);
    this.regularSubsumptionMap = new HashMap<FrjRegularSequent, FrjRegularSequent>();
    this.irregularSubsumptionMap = new HashMap<FrjIrregularSequent, FrjIrregularSequent>();

  }

  private HashMap<FrjRegularSequent, FrjRegularSequent> regularSubsumptionMap;
  private HashMap<FrjIrregularSequent, FrjIrregularSequent> irregularSubsumptionMap;

  /**
   * Add the specified pair to this subsumption map.
   * 
   * @param subsumed the sequent subsumed.
   * @param subsuming the subsuming sequent.
   */
  public void addSubsumption(_FrjSequent subsumed, _FrjSequent subsuming) {
    switch (subsumed.type()) {
    case REGULAR:
      addRegularSubsumption((FrjRegularSequent) subsumed, (FrjRegularSequent) subsuming);
      return;
    case IRREGULAR:
      addIrregularSubsumption((FrjIrregularSequent) subsumed, (FrjIrregularSequent) subsuming);
      return;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + subsumed.type());
    }
  }

  // The regular casted variant of generic method.
  private void addRegularSubsumption(FrjRegularSequent subsumed, FrjRegularSequent subsuming) {
    this.regularSubsumptionMap.put(subsumed, subsuming);
  }

  // The irregular casted variant of generic method.
  private void addIrregularSubsumption(FrjIrregularSequent subsumed, FrjIrregularSequent subsuming) {
    this.irregularSubsumptionMap.put(subsumed, subsuming);
  }

  /**
   * Returns the first irregular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the irregular sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public FrjIrregularSequent getFirstIrregularSubsuming(FrjIrregularSequent seq) {
    return irregularSubsumptionMap.get(seq);
  }

  /**
   * Returns the first regular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the regular sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public FrjRegularSequent getFirstRegularSubsuming(FrjRegularSequent seq) {
    return regularSubsumptionMap.get(seq);
  }

  /**
   * Returns the first sequent subsuming <code>seq</code> inserted in this table
   * or <code>null</code> if <code>seq</code> has no subsuming sequents in this
   * table.
   * 
   * @param seq the sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public _FrjSequent getFisrtSubsuming(_FrjSequent seq) {
    switch (seq.type()) {
    case REGULAR:
      return getFirstRegularSubsuming((FrjRegularSequent) seq);
    case IRREGULAR:
      return getFirstIrregularSubsuming((FrjIrregularSequent) seq);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + seq.type());
    }
  }

  /**
   * Returns the maximal irregular sequent subsuming <code>seq</code> inserted
   * in this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the irregular sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public FrjIrregularSequent getMaximalIrregularSubsuming(FrjIrregularSequent seq) {
    FrjIrregularSequent next, result = seq;
    while ((next = irregularSubsumptionMap.get(result)) != null)
      result = next;

    return result;
  }

  /**
   * Returns the maximal regular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the regular sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public FrjRegularSequent getMaximalRegularSubsuming(FrjRegularSequent seq) {
    FrjRegularSequent next, result = seq;
    while ((next = regularSubsumptionMap.get(result)) != null)
      result = next;

    return result;
  }

  /**
   * Returns the maximal sequent subsuming <code>seq</code> inserted in this
   * table or <code>null</code> if <code>seq</code> has no subsuming sequents in
   * this table.
   * 
   * @param seq the sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public _FrjSequent getMaximalSubsuming(_FrjSequent seq) {
    switch (seq.type()) {
    case REGULAR:
      return getMaximalRegularSubsuming((FrjRegularSequent) seq);
    case IRREGULAR:
      return getMaximalIrregularSubsuming((FrjIrregularSequent) seq);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + seq.type());
    }
  }

}
