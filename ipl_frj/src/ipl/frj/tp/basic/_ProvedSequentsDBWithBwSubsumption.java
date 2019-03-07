package ipl.frj.tp.basic;

import java.util.HashMap;

import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;

/**
 * Interface specifying the DB of proved sequents with backward subsumption.
 * 
 * @author Mauro Ferrari
 *
 */
public interface _ProvedSequentsDBWithBwSubsumption extends _ProvedSequentsDB {

  //  /**
  //   * Returns the object mapping an irregular sequent to the sequent that
  //   * subsumes it or null if no irregular sequent is subsumed.
  //   * 
  //   * @return the subsumption map.
  //   */
  //  public HashMap<FrjIrregularSequent, FrjIrregularSequent> getBwSubsumptionMap();

  /**
   * Add the specified pair to this subsumption map.
   * 
   * @param subsumed the sequent subsumed.
   * @param subsuming the subsuming sequent.
   */
  public void addSubsumption(_FrjSequent subsumed, _FrjSequent subsuming);

  /**
   * Returns the maximal sequent subsuming <code>seq</code> inserted in this
   * table or <code>null</code> if <code>seq</code> has no subsuming sequents in
   * this table.
   * 
   * @param seq the sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public _FrjSequent getMaximalSubsuming(_FrjSequent seq);

  /**
   * Returns the maximal regular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the regular sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public FrjRegularSequent getMaximalRegularSubsuming(FrjRegularSequent seq);

  /**
   * Returns the maximal irregular sequent subsuming <code>seq</code> inserted
   * in this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the irregular sequent.
   * @return the maximal sequent subsuming <code>code</code> or
   * <code>null</code>.
   */
  public FrjIrregularSequent getMaximalIrregularSubsuming(FrjIrregularSequent seq);

  /**
   * Returns the first sequent subsuming <code>seq</code> inserted in this table
   * or <code>null</code> if <code>seq</code> has no subsuming sequents in this
   * table.
   * 
   * @param seq the sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public _FrjSequent getFisrtSubsuming(_FrjSequent seq);

  /**
   * Returns the first regular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the regular sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public FrjRegularSequent getFirstRegularSubsuming(FrjRegularSequent seq);

  /**
   * Returns the first irregular sequent subsuming <code>seq</code> inserted in
   * this table or <code>null</code> if <code>seq</code> has no subsuming
   * sequents in this table.
   * 
   * @param seq the irregular sequent.
   * @return the first sequent subsuming <code>code</code> or <code>null</code>.
   */
  public FrjIrregularSequent getFirstIrregularSubsuming(FrjIrregularSequent seq);

}
