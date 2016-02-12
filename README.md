##A collection of JTabWb provers


###Provers for Classical Propositional Logic (cpl)

* *clnat* is the implementation of the proof-search procedure for the
  natural deduction calculus for Classical Propositional Logic
  presented in

  >M. Ferrari and C. Fiorentini. Proof-search in natural deduction
  >calculus for classical propositional logic. In H. De Nivelle,
  >editor, TABLEAUX 2015, LNCS, vol. 9323, pages 237–252. Springer
  >International Publishing, 2015

  This procedure does not require backtracking nor
  loop-checking. *clnat* generates counter-models for the unprovable
  formulas.

* *g3c* is a simple example of implementation of a prover for
  Classical Propositional Logic based on the sequent calculus **G3c**.



###Provers for Intuitionistic Propositional Logic (ipl)

* *f3ni* is the implementation of the O(n log n)-space decision
  procedure for IPL based on the duplication-free tableau calculus of

  > M. Ferrari, C. Fiorentini, and G. Fiorino. A Tableau Calculus for
  > Propositional Intuitionistic Logic with a Refined Treatment of
  > Nested Implications. Journal of Applied Non-Classical Logics,
  > 19(2):144-166, 2009.  [http://dx.doi.org/10.3166/jancl.19.149-166]


* *g3ibu* is the implementation of the calculus **Gbu** presented in

  > M. Ferrari, C. Fiorentini, and G. Fiorino. A terminating
  >  evaluation-driven variant of G3i. In D. Galmiche and
  >  D. Larchey-Wendling, editors, TABLEAUX 2013, LNCS, volume 8123,
  >  pages 104-118. Springer-Verlag, 2013.


* *g3ied* is the implementation of the proof-search procedure for the
  sequent calculus **G3i** using evaluations to avoid loop-checking
  presented in

  > M. Ferrari, C. Fiorentini, and G. Fiorino. An Evaluation-Driven
  >  Decision Procedure for G3i. ACM Transactions on Computational
  >  Logic (TOCL), 6(1):8:1–8:37, 2015.



* *g3iswiss* implements the proof-search procedure for the sequent
  calculus **G3i** for IPL using Swiss histories to avoid loops
  presented in

  > J.M. Howe. Two Loop Detection Mechanisms: A Comparision. In
  > D. Galmiche, editor, TABLEAUX 1997, LNCS 1227, Springer, 1997.



###Provers for S4 (s4)


* **s4tab** is the implementation of the proof-search procedure for
  modal logic S4 based on the tableau calculus presented in

  > P. Miglioli and U. Moscato and M. Ornaghi. Refutation systems for
  >  propositional modal logics, *TABLEAUX 1995*, P. Baumgartner and
  >  R. Hänle and J. Posegga, Eds., LNCS 918, Springer-Verlag, 1995.

  Termination is guaranteed via loop-checking.


