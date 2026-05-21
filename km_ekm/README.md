# Usage examples

To exec proof search with the **gbuICK4** prover:

```
java -jar ekm.jar -i   # use the -i option to read the formula from standard input (see the syntax below)
java -jar ekm.jar file # the file must specify the formula in the JTabWb format    (see the syntax below)
```

The directory   [examples](https://github.com/ferram/jtabwb_provers/tree/master/ekm/examples)  contains some problems in the JTabWb format.
For instance, the command

```
java -jar ekm.jar file examples/km_axiom_c_completeness.jtabwb  
```

searches for a derivation of the formula described in the file `examples/km_axiom_c_completeness.jtabwb`.

To generate a LaTeX file describing the outcome of proof search (a derivation if
proof search succeeds or all the failed proof-trees otherwise) add the `-latex` option.

```
java -jar ekm.jar -latex examples/km_axiom_c_completeness.jtabwb        #  yields a derivation
java -jar ekm.jar -latex examples/km_unprovable_godel_lob_axiom.jtabwb  #  yields all the open proof-trees           
```

**Note**

If the text in the pdf file exceeds the page width, open the tex file and change the parameter of 
`\pdfpagewidth`. For instance, you can set: 

```
\pdfpagewidth 200in %% MAX WIDTH
```

Now the text in the pdf is very tiny and must be magnified.



```
java -jar ekm.jar -h
```

To print the usage and all the available  options:

# Formula syntax

A formula `F` is specified by the following syntax:

```
F := atom        // propositional variable (every C-identifier)
   | false       // false
   | ~F          // not 
   | F & F       // and
   | F | F       // or
   | F => F      // implication
   | F -> F      // implication
   | # F         // box operator
```


Examples of formulas:

```
#(A => B) => (#A => #B)
#(p1 | q) => (#p1 | q)
~#~#~a -> #~a
```

Note that `~A` is an abbreviation for  `A => false`, `A <=> B` is an abbreviation for  `(A => B) & (B => A)`.


# JTabWb format

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

For exemplifications, see the files with extension `.jtabwb` in the examples directory. 

