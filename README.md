#A collection of JTabWb provers


##Provers for Classical Propositional Logic (cpl)


* **clnat** is the implementation of the proof-search procedure for the
  natural deduction calculus for Classical Propositional Logic
  presented in

  >M. Ferrari and C. Fiorentini. Proof-search in natural deduction
  >calculus for classical propositio- nal logic. In H. De Nivelle,
  >editor, TABLEAUX 2015, LNCS, vol. 9323, pages 237–252. Springer
  >International Publishing, 2015

  This procedure does not require backtracking nor
  loop-checking. **clnat** generates counter-models for the unprovable
  formulas.

* **cplg3c** is a simple example of implementation of a prover
  for Classical Propositional Logic based on the sequent calculus
  G3c of

> A. Troelstra and H. Schwichtenberg. Basic Proof Theory , vol.43 of
>  Cambridge Tractsin Theoretical Computer Science, 2nd edition,
>  Cambridge University Press, 2000.

