# Java Environment

In this experiment, **Java 8 (1.8)** or higher is required. There are two ways to execute the project:

1. **IntelliJ IDEA:** Open the project directory `COMP212_ASSIGNMENT_I`. Run the `src/Demo.java` class to view the demonstration. To generate experimental data, run the `src/Experiment.java` class.
2. **Visualization:** After running `src/Experiment.java`, four files will be generated in the root directory: `taskA.txt`, `taskB.txt`, `taskC.txt`, and `taskD.txt`. To convert these into visual charts, **Python** is required.



# Running via Terminal

If you prefer to run the project from the terminal, enter the `src` folder and compile all Java classes (note: they may already be compiled by default):

```bash
javac *.java */*.java
```

To run the demonstration:

```bash
java Demo
```

To reproduce the experimental data:

```bash
java Experiment
```



# Generating Charts

Before using Python, you must install the following two libraries: `matplotlib` and `numpy`.

```bash
pip install matplotlib
pip install numpy

```

Next, run `src/MakeTheGraph.py` from the terminal. For example, to create the chart for `taskA`:

```bash
python MakeTheGraph.py -path taskA.txt -xname "Processor Numbers"
```

* `-path`: The file path to the data.
* `-xname`: The label for the x-axis.



# Notes

* **IntelliJ IDEA:** If running via the IDE, files like `taskA.txt` will be placed in the `COMP212_ASSIGNMENT_I` root directory.
* **Terminal:** If running via the terminal, these files will be placed within the `COMP212_ASSIGNMENT_I/src` folder.



# Project Reproduction

Follow these steps to reproduce the project results via the terminal:

```bash
COMP212_ASSIGNMENT_I$ cd src
COMP212_ASSIGNMENT_I/src$ javac *.java */*.java
COMP212_ASSIGNMENT_I/src$ java Experiment
```

Wait for the program to complete, then generate the charts (assuming the Python environment is set up):

```bash
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskA.txt -xname "Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskB.txt -xname "Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskC.txt -xname "Subnet Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskD.txt -xname "Interface Processor Numbers"
```





