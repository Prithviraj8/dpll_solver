This application solves Sodoku puzzle using DPLL algorithm

### Prerequisite software

1. Java version 8 or higher
2. Maven version 3.1 or higher

### Build and Run Instructions

#### Application builds and runs on CIMS crackle and snappy systems.

Below are the sequence of commands to execute.
``` 
unzip sudokuSolver.zip
cd ./sudokuSolver
mvn clean package
java -jar ./target/sudokuSolver-1.0.jar [-v] [[row][col]=[num]]
```

### Resources Used

The cnf clauses were generated using hints from page titled "Sudoku as a SAT Problem" by Inˆes Lynce and Jo¨el Ouaknine 
