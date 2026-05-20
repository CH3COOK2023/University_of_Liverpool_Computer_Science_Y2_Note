# Java 环境

在该实验中，使用版本`Java 8(1.8)` 及以上版本，有两种运行方式，第一，可以使用 Intellij IDEA 打开该项目，目录为`COMP212_ASSIGNMENT_I`。运行 `src/Demo.java` 类以查看Demo，如果需要运行实验数据，请运行 `src/Experiment.java`。



运行`src/Experiment.java`完毕后，在根目录会生成四个文件，分别是`taskA.txt`、`taskB.txt`、`taskC.txt`以及`tastD.txt`，如果需要转化为可视化图表，则需要使用`python`



# 终端运行

如果希望在终端运行本项目，请进入`src`文件夹， 编译所有的Java类（但是默认已编译完成）

```bash
javac *.java */*.java
```

我们可以运行 Demo

```bash
java Demo
```

随后就可以运行`Experiment.java`以复现数据：

```bash
java Experiment
```



# 创建图表

在使用`python`前，必须安装如下两个库`matplotlib`以及`numpy`

```bash
pip install matplotlib
pip install numpy
```



随后，在终端运行`src/MakeTheGraph.py`，例如，希望创建`taskA`的对应图表，则

```bash
python MakeTheGraph.py -path taskA.txt -xname "Processor Numbers"
```

其中`-path`是数据的路径，`-xname`是横坐标轴的名称



# 注意事项

如果使用 Intellj IDEA 运行，那么`taskA.txt`等文件夹会放在 `COMP212_ASSIGNMENT_I` 目录下，如果使用终端运行，那么`taskA.txt`等文件夹会放在 `COMP212_ASSIGNMENT_I/src` 文件下



# 复现项目

此处，我们使用终端复现该项目！

```bash
COMP212_ASSIGNMENT_I$ cd src
COMP212_ASSIGNMENT_I/src$ javac *.java */*.java
COMP212_ASSIGNMENT_I/src$ java Experiment
```

等待项目运行，然后创建图表（假设`python`环境已安装完毕）

```bash
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskA.txt -xname "Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskB.txt -xname "Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskC.txt -xname "Subnet Processor Numbers"
COMP212_ASSIGNMENT_I/src$ python MakeTheGraph.py -path taskD.txt -xname "Interface Processor Numbers"
```

随后，在`COMP212_ASSIGNMENT_I/src`目录下即可找到对应图表。

















