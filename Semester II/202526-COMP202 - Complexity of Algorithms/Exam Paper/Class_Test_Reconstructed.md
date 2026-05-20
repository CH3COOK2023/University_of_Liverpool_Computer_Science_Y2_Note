# COMP202 Class Test (CA1) — 原题还原

> **原始试卷**：15 道 MCQ，40 分钟，占最终成绩 15%
> **考试日期**：2026年3月10日 16:10-16:50，Sherrington LT2
> **考纲范围**：Week 1-5
>
> **还原依据**：
> 1. `class-test-discussion_annotated.pdf`（Nikhil 手写注解版，vision 模型逐字读取）
> 2. Class test discussion 课堂录音 SRT 转写（Nikhil 逐题讲解）
>
> **注意**：以下为根据 Nikhil 讲解内容反推的原题，可能与真实试卷措辞有细微差异，但考点和正确答案完全一致。

---

## Question 1

**Which of the following is true?**

(Note: log n = log₂ n)

- A. For all constants c > 0, log n = O(n^c)  ← **正确**
- B. log n = Ω(n^0.5)
- C. There exists a constant c > 0 such that log n = Θ(n^c)
- D. n³ = O(n²)
- E. 2^n = O(log n)

> **Nikhil 点评**：不到一半人答对。log N 的增长比任何多项式幂都慢，所以 log N = O(N^c) ∀c>0。这个 Week 1 就讲了。

---

## Question 2

**T(n) = 8T(n/5) + 7n，假设 T(n) 对小 n 为常数。求 T(n) 的渐近界。**

- A. Θ(n^(log₅ 8))  ← **正确（Master Method Case 1）**
- B. Θ(n log n)
- C. Θ(n²)
- D. Θ(n³)
- E. Θ(n⁸)

> **Nikhil 点评**：大多数人做得很好。log₅8 ≈ 1.29 > 1，所以 Case 1 适用 → Θ(n^(log₅8))。注意 log₅8 在 log₅5=1 和 log₅25=2 之间，不用计算器也能判断 > 1。

---

## Question 3

**T(n) = 100T(n/10) + 1000n¹⁰，假设 T(n) 对小 n 为常数。求 T(n) 的渐近界。**

- A. Θ(n²)
- B. Θ(n log n)
- C. Θ(n¹⁰)  ← **正确（Master Method Case 3）**
- D. Θ(n^(log₁₀ 100))
- E. Θ(n¹¹)

> **Nikhil 点评**：log₁₀100 = 2 < 10，所以 Case 3 → Θ(n¹⁰)。大部分人答对。

---

## Question 4

**T(n) = 5T(n/5) + 5n log⁵ n，假设 T(n) 对小 n 为常数。求 T(n) 的渐近界。**

- A. Θ(n log⁵ n)
- B. Θ(n log⁶ n)  ← **正确（Master Method Case 2 + polyLog）**
- C. Θ(n²)
- D. Θ(n log n)
- E. Θ(n⁵)

> **Nikhil 点评**：这是 Master Method 中比较 tricky 的应用。先把 f(n) 中的 log 因子丢掉，剩下 N¹。log₅5 = 1，两边相等 → Case 2。Case 2 扩展：T(n) = Θ(f(n) · log n) = Θ(n log⁵ n × log n) = Θ(n log⁶ n)。有些人在这里卡住了。

---

## Question 5

**In Karatsuba's fast integer multiplication algorithm, multiplying two n-bit numbers reduces the problem to how many subproblems of multiplying two n/2-bit numbers?**

- A. 2
- B. 3  ← **正确**
- C. 4
- D. 7
- E. 8

> **Nikhil 点评**：普通高中数学乘法会产生 4 个子问题；Karatsuba 展示了如何减少到 3 个。Strassen（矩阵乘法）是 7 个子问题，不要混淆。有些人在这个问题上卡住了。Karatsuba 是 Week 2 Divide & Conquer 中我们看到的唯一一个快速整数乘法算法。

---

## Question 6

