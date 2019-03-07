package ipl.frj.tp.basic;

import java.util.Collection;

import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.Formula;

/**
 * Interface specifying the DB of proved sequents.
 * 
 * @author Mauro Ferrari
 *
 */
public interface _ProvedSequentsDB {

  /**
   * Commits the sequent proved in the last iteration to the global table.
   * 
   */
  void commitIteration();

  /**
   * Add the specified sequent to those proved in the current iteration provided
   * that the specified sequent does not already belong to the iteration table
   * or to the global table.
   * 
   * @param provedSequent the sequent to add.
   * @return true iff the sequent has been effectoively added to the current
   * iteration table.
   */
  boolean add(_FrjSequent provedSequent);

  /**
   * Proof-search succeed if a regular sequent having goal in the right hand
   * side has been generated.
   * 
   * @return <code>true</code> if proof-search succeed.
   */
  boolean checkForSuccess();

  /**
   * Returns the regular sequents in the global table proving the goal.
   * 
   * @return the collection of the sequents proving the goal contained in the
   * global table.
   */
  Collection<FrjRegularSequent> getSuccessSequents();

  /**
   * Return all the irregular sequents added to the specified table.
   * 
   * @return the irregular sequents contained in the specified table.
   */
  Collection<FrjIrregularSequent> irregular(ProvedSequentsTables table);

  /**
   * Return the regular sequents added to the specified table.
   * 
   * @return the regular sequents added to specified table.
   */
  Collection<FrjRegularSequent> regular(ProvedSequentsTables table);

  /**
   * Returns all the irregular sequents with the specified right formula
   * contained in the specified table.
   * 
   * @return the irregular sequents with the specified right formula contained
   * in the specified table.
   */
  Collection<FrjIrregularSequent> irregularWithRight(ProvedSequentsTables table, Formula wff);

  /**
   * Returns all the regular sequents with the specified right formula contained
   * in the specified table.
   * 
   * @return the regular sequents with the specified right formula contained in
   * the specified table.
   */
  Collection<FrjRegularSequent> regularWithRight(ProvedSequentsTables table, Formula wff);

  /**
   * Get the table storing the sequents proved at the specified iteration.
   * 
   * @param iteration
   * @return the table storing the sequents proved at the specified iteration.
   */
  _SequentsTable getIterationTable(int iteration);

  /**
   * The total number of proved sequents in the specified table.
   * 
   * @param table
   * @return the total number of proved sequents in the specified table.
   */
  int numberOfProvedSequents(ProvedSequentsTables table);

  /**
   * The current iteration counter.
   * 
   * @return the current iteration counter.
   */
  int currentIterarion();

  /**
   * The last committed iteration counter.
   * 
   * @return the last commit iteration counter.
   */
  int lastCommittedIteration();

  /**
   * Set the sequent idx of specified sequent.
   * 
   * @param seq the sequent.
   * @return the sequent idx.
   */
  int setSequentIdx(_FrjSequent seq);

  /**
   * Returns the candidate formulas used by this DB.
   * @return the candidate formulas used by this DB.
   */
  CandidateFormulas candidateFormulas();

}