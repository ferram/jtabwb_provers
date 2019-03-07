package ipl.frj.seqdb;

import java.util.LinkedList;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import jtabwb.util.ImplementationError;

public class SequentsTableRemovingSubsumed extends SequentsTable {


  public SequentsTableRemovingSubsumed(LauncherExecConfiguration configuration) {
    super(configuration);
  }

  /**
   * This method inserts the sequent <code>seq</code> in this table and removes
   * from this tables all the sequents subsumed by <code>seq</code>. This method
   * does not check if <code>seq</code> is already in the table or it is
   * subsumed by some method in the table; thus to avoid redundancies you should
   * check these facts with the methods {@link #contains(_FrjSequent)} and
   * {@link #subsumes(_FrjSequent)}.
   * 
   * @param seq the sequent to insert.
   */
  @Override
  public void insert(_FrjSequent seq) {
    removeSubsumed(seq);
    super.insert(seq);
  }

  public int removeSubsumed(_FrjSequent provedSequent) {
    switch (provedSequent.type()) {
    case IRREGULAR: {
      LinkedList<_FrjSequent> toRemove = new LinkedList<_FrjSequent>();
      for (FrjIrregularSequent seq : allIrregular)
        if (provedSequent.subsumes(seq))
          toRemove.add(seq);

      return this.removeAll(toRemove);
    }
    case REGULAR: {
      LinkedList<_FrjSequent> toRemove = new LinkedList<_FrjSequent>();
      for (FrjRegularSequent seq : allRegular)
        if (provedSequent.subsumes(seq))
          toRemove.add(seq);

      return this.removeAll(toRemove);
    }
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          "Sequent type: " + provedSequent.type());
    }

  }


}
