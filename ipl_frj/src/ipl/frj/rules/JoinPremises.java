package ipl.frj.rules;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.sequent.FrjIrregularSequent;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

/**
 * @author Mauro Ferrari
 *
 */
public class JoinPremises {

  public JoinPremises(FrjIrregularSequent[] premises) {
    super();
    this.premises = premises;
    BitSetOfFormulas rightSides = new BitSetOfFormulas(FrjFormulaFactory.getInstance());
    BitSetOfFormulas sigma = new BitSetOfFormulas(FrjFormulaFactory.getInstance());
    BitSetOfFormulas theta = FrjFormulaFactory.getInstance().getGeneratedFormulas().clone();
    for (int i = 0; i < premises.length; i++) {
      FrjIrregularSequent seq = (FrjIrregularSequent) premises[i];
      rightSides.add(seq.right());
      if (seq.stable() != null)
        sigma.or(seq.stable());
      if (theta != null && seq.nonStable() != null) {
        theta.and(seq.nonStable());
        if (theta.cardinality() == 0)
          theta = null;
      } else
        theta = null;
    }
    // restrict theta_implicative to right_sides
    if (theta != null)
      this.implicativeRestriction(theta, rightSides);

    this.sets = new SigmaThetaUpsilon(sigma, theta, rightSides);
  }

  public JoinPremises(FrjIrregularSequent[] premises, BitSet premisesIndexs) {
    this(premises);
    this.setOfidxsOfPremises = premisesIndexs;
  }

  private FrjIrregularSequent[] premises;
  private SigmaThetaUpsilon sets;
  private BitSet setOfidxsOfPremises; // the set of idx of premises

  /**
   * Remove from <code>set</code> the formulas of the kind A->B such that A is
   * not in <code>restrictingFormulas</code>.
   * 
   * @param set
   * @param restrictingFormulas
   */
  private void implicativeRestriction(BitSetOfFormulas set, BitSetOfFormulas restrictingFormulas) {
    Collection<Formula> setImplications = set.getAllFormulas(FormulaType.IMPLIES_WFF);
    if (setImplications != null)
      for (Formula wff : setImplications)
        if (!restrictingFormulas.contains(wff.immediateSubformulas()[0]))
          set.remove(wff);
  }

  /**
   * Returns the degree of this premises, i.e. the number of irregular sequents
   * in the premise.
   * 
   * @return the degree of this premises.
   */
  public int degree() {
    return this.premises.length;
  }

  /**
   * @return the premises
   */
  public FrjIrregularSequent[] getPremises() {
    return this.premises;
  }

  /**
   * @return the set of the idxs of the premises.
   */
  public BitSet getSetOfIdxsOfPremises(){
    return setOfidxsOfPremises;
  }
  
  /**
   * @return the sets
   */
  public SigmaThetaUpsilon getSigmaThetaUpsilon() {
    return this.sets;
  }

  public BitSetOfFormulas getSigma() {
    return sets.sigma;
  }

  public BitSetOfFormulas getTheta() {
    return sets.theta;
  }

  public BitSetOfFormulas getUspilon() {
    return sets.rightFormulas;
  }

  /**
   * The method returns true iff mainFormula does not belong to Sigma and the
   * degree of this premise is greater than 1 or the set Sigma \cup Theta
   * contains an implication of the kind rightSide -> C.
   * 
   * 
   * @param mainFormula the mainFormula candidate for rule application.
   * @return true iff the Join_ATOMIC rule is applicable to this premises for
   * the specified main formula.
   */
  public boolean isJoinATOMICApplicable(Formula mainFormula) {
    if (sets.sigma.contains(mainFormula))
      return false;

    if (this.degree() > 1)
      return true;

    FrjIrregularSequent seq = premises[0];
    Formula right = seq.right();
    BitSetOfFormulas[] sets = new BitSetOfFormulas[] { seq.stable(), seq.nonStable() };
    for (BitSetOfFormulas set : sets) {
      if (set != null) {
        Collection<Formula> implications = set.getAllFormulas(FormulaType.IMPLIES_WFF);
        if (implications != null)
          for (Formula implication : implications) {
            if (implication.immediateSubformulas()[0].equals(right))
              return true;
          }
      }
    }
    return false;
  }

  /**
   * The method returns true if the disjunct in mainFormula belong to the set of
   * rightformula of the premises.
   * 
   * 
   * @param mainFormula the mainFormula candidate for rule application.
   * @return true iff the Join_DISJUNCTION rule is applicable to this premises
   * for the specified main formula.
   */
  public boolean isJoinDISJUNCTIONApplicable(Formula mainFormula) {
    return sets.rightFormulas.contains(mainFormula.immediateSubformulas()[0])
        && sets.rightFormulas.contains(mainFormula.immediateSubformulas()[1]);
  }

  @Override
  public String toString() {
    return "JoinPremises -- degree " + this.degree() + " -- [premises="
        + Arrays.toString(this.premises) + ", sets=" + this.sets + "]";
  }

}
