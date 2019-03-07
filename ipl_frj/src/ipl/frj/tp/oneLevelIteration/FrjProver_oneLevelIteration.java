package ipl.frj.tp.oneLevelIteration;

import java.util.Collection;

import org.apache.commons.logging.impl.SimpleLog;

import ferram.util.ArrayFormatting;
import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.rules.JoinPremises;
import ipl.frj.rules.Join_ATOMIC;
import ipl.frj.rules.Join_DISJUNCTION;
import ipl.frj.rules.Rule_AND_IRREGULAR;
import ipl.frj.rules.Rule_AX_IRREGULAR;
import ipl.frj.rules.Rule_OR;
import ipl.frj.rules._FrjRule;
import ipl.frj.seqdb.JumpPremisesBuilder;
import ipl.frj.seqdb.JumpPremisesBuilder_BackwardSubsumption;
import ipl.frj.seqdb.DB;
import ipl.frj.seqdb.DB_BackwardSubsumption;
import ipl.frj.seqdb.DB_ForwardSubsumption;
import ipl.frj.rules.Rule_AND_REGULAR;
import ipl.frj.rules.Rule_AX_REGULAR;
import ipl.frj.rules.Rule_IMPLIES_IRREGULAR_IN_CL;
import ipl.frj.rules.Rule_IMPLIES_REGULAR_IN;
import ipl.frj.rules.Rule_IMPLIES_REGULAR_NOT_IN_CL;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._FrjProver;
import ipl.frj.tp.basic._JumpPremisesBuilder;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.util.MSGManager;
import jtabwb.engine.ProofSearchResult;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

/**
 * Forward prover based on the FRJ calculus. This prover applies all possible
 * rules at any iteration.
 * 
 * @author Mauro Ferrari
 *
 */
public class FrjProver_oneLevelIteration implements _FrjProver {

  // configuration data
  private final SimpleLog LOG;
  private final LauncherExecConfiguration CONFIGURATION;
  private final MSGManager VERBOSE_MNGR = new MSGManager("");
  private final boolean VERBOSE;
  private boolean DEBUG = false;

  // proof search data
  private final Formula goal;
  private final CandidateFormulas candidateFormulas;
  private final _ProvedSequentsDB db;
  private final _JumpPremisesBuilder jumpPremisesBuilder;
  private int currentIteration;
  private ProofSearchResult proofSearchResult;
  private int ruleApplications;
  private final int JOIN_AT_MAX_DEGREE;

