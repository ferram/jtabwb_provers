Usage examples
==============

java -jar gbuSL.jar -i              ## with -i optipon read the formula from standard input
java -jar gbuSL.jar  file           ## the file must specify the formula in the JTabWb format
java -jar gbuSL.jar  -r plain file  ## the file must specify the formula in one single line



Formula syntax
==============

The input formula must be written in a text file. A formula F is specified by the following syntax:

F := atom        // prop. variable
   | false       // false
   | ~F          // not 
   | F & F       // and
   | F | F       // or
   | F => F      // implication
   | F <=> F     // bi-implication
   | # F         // box operator


JTabWb format
=============

The problem is specified in a file with the following structure:

%------------------------
% File     : problem_name
% Status   : status
%------------------------
FORMULA
%------------------------

where:
- formula_name is a string specifying the problem name;
- status is provable or unprovable;
- FORMULA specify a formula with the syntax described above.