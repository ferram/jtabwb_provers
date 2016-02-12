package cpl.g3c.tp;

import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwb.engine.ProverName;
import jtabwb.engine._Prover;
import jtabwb.engine._Strategy;
import jtabwb.tracesupport._LatexCTreeFormatter;
import jtabwb.tracesupport._LatexSupport;
import jtabwbx.prop.formula.FormulaFactory;

/**
 * Prover for G3c.
 * 
 * @author Mauro Ferrari
 *
 */
public class Prover implements _Prover, _LatexSupport {

  public final String NAME = "jpcltp";
  public final String VERSION = "1.0";
  public final String DESCRITPION = "Prover for Classical Propositional Logic based on G3";

  FormulaFactory factory = new FormulaFactory();
  ProverName proverName;

  public Prover() {
    super();
    this.proverName = new ProverName(NAME);
    this.proverName.setDescription(DESCRITPION);
    this.proverName.setVersion(VERSION);
  }

  public void configure(FormulaFactory formulaFactory) {
    this.factory = formulaFactory;
  }

  @Override
  public ProverName getProverName() {
    return proverName;
  }

  @Override
  public _Strategy getStrategy() {
    return new Strategy();
  }

  @Override
  public ProvabilityStatus statusFor(ProofSearchResult result) {
    if (result == ProofSearchResult.SUCCESS)
      return ProvabilityStatus.PROVABLE;
    else
      return ProvabilityStatus.UNPROVABLE;
  }

  @Override
  public _LatexCTreeFormatter getLatexProofFormatter() {
    return new LatexCtreeFormatter();
  }

}
