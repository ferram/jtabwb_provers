package ipl.frj.rules;

import ipl.frj.sequent._FrjSequent;
import jtabwbx.prop.formula.Formula;

public interface _FrjRule {

  public _FrjSequent conclusion();

  public _FrjSequent[] premises();

  public Formula mainFormula();

  public int numberOfPremises();

  public FrjRulesID getID();

  public String name();

}
