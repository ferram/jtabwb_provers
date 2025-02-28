# Usage examples

To exec proof search with the **GwcRef** prover:

```
java -jar gwcref.jar -i             ## use the -i option to read the formula from standard input
java -jar gwcref.jar file           ## the file must specify the formula in the JTabWb format
```

The **examples** directory contains some problems in the JTabWb format.

Example:

```
java -jar gwcref.jar examples/gwc-axiom-KBox.jtabwb
```

To generate the latex of a proof search (the proof for the successful
proof searches, the proof-tree and the countermodel for the unsuccessful
ones) add the -latex option.


To print the usage and the details of other options:

```
java -jar gwcref.jar -h
```


# Formula syntax

A formula F is specified by the following syntax:

```
F := atom        // propositional variable (every C-identifier)
   | false       // false
   | ~F          // not 
   | F & F       // and
   | F | F       // or
   | F => F      // implication
   | F <=> F     // bi-implication
   | # F         // box operator
   | !F          // diamond operator
```

Note that ~A is translated as (A => false) during proof search (while
~A is used as abbreviation of (A => false) in LaTeX.


# JTabWb format

The problem is specified in a file with the following structure:

```
%------------------------
% File     : problem_name
% Status   : status
%------------------------
FORMULA
%------------------------
```

where:
- formula_name is a string specifying the problem name;
- status is provable or unprovable;
- FORMULA specify a formula with the syntax described above.