  public FrjProver_oneLevelIteration(LauncherExecConfiguration configuration, Formula goal) {
    super();
    this.CONFIGURATION = configuration;
    this.LOG = new SimpleLog(this.getClass().getCanonicalName());
    // configure
    this.LOG.setLevel(CONFIGURATION.logMode());
    if (this.LOG.isDebugEnabled())
      this.DEBUG = true;
    this.VERBOSE = CONFIGURATION.verboseMode();
    // build initial structures
    this.goal = goal;
    this.candidateFormulas = new CandidateFormulas(goal);
    this.JOIN_AT_MAX_DEGREE = candidateFormulas.getJoinATMaxDegree();
    switch (configuration.getSelectedSequentDB()) {
    case PLAIN:
      this.db = new DB(candidateFormulas, CONFIGURATION);
      this.jumpPremisesBuilder =
          new JumpPremisesBuilder(candidateFormulas, (DB) this.db, configuration);
      break;
    case SUBSUMPTION_FORWARD:
      this.db = new DB_ForwardSubsumption(candidateFormulas, CONFIGURATION);
      this.jumpPremisesBuilder =
          new JumpPremisesBuilder(candidateFormulas, this.db, configuration);
      break;
    case SUBSUMPTION_BACWARD:
      this.db = new DB_BackwardSubsumption(candidateFormulas, CONFIGURATION);
      this.jumpPremisesBuilder = new JumpPremisesBuilder_BackwardSubsumption(candidateFormulas,
          (DB_BackwardSubsumption) db, configuration);
      break;
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED_arg,
          configuration.getSelectedSequentDB());
    }
    this.currentIteration = 0;
    this.ruleApplications = 0;
    this.proofSearchResult = null;
  }

  /**
   * Returns the prover name.
   * 
   * @return the prover name.
   */
  @Override
  public String getProverName() {
    return "frj-dev-" + (CONFIGURATION.getSelectedSequentDB().getName());
  }

  public void prove() {
    LOG.debug(this);

    // at iteration 0 generate all axiom sequents
    if (this.VERBOSE)
      verbose_preIteration(currentIteration);
    generate_AXIOMS();
    db.commitIteration();

    if (this.VERBOSE)
      verbose_postIteration();

    // next itertion
    boolean proofSearchSucceded = db.checkForSuccess();
    while (db.numberOfProvedSequents(ProvedSequentsTables.LAST_COMMITTED_ITERATION) > 0
        && !proofSearchSucceded) {

      currentIteration++;
      if (this.VERBOSE)
        verbose_preIteration(currentIteration);

      // apply rules
      generate_AND();
      generate_OR();
      generate_IMPLIES();
      generate_JOIN();

      db.commitIteration();

      if (this.VERBOSE)
        verbose_postIteration();

      proofSearchSucceded = db.checkForSuccess();
    }
    if (proofSearchSucceded)
      proofSearchResult = ProofSearchResult.SUCCESS;
    else
      proofSearchResult = ProofSearchResult.FAILURE;

  }

  private void applyRule(_FrjRule rule) {
    ruleApplications++;
    _FrjSequent conclusion = rule.conclusion();
    if (db.add(conclusion))
      if (this.VERBOSE)
        verbose_ruleApplication(rule);
    if (this.DEBUG)
      verbose_ruleApplicationWithPremises(rule);
  }

  // the cases of triples in premises of JOIN rules already treated
  //HashSet<SigmaThetaUpsilon> alreadyTreated = new HashSet<SigmaThetaUpsilon>();

  private void generate_JOIN() {
    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.FRJPROVER.APPLYING_JOIN);

    Collection<FrjIrregularSequent> lastIterationIrregular =
        db.irregular(ProvedSequentsTables.LAST_COMMITTED_ITERATION);
    if (lastIterationIrregular == null)
      return;

    // get the set of all proved irregular sequents
    Collection<FrjIrregularSequent> provedIrregulars =
        db.irregular(ProvedSequentsTables.GLOBAL);

    if (provedIrregulars == null)
      return;

    BitSetOfFormulas atomicRightCandidate = candidateFormulas.getRight(FormulaType.ATOMIC_WFF);
    BitSetOfFormulas orRightCandidate = candidateFormulas.getRight(FormulaType.OR_WFF);

    if (atomicRightCandidate == null && orRightCandidate == null)
      return;

    // update the compatibility table
    // build the set of sets of compatible sequents
    Collection<JoinPremises> compatible = jumpPremisesBuilder.buildJoinPremises();

    if (compatible != null) { // apply all possible instances of jump rules
      for (JoinPremises joinPremises : compatible) {
        if (joinPremises.degree() <= JOIN_AT_MAX_DEGREE) {
          // JUMP_ATOMIC
          for (Formula mainFormula : atomicRightCandidate)
            if (joinPremises.isJoinATOMICApplicable(mainFormula))
              applyRule(new Join_ATOMIC(currentIteration, joinPremises, mainFormula));
        }

        // apply JUMP_DISJUNCTION
        // note that the compatibilty table already build premises with 
        // at most JOIN_OR_MAX_DEGREE premises 
        for (Formula mainFormula : orRightCandidate)
          if (joinPremises.isJoinDISJUNCTIONApplicable(mainFormula))
            applyRule(new Join_DISJUNCTION(currentIteration, joinPremises, mainFormula));

      }
    }
  }

  private void generate_IMPLIES() {
    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.FRJPROVER.APPLYING_IMPLIES);

    BitSetOfFormulas impliesCandidate = candidateFormulas.getRight(FormulaType.IMPLIES_WFF);

    for (Formula AimpliesB : impliesCandidate) {
      Formula A = AimpliesB.immediateSubformulas()[0];
      Formula B = AimpliesB.immediateSubformulas()[1];
      {
        // get regular sequents with right = consequent
        Collection<FrjRegularSequent> premises =
            db.regularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, B);
        if (premises != null) {
          // APPLY IMPLIES_REGULAR_IN_CL
          for (FrjRegularSequent prem : premises) {
            if (candidateFormulas.isInClosure(A, prem.left()))
              applyRule(new Rule_IMPLIES_REGULAR_IN(currentIteration, AimpliesB, prem));
          }

          // APPLY IMPLIES_REGULAR_NOT_IN_CL
          for (FrjRegularSequent prem : premises) {
            BitSetOfFormulas left = prem.left();
            if (left != null)
              if (candidateFormulas.isInClosure(A, left)) {
                Collection<BitSetOfFormulas> maximalNotCovering =
                    candidateFormulas.buildMaximalClosureNotCoveringSets(prem, A);
                if (maximalNotCovering == null)
                  applyRule(
                      new Rule_IMPLIES_REGULAR_NOT_IN_CL(currentIteration, null, AimpliesB, prem));
                else {
                  for (BitSetOfFormulas theta : maximalNotCovering)
                    applyRule(new Rule_IMPLIES_REGULAR_NOT_IN_CL(currentIteration, theta, AimpliesB,
                        prem));
                }
              }
          }
        }
      }

      // APPLY IMPLIES_IRREGULAR_IN_CL
      if (!AimpliesB.equals(goal)) { // do not generate irregular sequents with goal on the right
        Collection<FrjIrregularSequent> premises =
            db.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, B);
        if (premises != null)
          for (FrjIrregularSequent prem : premises) {
            if (prem.nonStable() != null) {
              // get the minimal coverings  of A
              Collection<BitSetOfFormulas> coverings =
                  candidateFormulas.buildMinimalClosureCoveringSets(prem, A);

              if (coverings != null)
                for (BitSetOfFormulas mincov : coverings) {
                  //                      + "\nAntecedet = " + A + "\nMin.Cov.= " + coverings);
                  BitSetOfFormulas notInResources = null;
                  if (prem.stable() != null)
                    notInResources = mincov.difference(prem.stable());
                  else
                    notInResources = mincov;
                  applyRule(new Rule_IMPLIES_IRREGULAR_IN_CL(currentIteration, notInResources,
                      AimpliesB, prem));
                }
            }
          }
      }
    }
  }

  private void generate_OR() {
    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.FRJPROVER.APPLYING_OR);

    BitSetOfFormulas orCandidate = candidateFormulas.getRight(FormulaType.OR_WFF);
    for (Formula orWff : orCandidate)
      if (!orWff.equals(goal)) { // do not generate irregular sequents with goal on the right
        Formula A0 = orWff.immediateSubformulas()[0];
        Formula A1 = orWff.immediateSubformulas()[1];

        // treat A0
        // premises proved in the last iteration with right=A0
        Collection<FrjIrregularSequent> last_iteration_premises_A0 =
            db.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, A0);
        if (last_iteration_premises_A0 != null) {
          // apply rules for last_iteration_premises_A0 if there are proofs of A1 in global table
          Collection<FrjIrregularSequent> global_premises_A1 =
              db.irregularWithRight(ProvedSequentsTables.GLOBAL, A1);
          if (global_premises_A1 != null)
            for (FrjIrregularSequent preA0 : last_iteration_premises_A0)
              for (FrjIrregularSequent preA1 : global_premises_A1) {
                BitSetOfFormulas sigma0 = preA0.stable();
                BitSetOfFormulas sigma1 = preA1.stable();
                BitSetOfFormulas sigma0_cup_theta0 = preA0.left();
                BitSetOfFormulas sigma1_cup_theta1 = preA1.left();
                if ((sigma0 == null
                    || (sigma1_cup_theta1 != null && sigma0.subseteq(sigma1_cup_theta1)))
                    && (sigma1 == null
                        || (sigma0_cup_theta0 != null && sigma1.subseteq(sigma0_cup_theta0))))
                  applyRule(new Rule_OR(currentIteration, orWff, preA0, preA1));
              }
        }

        // treat A1
        // premises proved in the last iteration with right=A0
        Collection<FrjIrregularSequent> last_iteration_premises_A1 =
            db.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, A1);
        if (last_iteration_premises_A1 != null) {
          // apply rules for last_iteration_premises_A1 if there are proofs of A0 in global table
          Collection<FrjIrregularSequent> global_premises_A0 =
              db.irregularWithRight(ProvedSequentsTables.GLOBAL, A0);
          if (global_premises_A0 != null)
            for (FrjIrregularSequent preA0 : global_premises_A0)
              for (FrjIrregularSequent preA1 : last_iteration_premises_A1) {
                BitSetOfFormulas sigma0 = preA0.stable();
                BitSetOfFormulas sigma1 = preA1.stable();
                BitSetOfFormulas sigma0_cup_theta0 = preA0.left();
                BitSetOfFormulas sigma1_cup_theta1 = preA1.left();
                if ((sigma0 == null
                    || (sigma1_cup_theta1 != null && sigma0.subseteq(sigma1_cup_theta1)))
                    && (sigma1 == null
                        || (sigma0_cup_theta0 != null && sigma1.subseteq(sigma0_cup_theta0))))
                  applyRule(new Rule_OR(currentIteration, orWff, preA0, preA1));
              }
        }
      }
  }

  private void generate_AND() {
    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.FRJPROVER.APPLYING_AND);

    BitSetOfFormulas andCandidate = candidateFormulas.getRight(FormulaType.AND_WFF);
    for (Formula wff : andCandidate) { // candidate is A_0 AND A_1
      for (int treatedConjunct = 0; treatedConjunct < 2; treatedConjunct++) { // for every conjunct (0,1) 
        // Apply Regular_AND to regular sequents with right=A_i
        Collection<FrjRegularSequent> regularPremises =
            db.regularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION,
                wff.immediateSubformulas()[treatedConjunct]);
        if (regularPremises != null)
          for (FrjRegularSequent premise : regularPremises)
            applyRule(new Rule_AND_REGULAR(currentIteration, wff, premise));

        // Apply Irregulard_AND to irregular sequents with right=A_i
        if (!andCandidate.equals(goal)) { // do not generate irregular sequents with goal on the right
          Collection<FrjIrregularSequent> irregularPremises =
              db.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION,
                  wff.immediateSubformulas()[treatedConjunct]);
          if (irregularPremises != null)
            for (FrjIrregularSequent premise : irregularPremises)
              applyRule(new Rule_AND_IRREGULAR(currentIteration, wff, treatedConjunct, premise));
        }
      }
    }
  }

  private void generate_AXIOMS() {
    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.FRJPROVER.APPLYING_AXIOMS);

    // get the atoms that can occurr in the right hand side
    BitSetOfFormulas atomsCandidate = candidateFormulas.getRight(FormulaType.ATOMIC_WFF);
    // AX_REGULAR
    for (Formula atom : atomsCandidate)
      applyRule(new Rule_AX_REGULAR(currentIteration, candidateFormulas.getLeftAtomic(), atom));

    // AX_IRREGULAR
    for (Formula atom : atomsCandidate)
      applyRule(new Rule_AX_IRREGULAR(currentIteration, candidateFormulas.getLeftFormulas(), atom));

  }

  public FrjProofSearchDetails getProofSearchDetails() {
    FrjProofSearchDetails details = new FrjProofSearchDetails();
    details.setIterations(currentIteration);
    details.setProofSearchResult(proofSearchResult);
    details.setGeneratedSequents(db);
    details.setNumberOfAppliedRules(ruleApplications);
    details.setGoal(goal);
    return details;
  }

  @Override
  public String toString() {
    return //
    "-- PROVER STATE\n" + // 
        "-- goal=" + this.goal + "\n" + //
        candidateFormulas.toString();
  }

  private void verbose_preIteration(int currentIteration) {
    MSGManager.info(MSG.VERBOSE.FRJPROVER.ITERATION_BEGIN, currentIteration);
  }

  private void verbose_postIteration() {
    VERBOSE_MNGR.print(MSG.VERBOSE.FRJPROVER.ITERATION_END,
        db.numberOfProvedSequents(ProvedSequentsTables.LAST_COMMITTED_ITERATION),
        db.numberOfProvedSequents(ProvedSequentsTables.GLOBAL));
  }

  private void verbose_ruleApplication(_FrjRule rule) {
    MSGManager.info(MSG.VERBOSE.FRJPROVER.RULE_APPLICATION_RESULT, rule.name(),
        rule.conclusion().getSequentProgessiveNumber(), rule.conclusion().format());
  }

  private void verbose_ruleApplicationWithPremises(_FrjRule rule) {
    MSGManager.info(MSG.VERBOSE.FRJPROVER.RULE_APPLICATION_DETAILS_WITH_PREMISES, rule.name(),
        format_premises(rule.premises()), rule.conclusion().getSequentProgessiveNumber(),
        rule.conclusion().format());
  }

  private String format_premises(_FrjSequent[] premises) {
    return ArrayFormatting.toString(premises, ", ");
  }

}
