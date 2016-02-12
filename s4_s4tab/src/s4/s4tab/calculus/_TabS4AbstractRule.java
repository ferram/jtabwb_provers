package s4.s4tab.calculus;

import s4.s4tab.nodeset._S4TabGoal;
import jtabwb.engine._AbstractRule;


public interface _TabS4AbstractRule extends _AbstractRule {
	
	public TabS4RuleIdentifiers getRuleIdentifier();
	
	public _S4TabGoal goal();

}
