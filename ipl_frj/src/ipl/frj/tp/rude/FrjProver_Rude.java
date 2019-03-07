package ipl.frj.tp.rude;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.impl.SimpleLog;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.rules.Join_ATOMIC;
import ipl.frj.rules.Join_DISJUNCTION;
import ipl.frj.rules.Rule_AND_IRREGULAR;
import ipl.frj.rules.Rule_AX_IRREGULAR;
import ipl.frj.rules.Rule_OR;
import ipl.frj.rules._FrjRule;
import ipl.frj.seqdb.DB;
import ipl.frj.rules.Rule_AND_REGULAR;
import ipl.frj.rules.Rule_AX_REGULAR;
import ipl.frj.rules.Rule_IMPLIES_IRREGULAR_IN_CL;
import ipl.frj.rules.Rule_IMPLIES_REGULAR_IN;
import ipl.frj.rules.Rule_IMPLIES_REGULAR_NOT_IN_CL;
import ipl.frj.sequent.FrjFormulaFactory;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.FrjProofSearchDetails;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._FrjProver;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.util.MSGManager;
import jtabwb.engine.ProofSearchResult;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class FrjProver_Rude implements _FrjProver {

  // configuration data
  private final SimpleLog LOG;
  private final LauncherExecConfiguration CONFIGURATION;
  private final MSGManager VERBOSE_MNGR = new MSGManager("");
  private final boolean VERBOSE;
  static boolean DEBUG = true;

  // proof search data
  private final Formula goal;
  private final CandidateFormulas candidateFormulas;
  private final _ProvedSequentsDB globalTable;
  private int currentIteration;
  private ProofSearchResult proofSearchResult;
  private int ruleApplications;

  public FrjProver_Rude(LauncherExecConfiguration configuration, Formula goal) {
    super();
    this.CONFIGURATION = configuration;
    this.LOG = new SimpleLog(this.getClass().getCanonicalName());
    // configure
    this.LOG.setLevel(CONFIGURATION.logMode());
    this.VERBOSE = CONFIGURATION.verboseMode();
    // build initial structures
    this.goal = goal;
    this.candidateFormulas = new CandidateFormulas(goal);
    this.globalTable = new DB(candidateFormulas,configuration);
    this.currentIteration = 0;
    this.ruleApplications = 0;
    this.proofSearchResult = null;
  }
  
  
  /**
   * Returns the prover name. 
   * @return the prover name.
   */
  @Override
  public String getProverName(){
    return "frj-rude"; 
  }

  public void prove() {
    LOG.debug(this);

    // at iteration 0 generate all axiom sequents
    if (this.VERBOSE)
      verbose_preIteration(currentIteration);
    generate_AXIOMS();
    globalTable.commitIteration();

    if (this.VERBOSE)
      verbose_postIteration();

    // next itertion
    boolean proofSearchSucceded = globalTable.checkForSuccess();
    while (globalTable.numberOfProvedSequents(ProvedSequentsTables.LAST_COMMITTED_ITERATION) > 0 && !proofSearchSucceded) {

      currentIteration++;
      if (this.VERBOSE)
        verbose_preIteration(currentIteration);

      // apply rules
      generate_AXIOMS();
      generate_AND();
      generate_OR();
      generate_IMPLIES();
      generate_JUMP();
      // commit iteration 
      globalTable.commitIteration();

      if (this.VERBOSE)
        verbose_postIteration();

      proofSearchSucceded = globalTable.checkForSuccess();
    }
    if (proofSearchSucceded)
      proofSearchResult = ProofSearchResult.SUCCESS;
    else
      proofSearchResult = ProofSearchResult.FAILURE;

  }

  private void applyRule(_FrjRule rule) {
    ruleApplications++;
    _FrjSequent conclusion = rule.conclusion();
    if (globalTable.add(conclusion))
      if (this.VERBOSE)
        verbose_ruleApplication(rule, conclusion);
  }

  private void generate_JUMP() {
    Collection<FrjIrregularSequent> lastIterationIrregular =
        globalTable.irregular(ProvedSequentsTables.LAST_COMMITTED_ITERATION);
    if (lastIterationIrregular == null)
      return;

    // get the set of all proved irregular sequents
    Collection<FrjIrregularSequent> provedIrregulars = globalTable.irregular(ProvedSequentsTables.GLOBAL);
    //provedSequents.getIrregularSequentsGeneratedUpToIteration(currentIteration - 1);

    if (provedIrregulars == null)
      return;

    // build the set of premises satisfying compatibility and condition on implicative resources
    Collection<FrjIrregularSequent[]> compatible = bruteForcePremisesGeneration(provedIrregulars);

    if (compatible != null) { // apply all possible instances of jump rules
      for (FrjIrregularSequent[] premises : compatible) {
        BitSetOfFormulas[] sigmaThetaRightSides = buildSigmaThetaRightSide(premises);
        BitSetOfFormulas sigma = sigmaThetaRightSides[0];
        BitSetOfFormulas theta = sigmaThetaRightSides[1];
        BitSetOfFormulas rightSides = sigmaThetaRightSides[2];
        // JUMP_ATOMIC
        for (Formula mainFormula : candidateFormulas.getRight(FormulaType.ATOMIC_WFF))
          if (!sigma.contains(mainFormula))
            applyRule(new Join_ATOMIC(currentIteration, sigma, theta, mainFormula, premises));

        // apply JUMP_DISJUNCTION
        for (Formula mainFormula : candidateFormulas.getRight(FormulaType.OR_WFF)) {
          if (rightSides.contains(mainFormula.immediateSubformulas()[0])
              && rightSides.contains(mainFormula.immediateSubformulas()[1]))
            applyRule(new Join_DISJUNCTION(currentIteration, sigma, theta, mainFormula, premises));
        }
      }
    }
  }

  /**
   * Returns the possible premises of a jump rule for the given set of candidate
   * premises.
   * 
   * @param candidates
   * @return
   */
  private Collection<FrjIrregularSequent[]> bruteForcePremisesGeneration(
      Collection<FrjIrregularSequent> possiblePremises) {
    // generates all subsets of irregulars
    FrjIrregularSequent[] candidates =
        possiblePremises.toArray(new FrjIrregularSequent[possiblePremises.size()]);

    // build the compatibility table
    Boolean[][] compatibilityTable = buildCompatibilityTable(candidates);
    // build the set of sets of compatible sequents
    LinkedList<FrjIrregularSequent[]> result = new LinkedList<FrjIrregularSequent[]>();
    for (int i = 0; i < candidates.length; i++) {
      // Build the set of indexes of sequents compatibile with i
      HashSet<Integer> set_idxCompatible = new HashSet<Integer>();
      set_idxCompatible.add(i);
      for (int j = 0; j < candidates.length; j++)
        if (i != j && compatibilityTable[i][j])
          set_idxCompatible.add(j);

      // generate all subsets of set_idxCompatible
      Set<Set<Integer>> powerset = this.powerSet(set_idxCompatible);
      // check if all indexes in the set are compatible
      for (Set<Integer> set : powerset)
        if (set.size() > 0)
          if (isValidSet(compatibilityTable, set) && // all indexes are compatible
              checkConditionOnImplications(candidates, set) // implications satisfy condition
          )
            result.add(buildFormIndexs(candidates, set));

    }
    return result.size() == 0 ? null : purge(result);
  }

  private Collection<FrjIrregularSequent[]> purge(Collection<FrjIrregularSequent[]> compatible) {

    // compute max size
    int maxSize = -1;
    for (FrjIrregularSequent[] set : compatible) {
      if (maxSize == -1 || maxSize < set.length)
        maxSize = set.length;
    }

    // order by size: setsBySize[i] the linked list array of compatible premises o lenght i 
    LinkedList<FrjIrregularSequent[]>[] setsBySize = new LinkedList[maxSize];
    for (FrjIrregularSequent[] set : compatible) {
      if (setsBySize[set.length - 1] == null)
        setsBySize[set.length - 1] = new LinkedList<FrjIrregularSequent[]>();
      setsBySize[set.length - 1].add(set);
    }

    // build result
    LinkedList<FrjIrregularSequent[]> result = new LinkedList<FrjIrregularSequent[]>();
    for (int i = 0; i < maxSize; i++)
      if (setsBySize[i] != null)
        result.addAll(setsBySize[i]);

    return result;

  }

  private boolean checkConditionOnImplications(FrjIrregularSequent[] candidates,
      Set<Integer> setOfIdx) {
    // build the set of right sides
    HashSet<Formula> setOfRightSides = new HashSet<Formula>();
    for (int i : setOfIdx)
      setOfRightSides.add(candidates[i].right());

    // check condition on implications
    for (int i : setOfIdx) {// for every idx in the set
      //check that every implication in resource has antecedent in setOfRightSizes
      BitSetOfFormulas res_implies = candidates[i].stable(FormulaType.IMPLIES_WFF);
      if (res_implies != null)
        for (Formula imp : res_implies)
          if (!setOfRightSides.contains(imp.immediateSubformulas()[0]))
            return false;
    }
    return true;
  }

  private boolean isValidSet(Boolean[][] compatibilityTable, Set<Integer> set) {
    for (int i : set)
      for (int j : set)
        if (i != j && !compatibilityTable[i][j]) // if a pair of seq are no compatible return false
          return false;
    return true;
  }

  private FrjIrregularSequent[] buildFormIndexs(FrjIrregularSequent[] candidates, Set<Integer> idxs) {
    FrjIrregularSequent[] result = new FrjIrregularSequent[idxs.size()];
    int k = 0;
    for (int i : idxs)
      result[k++] = candidates[i];
    return result;
  }

  /**
   * Returns the table of boolean such that table[i][j] if candidates[i] is
   * compatible with candidates[j].
   * 
   * @param candidates
   * @param filterSet
   * @return
   */
  private Boolean[][] buildCompatibilityTable(FrjIrregularSequent[] candidates) {
    Boolean[][] table = new Boolean[candidates.length][candidates.length];
    for (int i = 0; i < candidates.length; i++)
      for (int j = 0; j < candidates.length; j++)
        if (i != j) {
          if (areCompatible(candidates[i], candidates[j]))
            table[i][j] = true;
          else
            table[i][j] = false;
        }
    return table;
  }

  //
  private boolean areCompatible(FrjIrregularSequent i, FrjIrregularSequent j) {
    // check compatibility between i and j
    if (i.stable() != null)
      if (j.left() == null || !i.stable().subseteq(j.left()))
        return false;

    // check compatibility between j and i
    if (j.stable() == null)
      return true;
    else {
      if (i.left() == null)
        return false;
      else
        return j.stable().subseteq(i.left());
    }
  }

  private BitSetOfFormulas[] buildSigmaThetaRightSide(FrjIrregularSequent[] premises) {
    // build set of right sides
    FrjFormulaFactory formulaFactory = FrjFormulaFactory.getInstance();
    BitSetOfFormulas setOfRightSides = new BitSetOfFormulas(formulaFactory);
    BitSetOfFormulas sigma = new BitSetOfFormulas(formulaFactory);
    BitSetOfFormulas theta = formulaFactory.getGeneratedFormulas().clone();
    for (int i = 0; i < premises.length; i++) {
      FrjIrregularSequent seq = (FrjIrregularSequent) premises[i];
      setOfRightSides.add(seq.right());
      if (seq.stable() != null)
        sigma.or(seq.stable());
      if (theta != null && seq.nonStable() != null)
        theta.and(seq.nonStable());
      else
        theta = null;
    }
    // restrict theta_implicative to right_sides
    if (theta != null)
      this.implicativeRestriction(theta, setOfRightSides);
    return new BitSetOfFormulas[] { sigma, theta, setOfRightSides };
  }

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

  private void generate_IMPLIES() {
    BitSetOfFormulas impliesCandidate = candidateFormulas.getRight(FormulaType.IMPLIES_WFF);

    for (Formula AimpliesB : impliesCandidate) {
      Formula A = AimpliesB.immediateSubformulas()[0];
      Formula B = AimpliesB.immediateSubformulas()[1];
      {
        // get regular sequents with right = consequent
        Collection<FrjRegularSequent> premises = globalTable.regularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, B);
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
      {
        Collection<FrjIrregularSequent> premises =
            globalTable.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, B);
        if (premises != null)
          for (FrjIrregularSequent prem : premises) {
            if (prem.nonStable() != null) {
              // get the minimal coverings  of A
              Collection<BitSetOfFormulas> coverings =
                  candidateFormulas.buildMinimalClosureCoveringSets(prem, A);
              if (coverings != null)
                for (BitSetOfFormulas mincov : coverings) {
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
    BitSetOfFormulas orCandidate = candidateFormulas.getRight(FormulaType.OR_WFF);
    for (Formula wff : orCandidate) {
      Formula A0 = wff.immediateSubformulas()[0];
      Formula A1 = wff.immediateSubformulas()[1];

      // treat A0
      // premises proved in the last iteration with right=A0
      Collection<FrjIrregularSequent> last_iteration_premises_A0 =
          globalTable.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, A0);
      if (last_iteration_premises_A0 != null) {
        // apply rules for last_iteration_premises_A0 if there are proofs of A1 in global table
        Collection<FrjIrregularSequent> global_premises_A1 =
            globalTable.irregularWithRight(ProvedSequentsTables.GLOBAL, A1);
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
                applyRule(new Rule_OR(currentIteration, wff, preA0, preA1));
            }
      }

      // treat A1
      // premises proved in the last iteration with right=A0
      Collection<FrjIrregularSequent> last_iteration_premises_A1 =
          globalTable.irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, A1);
      if (last_iteration_premises_A1 != null) {
        // apply rules for last_iteration_premises_A1 if there are proofs of A0 in global table
        Collection<FrjIrregularSequent> global_premises_A0 =
            globalTable.irregularWithRight(ProvedSequentsTables.GLOBAL, A0);
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
                applyRule(new Rule_OR(currentIteration, wff, preA0, preA1));
            }
      }
    }
  }

  private void generate_AND() {
    BitSetOfFormulas andCandidate = candidateFormulas.getRight(FormulaType.AND_WFF);
    for (Formula wff : andCandidate) { // candidate is A_0 AND A_1
      for (int treatedConjunct = 0; treatedConjunct < 2; treatedConjunct++) { // for every conjunct (0,1) 
        // Apply Regular_AND to regular sequents with right=A_i          ;
        Collection<FrjRegularSequent> regularPremises = globalTable
            .regularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, wff.immediateSubformulas()[treatedConjunct]);
        if (regularPremises != null)
          for (FrjRegularSequent premise : regularPremises)
            applyRule(new Rule_AND_REGULAR(currentIteration, wff, premise));

        // Apply Focused_AND to focused sequents with right=A_i          ;
        Collection<FrjIrregularSequent> irregularPremises = globalTable
            .irregularWithRight(ProvedSequentsTables.LAST_COMMITTED_ITERATION, wff.immediateSubformulas()[treatedConjunct]);
        if (irregularPremises != null)
          for (FrjIrregularSequent premise : irregularPremises)
            applyRule(new Rule_AND_IRREGULAR(currentIteration, wff, treatedConjunct, premise));
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
    details.setGeneratedSequents(globalTable);
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
        globalTable.numberOfProvedSequents(ProvedSequentsTables.LAST_COMMITTED_ITERATION),
        globalTable.numberOfProvedSequents(ProvedSequentsTables.GLOBAL));
  }

  private void verbose_ruleApplication(_FrjRule rule, _FrjSequent conclusion) {
    MSGManager.info(MSG.VERBOSE.FRJPROVER.SEQUENT_DETAILS, rule.name(), conclusion.format());
  }

  // POWER SET
  public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {

    Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
    if (originalSet.isEmpty()) {
      sets.add(new HashSet<Integer>());
      return sets;
    }
    List<Integer> list = new ArrayList<Integer>(originalSet);
    Integer head = list.get(0);
    Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
    for (Set<Integer> set : powerSet(rest)) {
      Set<Integer> newSet = new HashSet<Integer>();
      newSet.add(head);
      newSet.addAll(set);
      sets.add(newSet);
      sets.add(set);
    }
    return sets;
  }

}