**Repeatedly toss a fair coin (probability 1/2 heads) until you see heads, then stop. Repeat this experiment a second time. Let X = number of tosses in the first experiment, Y = number of tosses in the second experiment. What is E[X+Y]?**

- A. 2
- B. 3
- C. 4  ← **正确**
- D. 8
- E. 1

> **Nikhil 点评**：几何分布 E[X] = 1/p = 2。由线性期望：E[X+Y] = E[X] + E[Y] = 2 + 2 = 4。大多数人做得很好。

---

## Question 7

**A bag contains 100 balls numbered 1 to 100. If a ball is selected uniformly at random, what is the probability that the number on the ball is between 1 and 40 (inclusive)?**

- A. 1/100
- B. 40/100 = 2/5  ← **正确**
- C. 1/40
- D. 1/2
- E. 39/100

> **Nikhil 点评**：几乎所有人都答对了。100 个等可能结果，40 个有利 → 40/100 = 2/5。纯基础概率。

---

## Question 8

**What is the expected running time of the Randomized Select algorithm to find the n/2-th largest element in an array of n elements?**

- A. O(n)  ← **正确**
- B. O(n log n)
- C. O(log n)
- D. O(1)
- E. O(n²)

> **Nikhil 点评**：选 O(n log n) 的人比选 O(n) 的人多，这让他很意外。Randomized Quicksort 是 O(n log n) 期望时间用来排序；Randomized Select 找第 i 大只需要 O(n) 期望时间。两者有本质区别，Select 省掉了那 log n 因子。

---

## Question 9

**Consider the following max-heap:**

```
        19
       /  \
      11   18
     / \   / \
    10  6 15  17
```

**After performing 4 remove-max operations on this heap, what value appears at the root?**

- A. 15
- B. 11  ← **正确**
- C. 10
- D. 6
- E. 17

> **Nikhil 点评**：多数人答对。不需要执行完整的 remove-max 操作。关键思路：max-heap 中根总是最大值。只需把堆中数字降序排列：19, 18, 17, 15, 11, 10, 6。经过 4 次 remove-max，前 4 个最大值被移除了，根变成第 5 大的 11。

---

## Question 10

**What is the worst-case running time of a remove-max operation on a heap containing n elements?**

- A. O(1)
- B. O(log n)  ← **正确**
- C. O(n)
- D. O(n log n)
- E. O(n²)

> **Nikhil 点评**：纯书本题。堆的深度是 O(log n)，remove-max 涉及一次交换 + 向下冒泡 → O(log n)。

---

## Question 11

**Classify the following sorting algorithms as comparison-based or non-comparison-based:**

**Algorithms: Heapsort, Radix Sort, Mergesort, Quicksort, Counting Sort**

**Which of the following correctly classifies these algorithms?**

- A. Comparison-based: Heapsort, Mergesort, Quicksort | Non: Radix Sort, Counting Sort  ← **正确**
- B. Comparison-based: Heapsort, Radix Sort | Non: Mergesort, Quicksort, Counting Sort
- C. Comparison-based: all except Counting Sort | Non: Counting Sort only
- D. Comparison-based: Heapsort only | Non: the rest
- E. All are comparison-based

> **Nikhil 点评**：大多数答对。比较排序以比较两个数的大小为基本操作；非比较排序（基数排序、计数排序）深入数字本身的结构，不直接比较整个数字。

---

## Question 12

**Which of the following is true about all comparison-based sorting algorithms?**

- A. They must take Ω(n log n) time in the worst case  ← **正确**
- B. They can sort in O(n) time in the best case
- C. They are always in-place
- D. They are always stable
- E. They must take Ω(n²) time in the worst case

> **Nikhil 点评**：这是我们课程中看到的第一个"不可能性结果"（下界）。任何仅依赖比较的排序算法最坏情况必须做 Ω(n log n) 次比较。这是通过决策树模型证明的。

---

## Question 13

**What is the asymptotic number of bits required to represent the nth Fibonacci number F(n) in binary?**

