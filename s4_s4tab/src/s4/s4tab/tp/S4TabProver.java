package s4.s4tab.tp;

import jtabwb.engine.ProofSearchResult;
import jtabwb.engine.ProvabilityStatus;
import jtabwb.engine.ProverName;
import jtabwb.engine._Prover;
import jtabwb.engine._Strategy;
import jtabwb.util.CaseNotImplementedImplementationError;

/**
 * The S4Tab prover.
 * 
 * @author Mauro Ferrari
 *
 */
public class S4TabProver implements _Prover {

  private ProverName PROVER_NAME;
  private Strategy strategy;

  public S4TabProver() {
    this.PROVER_NAME = new ProverName("s4tab", "1.0");
    this.strategy = new Strategy();

  }

  @Override
  public ProverName getProverName() {
    return PROVER_NAME;
  }

  @Override
  public _Strategy getStrategy() {
    return strategy;
  }

  @Override
  public ProvabilityStatus statusFor(ProofSearchResult result) {
    switch (result) {
    case SUCCESS:
      return ProvabilityStatus.PROVABLE;

    case FAILURE:
      return ProvabilityStatus.UNPROVABLE;
    default:
      throw new CaseNotImplementedImplementationError(result.name());
    }
  }

}
