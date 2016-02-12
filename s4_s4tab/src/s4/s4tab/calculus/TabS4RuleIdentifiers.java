package s4.s4tab.calculus;

/**
 * The identifiers for the rules of the S4Tab calculus, this enumerative
 * provides a constant for every logical rule of the calculus.
 * 
 * @author Mauro Ferrari
 */
public enum TabS4RuleIdentifiers {
  CLASH_DETECTION,
  /**
   * REGULAR RULE.
   * 
   * <pre>
   *      S, T(A \land B)
   *      ------------------ 
   *      S, TA, TB
   * </pre>
   */
  T_AND, //
  /**
   * REGULAR RULE.
   * 
   * <pre>
   *      S, T\neg A 
   *      -----------
   *      S, FA A
   * </pre>
   */
  T_NOT, //
  /**
   * REGULAR RULE.
   * 
   * <pre>
   *      S, T(\nec A)
   *      -------------------
   *      S, Tc A
   * </pre>
   */
  T_NEC,
  /**
   * REGULAR BRANCHING RULE.
   * 
   * <pre>
   *      S,F(A \land B)
   *      --------------
   *      S, FA | S, FB
   * </pre>
   */
  F_AND, //
  /**
   * REGULAR RULE.
   * 
   * <pre>
   *      S, F\neg A 
   *      -----------
   *      S, TA
   * </pre>
   */
  F_NOT, //
  /**
   * NON_INVERTIBLE RULE.
   * 
   * <pre>
   *      S, F(\nec A)
   *      -------------------
   *      Sc, F A
   * </pre>
   */
  F_NEC,
  /**
   * REGULAR RULE.
   * 
   * <pre>
   *      S, Tc(A \land B)
   *      -------------------
   *      S, Tc A, Tc B
   * </pre>
   */
  Tc_AND,
  /**
   * REGULAR RULE. DUPLICATION
   * 
   * <pre>
   *        S, Tc(\neg A)
   *      --------------------
   *      S, F A , Tc(\neg A)
   * </pre>
   */
  Tc_NOT,
  /**
   * REGULAR RULE
   * 
   * <pre>
   *        S, Tc(\nec A)
   *      --------------------
   *      S, Tc A
   * </pre>
   */
  Tc_NEC, ;

  public static TabS4RuleIdentifiers getByName(String name) {
    for (TabS4RuleIdentifiers id : TabS4RuleIdentifiers.values())
      if (id.name().equals(name))
        return id;
    return null;
  }

}
