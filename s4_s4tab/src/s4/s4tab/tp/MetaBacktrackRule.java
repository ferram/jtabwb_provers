package s4.s4tab.tp;

import java.util.Collection;
import java.util.Iterator;

import s4.s4tab.calculus.Rule_F_NEC_nonInv;
import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine.NoSuchBacktrackRuleException;
import jtabwb.engine.ProofSearchResult;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.engine._MetaBacktrackRule;
import jtabwb.engine._OnRuleCompletedListener;
import jtabwbx.modal.formula.ModalFormula;

class MetaBacktrackRule implements _MetaBacktrackRule, _OnRuleCompletedListener {

  public MetaBacktrackRule(GlobalCache globalCache, LoopChecker loopchecker, _S4TabGoal goal,
      Collection<ModalFormula> backtrackFormulas) {
    super();
    this.globalCache = globalCache;
    this.loopChechker = loopchecker;
    this.goal = goal;
    this.toTreat = backtrackFormulas.iterator();
    this.totalNumberOfRules = backtrackFormulas.size();
  }

  private GlobalCache globalCache;
  private LoopChecker loopChechker;
  private _S4TabGoal goal;
  private Iterator<ModalFormula> toTreat;
  private int totalNumberOfRules;
  boolean firstExecution = true;

  @Override
  public String name() {
    return "META_BACKTRACK";
  }

  @Override
  public _AbstractGoal goal() {
    return goal;
  }

  @Override
  public int totalNumberOfRules() {
    return totalNumberOfRules;
  }

  @Override
  public _AbstractRule nextRule() throws NoSuchBacktrackRuleException {
    if (firstExecution) {
      loopChechker.push(goal);
      firstExecution = false;
    }
    return new Rule_F_NEC_nonInv(goal, toTreat.next());
  }

  @Override
  public boolean hasNextRule() {
    return toTreat.hasNext();
  }

  @Override
  public void onCompleted(ProofSearchResult status) {
    globalCache.put(goal, status);
    //loopChechker.pop(goal);  // this methods is used to check that the loop-checking strategy is correctly applied.
    loopChechker.pop();
  }

}
