package ipl.frj.gbu.tp;

import java.util.Collection;

import ipl.frj.gbu.sequent.GbuIrregularSequent;
import ipl.frj.gbu.sequent.GbuRegularSequent;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._ProvedSequentsDB;
import jtabwbx.prop.formula.BitSetOfFormulas;

public class SaturatedDBEvaluator {

  public SaturatedDBEvaluator(_ProvedSequentsDB db) {
    super();
    this.db = db;
    this.candidateFormulas = db.candidateFormulas();
  }

  private final _ProvedSequentsDB db;
  private final CandidateFormulas candidateFormulas;

  public boolean isEvaluated(GbuRegularSequent regular) {
    Collection<FrjRegularSequent> provedRegular =
        db.regularWithRight(ProvedSequentsTables.GLOBAL, regular.getRight());
    if (provedRegular == null)
      return false;
    else
      for (FrjRegularSequent seq : provedRegular)
        if (candidateFormulas.isInClosure(regular.leftSide(), seq.left()))
          return true;

    return false;
  }

  public boolean isEvaluated(GbuIrregularSequent irregular) {
    Collection<FrjIrregularSequent> provedIrregular =
        db.irregularWithRight(ProvedSequentsTables.GLOBAL, irregular.getRight());
    if (provedIrregular == null)
      return false;
    else
      for (FrjIrregularSequent seq : provedIrregular) {
        BitSetOfFormulas leftSideToCheck = irregular.leftSide();
        if (supersetEQ(leftSideToCheck,seq.stable()) && subsetEQ(leftSideToCheck, seq.left()))
          return true;
      }

    return false;

  }

  private boolean supersetEQ(BitSetOfFormulas one, BitSetOfFormulas two) {
    if (two == null)
      return true;
    if (one == null)
      return false;

    return one.superseteq(two);
  }

  private boolean subsetEQ(BitSetOfFormulas one, BitSetOfFormulas two) {
    if (one == null)
      return true;
    if (two == null)
      return false;

    return one.subseteq(two);
  }

}
