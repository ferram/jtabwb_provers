# Usage examples

To exec proofsearch with the **GbuSL** prover:

```
java -jar gbuSL.jar -i              ## -i option to read the formula from standard input
java -jar gbuSL.jar  file           ## the file must specify the formula in the JTabWb format
java -jar gbuSL.jar  -r plain file  ## the file must specify the formula in one single line
```

the examples directory contains some problems in the JTabWb format.
To exec proofsearch with the **RbuSL** calulus add the **-p rbuSL**
option. Exmaple:

```
java -jar gbuSL.jar -p rbuSL examples/isl_simple_unprov_paperExample.jtabwb
```

To generate the latex of proofsearch trees add the **--latex-ctrees** option.

To print the usage help:

```
java -jar gbuSL.jar -h
```





# Formula syntax

The input formula must be written in a text file. A formula F is specified by the following syntax:

```
F := atom        // prop. variable
   | false       // false
   | ~F          // not 
   | F & F       // and
   | F | F       // or
   | F => F      // implication
   | F <=> F     // bi-implication
   | # F         // box operator
```

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