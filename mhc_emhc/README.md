# Usage examples

To execute proof search with the **emhc** prover for MHC, run the following commands from this directory:

```
java -jar emhc.jar -i     # use the -i option to read the formula from standard input (see the syntax below)
java -jar emhc.jar file   # the file must specify the formula in the JTabWb format    (see the syntax below)
```

The directory [examples](examples) contains some problems in the JTabWb format.
For instance, the command

```
java -jar emhc.jar examples/mhc_axiom_c_completeness.jtabwb
```

searches for a derivation of the formula `p -> #p` described in the file `examples/mhc_axiom_c_completeness.jtabwb`.

To generate a LaTeX file describing the outcome of proof search (a derivation if
proof search succeeds or all the failed proof-trees otherwise) add the `-latex` option.

```
java -jar emhc.jar -latex examples/mhc_axiom_c_completeness.jtabwb   #  yields a derivation
java -jar emhc.jar -latex examples/mhc_unprovable_example_01.jtabwb  #  yields all the open proof-trees           
```

To generate a LaTeX file describing the counter-model extracted from a failed proof search, use `-model`:

```
java -jar emhc.jar -model examples/mhc_unprovable_example_01.jtabwb
```

Output files are saved in `out-jtabwb` by default. Use `--outdir dirname` to choose another output directory.

**Note**

If the text in the pdf file exceeds the page width, open the tex file and change the parameter of 
`\pdfpagewidth`. For instance, you can set: 

```
\pdfpagewidth 200in %% MAX WIDTH
```

Now the text in the pdf is very tiny and must be magnified.

To print the usage and all the available  options:

```
java -jar emhc.jar -h
```

Use `--latex-all` to save all the c-trees generated during proof search, `-v` to print proof-search details, `--log` to save execution details, and `--trace` to save the proof-search trace. The `--noabbr` option disables the abbreviation of `A -> false` as `~A` in LaTeX output.

To run all the example problems as a test set:

```
java -jar emhc.jar -testset examples/*.jtabwb
```

The default file reader is `jtabwb`. To read a file containing exactly one line with a formula, use `-r plain`. Run `java -jar emhc.jar --readers` for details about the available readers.

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
% Formula  : problem_name
% Status   : status
%------------------------
FORMULA
%------------------------
```



- `problem_name` is the name of the problem (used to generate the output file names);
- `status` is `provable` or `unprovable`;
- `FORMULA` specifies a formula using the above syntax.

For exemplifications, see the files with extension `.jtabwb` in the examples directory. 