(Recall: F(1)=F(2)=1, F(3)=2, F(4)=3, F(5)=5, ... and F(n) ≈ 1.618ⁿ)

- A. O(1)
- B. O(log n)
- C. O(n)  ← **正确**
- D. O(n²)
- E. O(2^n)

> **Nikhil 点评**：这道题多数人答错，是课程中数学上比较深入的部分（Week 5）。
> 推导：F(n) ≈ 1.618ⁿ ≈ 1.6ⁿ。表示数字 k 需要的位数 ≈ O(log k)。所以位数 = O(log(1.6ⁿ)) = O(n × log 1.6) = O(n)（因为 log 1.6 是常数）。

---

## Question 14

**Which of the following is known to be true about the 0-1 Knapsack problem?**

- A. There is a greedy algorithm that always finds the optimal solution in O(n log n) time
- B. There is no known algorithm that finds the optimal solution
- C. There is a dynamic programming algorithm that runs in O(nW) time  ← **正确**
- D. The problem is NP-complete, so no algorithm exists
- E. The problem can be solved in O(n) time

> **Nikhil 点评**：也有一半以上的人答错。Week 5 我们学了 0-1 Knapsack 的 DP 解法，运行时间 O(nW)（伪多项式时间）。贪心在这里不 work（贪心只对 Fractional Knapsack 有效）。0-1 Knapsack 确实是 NP-hard，但 DP 仍能在 O(nW) 时间内找到精确解——只是 W 可能指数级大。

---

## Question 15

**What is the correct greedy choice for the Interval Scheduling problem (maximizing the number of non-overlapping intervals)?**

- A. Always pick the interval that starts the earliest
- B. Always pick the shortest interval
- C. Always pick the interval that overlaps with the fewest other intervals
- D. Always pick the next valid interval that finishes the earliest  ← **正确**
- E. Always pick the interval that starts the latest

> **Nikhil 点评**：不少人也卡在这道题。我们在课堂上展示了：选最早开始、选最短、选重叠最少的贪心策略都 counterexample 可证伪。唯一正确的是选"最早结束"的区间。这是经典的 Interval Scheduling / Activity Selection 贪心算法。

---

## 统计摘要

| 题目 | 主题 | Week | 难度（Nikhil评价） |
|------|------|------|---------------------|
| Q1 | Big-O 定义 | 1 | 不到一半答对 |
| Q2 | Master Method Case 1 | 2 | 多数答对 |
| Q3 | Master Method Case 3 | 2 | 多数答对 |
| Q4 | Master Method Case 2+polyLog | 2 | tricky，部分卡住 |
| Q5 | Karatsuba 子问题数 | 2 | 部分卡住 |
| Q6 | 几何分布+线性期望 | 3 | 多数答对 |
| Q7 | 基础概率 | 3 | 几乎全对 |
| Q8 | Randomized Select vs Quicksort | 3 | 半数以上选错 → O(n log n) |
| Q9 | Max-Heap remove-max | 4 | 多数答对 |
| Q10 | Remove-max 运行时间 | 4 | 纯书本题 |
| Q11 | 比较排序 vs 非比较排序 | 4 | 多数答对 |
| Q12 | 比较排序下界 | 4 | 多数答对 |
| Q13 | Fibonacci 位数 | 5 | 多数答错（课程最数学化的部分） |
| Q14 | 0-1 Knapsack DP | 5 | 刚过半答错 |
| Q15 | Interval Scheduling 贪心 | 5 | 部分卡住 |

---

## 还原可信度

- **高度确定** Q1-Q15 全部正确答案 → 与 annotated PDF 手写答案 + SRT 逐题讲评完全吻合
- **中等推测** 选项 A-E 具体措辞 → 基于 Mock Questions 的选项风格 + Nikhil 口述的错误选项方向
- **SRT 中 Nikhil 提到 Q8 很多人选了 O(n log n)，Q14 错误选项涉及贪心等，这验证了干扰项的设计方向**
