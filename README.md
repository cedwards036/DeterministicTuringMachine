# Deterministic Turing Machine
### Basic operation
You can run the Deterministic Turing Machine simulation program using the command 

```java DTMMain [filepath to state transition spec file] [optional output directory]```

The program accepts as input:
* A file specifying the state transitions for the DTM
* An optional output directory into which any output files will be placed. If this
is not specified, then all output will be printed to the console

### State Transition Spec File Syntax
The state transition spec file must contain the following elements:
* The first line of non-commented content should specify the initial state of the DTM
* the second line of non-commented content should specify the character used as the 
  "blank" symbol
* the rest of the file should consist of state transitions of the form:
        
        given state, given char, next state, char to write, direction
    where ```given state``` is the current state of the DTM, ```given char``` is the char
    read from the tape, ```next state``` is the next state of the DTM, ```char to write``` 
    is the char to write to the tape, and ```direction``` is either ```l``` (for Left) 
    or ```r``` (for Right).
* Any line beginning with ```//``` is ignored as a comment
    
For example, a valid state transition spec file might begin with:
```
state1
b
//beginning of state transitions
state1, 1, state2, b, l
```

This file declares that the starting state is ```state1``` and the blank character for this
program is ```b```, then describes a state transition:
Given that we are in ```state1```, if we read character ```1```, then go to ```state2```, 
write ```b``` to the tape and move the tape head one square to the left.

### Included Input Files

##### add_two_nums_dtm.txt
This file performs binary addition on two numbers given an input tape of the form 
{0, 1}*#{0, 1}\*. For example, given 101#10, it would produce the output tape 111#00. 
The "answer" is the binary string to the left of the "#" (i.e. 111);
 
##### sub_two_nums_dtm.txt
This file performs binary subtraction on two numbers given an input tape of the form 
{0, 1}*#{0, 1}\*. For example, given 101#10, it would produce the output tape 011#00. 
The "answer" is the binary string to the left of the "#" (i.e. 011). 

##### mult_two_nums_dtm.txt
This file performs binary multiplication on two numbers given an input tape of the form 
{0, 1}*#{0, 1}\*. For example, given 101#10, it would produce the output tape 1010|101&000#00. 
The "answer" is binary string to the left of the "|" (i.e. 1010). 

