![abs](https://user-images.githubusercontent.com/95234842/224309175-61d5bcd6-e9f6-4a12-bf7b-3d5053125c49.jpg)

# FSA-validator
This is project with the implementation of FSA validator.

## **Formats**: 
          --> input: "fsa.txt"
          --> output: "result.txt"


## **Validation Result**:
| Order | Error |
|------:|-------|
|   1   | E1: A state 's' is not in the set of states |
|   2   | E2: Some states are disjoint |
|   3   | E3: A transition 'a' is not represented in the alphabet |
|   4   | E4: Initial state is not defined |
|   5   | E5: Input file is malformed |

If the error was occured --> print error message and terminate.

## **Report**:
FSA is complete/incomplete

## **Warnings**:
| Order | Warning |
|------:|---------|
|   1   | W1: Accepting state is not defined |
|   2   | W2: Some states are not reachable from the initial state |
|   3   | W3: FSA is nondeterministic |

## **Input File Format**: 
states=[s1,s2,...]	  // s1 , s2, ... ∈ latin letters, words and numbers

alpha=[a1,a2, ...]	  // a1 , a2, ... ∈ latin letters, words, numbers and character '_’(underscore)

init.st=[s]	          // s ∈ states

fin.st=[s1,s2,...]	  // s1, s2 ∈ states

trans=[s1>a>s2,... ]  // s1,s2,...∈ states; a ∈ alpha
