package ipl.frj.rules;

/**
 * Enum representing the rules of Frj caclulus.
 * 
 * @author Mauro Ferrari
 *
 */
public enum FrjRulesID {
  AX_IRR, //
  AX_REG, //
  AND_IRR, //
  AND_REG, //
  OR_IRR, //
  IMP_REG_IN_CL, //
  IMP_REG_NOT_IN_CL, //
  IMP_IRR_IN_CL, //
  JOIN_ATOMIC, //
  JOIN_DISJUNCTION
  ;


  public String getRuleName() {
    return this.name();
  }

}
