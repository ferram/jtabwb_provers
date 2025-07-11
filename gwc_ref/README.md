# Implementation of a refutation calculus for the GWC - Gödel modal logic over witnessed crisp models


**gwcref** is the implementation of the logic **GWC**, which is
semantically characterized by witnessed Gödel crisp Kripke models. The
calculus at the base of the implementation is presented in:

>M. Ferrari, C. Fiorentini, R.O. Rodriguez.  A Gödel modal logic
>over witnessed crisp models. *TABLEAUX 2025*, to appear.


## Usage examples

To exec proof search with the **GwcRef** prover:

```
java -jar gwcref.jar -i             ## use the -i option to read the formula from standard input (see the syntax below)
java -jar gwcref.jar file           ## the file must specify the formula in the JTabWb format    (see the syntax below)
```

The directory   [examples](https://github.com/ferram/jtabwb_provers/tree/master/gwc_ref/examples)  contains some problems in the JTabWb format.
For instance, the command

```
java -jar gwcref.jar examples/gwc-axiom-KBox.jtabwb  
```
 searches for a derivation of the formula described in the file `examples/gwc-axiom-KBox.jtabwb`.

To generate a LaTeX file describing the outcome of proof search (a derivation if
proof search succeeds, an open proof-tree  and a countermodel if proof search fails) add the `-latex` option.

```
 java -jar gwcref.jar -latex  examples/gwc-axiom-KBox.jtabwb                #  yields a derivation
 java -jar gwcref.jar -latex  examples/gwc-unprovable-paper-ex1.jtabwb      #  yields an open proof-tree and a countermodel           
```


To print the usage and all the available  options:

```
java -jar gwcref.jar -h
```

**Note**

If the text in the pdf file exceeds the page width, open the tex file and change the parameter of 
`\pdfpagewidth`. For instance, you can set: 

```
\pdfpagewidth 200in %% MAX WIDTH
```

Now the text in the pdf is very tiny and must be magnified.


## Formula syntax

A formula `F` is specified by the following syntax:

```
F := atom        // propositional variable (every C-identifier)
   | false       // false
   | ~F          // not 
   | F & F       // and
   | F | F       // or
   | F => F      // implication
   | F -> F      // implication
   | F <=> F     // bi-implication
   | F <-> F     // bi-implication
   | # F         // box operator
   | !F          // diamond operator
```


Examples of formulas:

```
#(A => B) => (#A => #B)
#(p1 | q) => (#p1 | !q)
~!~~a -> #~a
```

Note that `~A` is an abbreviation for  `A => false`, `A <=> B` is an abbreviation for  `(A => B) & (B => A)`.


## JTabWb format

A problem can be specified in a file having the following structure:

```
%------------------------
% File     : problem_name
% Status   : status
%------------------------
FORMULA
%------------------------
```



- `problem_name` is the name of the problem (used to generate the output file names);
- `status` is `provable` or `unprovable`;
- `FORMULA` specifies a formula using the above syntax.

For exemplifications, see the files with extension `.jtabwb` in the directory
[examples](https://github.com/ferram/jtabwb_provers/tree/master/gwc_ref/examples). 

