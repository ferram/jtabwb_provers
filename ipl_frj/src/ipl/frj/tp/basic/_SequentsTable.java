package ipl.frj.tp.basic;

import java.util.Collection;

import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.Formula;

public interface _SequentsTable {

  /**
   * Inserts the sequent <code>seq</code> in this table. This method does not
   * check if <code>seq</code> is already in the table or it is subsumed by some
   * method in the table; thus to avoid redundancies you should check these
   * facts with the methods {@link #contains(_FrjSequent)} and
   * {@link #subsumes(_FrjSequent)}.
   * 
   * @param seq the sequent to insert.
   */
  public void insert(_FrjSequent seq);

  /**
   * Returns true iff this table contains the specified sequent.
   * 
   * @param sequent the sequent to search.
   * @return true iff this table contains the specified sequent.
   */
  boolean contains(_FrjSequent sequent);

  /**
   * Returns the irregular sequents stored in this table.
   * 
   * @return the irregular sequents stored in this table.
   */
  Collection<FrjIrregularSequent> irregular();

  /**
   * Returns the regular sequents stored in this table.
   * 
   * @return the regular sequents stored in this table.
   */
  Collection<FrjRegularSequent> regular();

  /**
   * Returns the irregular sequents with the specified right formula stored in
   * this table.
   * 
   * @return the irregular sequents with the specified right formula stored in
   * this table.
   */
  Collection<FrjIrregularSequent> irregularWithRight(Formula wff);

  /**
   * Returns the regular sequents with the specified right formula stored in
   * this table.
   * 
   * @return the regular sequents with the specified right formula stored in
   * this table.
   */
  Collection<FrjRegularSequent> regularWithRight(Formula wff);

  /**
   * Returns the number of sequents (regular and irregular) stored in this
   * table.
   * 
   * @return the number of sequents stored in this table.
   */
  int numberOfProvedSequents();

  /**
   * Returns the collection of the irregular sequents in this table subsumed by
   * the specified one or <code>null</code> if no irregular sequent in the
   * global table is subsumed by <code>seq</code>.
   * 
   * @return the collection of the irregular sequents subsumed by
   * <code>seq</code> or <code>null</code>.
   */
  public Collection<FrjIrregularSequent> subsumedIrregular(FrjIrregularSequent seq);

  /**
   * Returns the collection of the regular sequents in this table subsumed by
   * the specified one or <code>null</code> if no regular sequent in this table
   * is subsumed by <code>seq</code>.
   * 
   * @return the collection of the regular sequents subsumed by
   * <code>seq</code> or <code>null</code>.
   */
  public Collection<FrjRegularSequent> subsumedRegular(FrjRegularSequent seq);

  public boolean subsumes(_FrjSequent sequent);

  /**
   * Removes the specified sequent from this table and returns <code>true</code>
   * if the sequent was contained in this table.
   * 
   * @param seq the sequent to be removed.
   * @return <code>true</code> iff the sequent was contained in this table.
   */
  public boolean remove(_FrjSequent seq);

  /**
   * Removes from this table all its sequents that are contained in the
   * specified collection.
   * 
   * @param sequents the collection containing the sequents to be removed from
   * this tale.
   * @return the number of sequents removed from this table.
   */
  public int removeAll(Collection<_FrjSequent> sequents);

}