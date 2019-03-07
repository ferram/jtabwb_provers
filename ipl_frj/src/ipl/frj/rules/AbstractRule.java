package ipl.frj.rules;

import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.Formula;

public abstract class AbstractRule implements _FrjRule {

  public AbstractRule(int iteration, FrjRulesID ruleID, Formula mainFormula,
      _FrjSequent... premises) {
    super();
    this.iteration = iteration;
    this.premises = premises;
    this.ruleID = ruleID;
    this.mainFormula = mainFormula;
  }

  final int iteration;
  final Formula mainFormula;
  final _FrjSequent[] premises;
  final FrjRulesID ruleID;

  @Override
  public FrjRulesID getID() {
    return ruleID;
  }

  @Override
  public _FrjSequent[] premises() {
    return premises;
  }

  @Override
  public Formula mainFormula() {
    return mainFormula;
  }

  @Override
  public int numberOfPremises() {
    return premises.length;
  }

  @Override
  public String name() {
    return ruleID.getRuleName();
  }

}
