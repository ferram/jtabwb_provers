# Implementation of a refutation calculus for the Gödel modal logic over witnessed crisp models (GWC)


**gwref** is the implementation of the logic **GW**, which is
semantically characterized by **witnessed Gödel Kripke models**. The
calculus at the base of the implementation is presented in:

>M. Ferrari, C. Fiorentini, P. Giardini, R.O. Rodriguez.  A Gödel Modal Logic
>Over Witnessed  Models. Submitted to *AiML 2026*.


## Usage examples

To exec proof search with the **GwRef** prover:

```
java -jar gwref.jar -i             ## use the -i option to read the formula from standard input (see the syntax below)
java -jar gwref.jar file           ## the file must specify the formula in the JTabWb format    (see the syntax below)
```

The directory   [examples](https://github.com/ferram/jtabwb_provers/tree/master/gw_ref/examples)  contains some problems in the JTabWb format.
For instance, the command

```
java -jar gwref.jar examples/gw-axiom-KBox.jtabwb  
```
 searches for a derivation of the formula described in the file `examples/gw-axiom-KBox.jtabwb`.

To generate a LaTeX file describing the outcome of proof search (a derivation if
proof search succeeds, an open proof-tree  and a countermodel if proof search fails) add the `-latex` option.

```
 java -jar gwref.jar -latex  examples/gw-axiom-KBox.jtabwb                #  yields a derivation
 java -jar gwref.jar -latex  examples/??      #  yields an open proof-tree and a countermodel           
```


To print the usage and all the available  options:

```
java -jar gwref.jar -h
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

