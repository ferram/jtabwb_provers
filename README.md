
# Recently added: [gwcref](https://github.com/ferram/jtabwb_provers/tree/master/gwc_ref)


* **gwcref** is the implementation of the refutation calculus for the
  **Gödel modal logic over witnessed crisp models (GWC)** presented in:

  >M. Ferrari, C. Fiorentini, R.O. Rodriguez.  A Gödel modal logic
  >over witnessed crisp models. *TABLEAUX 2025*, to appear, 2025.


# Recently added: [gbuICK4](https://github.com/ferram/jtabwb_provers/tree/master/ick4_gbuICK4)

* **gbuICK4** is the implementation of a calculus for the **minimal
  coreflection logic iCK4**, an intuitionistic modal logic with the
  normality axiom and the coreflection principle. The calculus at the
  base of the implementation is presented in:

  >M. Ferrari, C. Fiorentini, P. Giardini. Proof search and
  >countermodel construction for **iCK4**, published in Proceedings of
  >*CILC 2025: 40th Italian Conference on Computational Logic*, to
  >appear, 2025.

# A collection of JTabWb provers


## Classical Propositional Logic (cpl)

* **clnat** is the implementation of the proof-search procedure for the
  natural deduction calculus for Classical Propositional Logic
  presented in

  >M. Ferrari and C. Fiorentini. Proof-search in natural deduction
  >calculus for classical propositional logic. In H. De Nivelle,
  >editor, TABLEAUX 2015, LNCS, vol. 9323, pages 237–252. Springer
  >International Publishing, 2015

  This procedure does not require backtracking nor
  loop-checking. **clnat** generates counter-models for the unprovable
  formulas.

* **g3c** is a simple example of implementation of a prover for
  Classical Propositional Logic based on the sequent calculus **G3c**.



## Intuitionistic Propositional Logic (ipl)

* **frj** is the implementation of the forward calculus FRJ(G) for
  Intuitionistic unprovability originally described in

  > C. Fiorentini and M. Ferrari. A Forward Unprovability Calculus for
  > Intuitionistic Propositional Logic. In R. A. Schmidt and
  > C. Nalon, editors, *TABLEAUX 2017*, LNCS, vol. 10501, pages
  > 114–130. Springer International Publishing, 2017

  The current version allows the user to extract a direct proof of the
  goal G in the GBU-calculus for intuitionistic provability from the
  saturated database generated by failed proof-search in FRJ(G).


* **f3ni** is the implementation of the O(n log n)-space decision
  procedure for IPL based on the duplication-free tableau calculus of

  > M. Ferrari, C. Fiorentini, and G. Fiorino. A Tableau Calculus for
  > Propositional Intuitionistic Logic with a Refined Treatment of
  > Nested Implications. Journal of Applied Non-Classical Logics,
  > 19(2):144-166, 2009.  [http://dx.doi.org/10.3166/jancl.19.149-166]


* **g3ibu** is the implementation of the calculus **Gbu** presented in

  > M. Ferrari, C. Fiorentini, and G. Fiorino. A terminating
  > evaluation-driven variant of G3i. In D. Galmiche and
  > D. Larchey-Wendling, editors, TABLEAUX 2013, LNCS, volume 8123,
  > pages 104-118. Springer-Verlag,
  > 2013. [http://dx.doi.org/10.1007/978-3-642-40537-2_11]

  **g3ibu** also implement the dual calculus **Rbu** for
  intuitionistic unprovability which generates Kripke counter-models
  for the unprovable sequents and their LaTeX rendering.


* **g3ied** is the implementation of the proof-search procedure for the
  sequent calculus **G3i** using evaluations to avoid loop-checking
  presented in

  > M. Ferrari, C. Fiorentini, and G. Fiorino. An Evaluation-Driven
  > Decision Procedure for G3i. ACM Transactions on Computational
  > Logic (TOCL), 6(1):8:1–8:37,
  > 2015. [http://dx.doi.org/10.1145/2660770]

  **rg3ied** implements the dual calculus **RJ** for intuitionistic
  unprovability which generates Kripke counter-models for the
  unprovable sequents and their LaTeX rendering.

* **g3iswiss** implements the proof-search procedure for the sequent
  calculus **G3i** for IPL using Swiss histories to avoid loops
  presented in

  > J.M. Howe. Two Loop Detection Mechanisms: A Comparision. In
  > D. Galmiche, editor, TABLEAUX 1997, LNCS 1227, Springer, 1997.



* **jfcube** implements the basic version of the *FCube* prover
  described in

  > M. Ferrari, C. Fiorentini, and G. Fiorino. FCube: An Efficient
  > Prover for Intuitionistic Propositional Logic. In C. G. Fermuller
  > and A. Voronkov, editors, Logic for Programming, Artificial
  > Intelligence, and Reasoning, LPAR-17, volume 6397, pages
  > 294-301. Springer, 2010.
  > [http://dx.doi.org/10.1007/978-3-642-16242-8_21]

  It is based on a tableau calculus and it exploits some of the
  optimization techniques discussed in


  > M. Ferrari, C. Fiorentini, and G. Fiorino. Simplification Rules
  > for Intuitionistic Propositional Tableaux. ACM Transactions on
  > Computational Logic (TOCL), 13(2),
  > 2012. [http://dl.acm.org/citation.cfm?doid=2159531.2159536]



* **lsj** is the proof-search procedure for the sequent
  calculus **LSJ** of

  > M. Ferrari, C. Fiorentini, and G. Fiorino. Contraction-free Linear
  > Depth Sequent Calculi for Intuitionistic Propositional Logic with
  > the Subformula Property and Minimal Depth Counter-Models. Journal
  > of Automated Reasoning, 51(2):129-149,
  > 2013. [http://dx.doi.org/10.1007/s10817-012-9252-7]

  **LSJ** is a contraction-free sequent calculus for IPL based on a
  non-standard notion of sequent; derivations of *LSJ* have linear
  depth in the sequent to be proved.  **lsj** also implements the dual
  calculus **RJ** for unprovability. It generates Kripke
  counter-models for the unprovable sequents and their LaTeX
  rendering.


* **nbu** is the implementation of a goal-oriented proof-search
  procedure for the natural deduction calculus for IPL.


## Intuitionistic Strong Löb Logic (**iSL**)

* **gbuSL** is the implementation of the calculus for **Intuitionistic
    Strong Löb Logic** **iSL** presented in

  > C. Fiorentini, M. Ferrari. A Terminating Sequent Calculus for
  > Intuitionistic Strong Löb Logic with the Subformula Property. In
  > C. Benzmüller, M. J. H. Heule, and R. A. Schmidt, editors, IJCAR
  > 2024, volume 14740 of LNCS, 24–42, , Springer, 2024. DOI:
  > [http://dx.doi.org/10.1007/978-3-031-63501-4_2]

  The prover also provide an implementation of the refutation calculus
  **Rbu** for **iSL**.


## S4 (s4)


* **s4tab** is the implementation of the proof-search procedure for
  modal logic S4 based on the tableau calculus presented in

  > P. Miglioli and U. Moscato and M. Ornaghi. Refutation systems for
  >  propositional modal logics, *TABLEAUX 1995*, P. Baumgartner and
  >  R. Hänle and J. Posegga, Eds., LNCS 918, Springer-Verlag, 1995.

  Termination is guaranteed via loop-checking.




  
