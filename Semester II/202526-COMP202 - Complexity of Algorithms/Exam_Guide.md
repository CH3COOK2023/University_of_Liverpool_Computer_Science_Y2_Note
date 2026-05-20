# COMP202 期末考试指南 — 基于 Revision Lecture 逐句整理

> **来源**：Nikhil Mande 2026年4月28日 Revision Lecture 录音转写
> **SRT 文件**：`~/comp202_transcripts/srt/Revision_Revision_lecture*.srt`
> **复习 PPT**：`PPT/All-slides.pdf`（176页，已提取到 `all_pdf_combined.txt`，可直接搜索）
> **考试格式**：50 道 MCQ，2 小时

---

## 在考纲

### Week 1: Introduction & Big-O Notation

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:03:00` | You **should know** this minimum finding pseudo code | 最小值查找伪代码 |
| `00:03:04` | The number of primitive operations it takes. Worst case analysis. | 操作计数+最坏分析 |
| `00:03:08` | **Definitely** you **should be familiar with** big O notation, as well as big omega and big theta notation | Big-O/Ω/Θ 必考 |
| `00:03:16` | I **highly recommend** going through these examples to familiarize yourself with them | 熟悉所有 Big-O 例题 |
| `00:03:28` | This is a mistake a lot of you made on the class test | 课堂测验常见错误会再考 |
| `00:03:32` | log N is big O of N to the power C for all constants C. So log N is big O of N to the power 0.1 and so on | logN = O(N^c) ∀c>0 |
| `00:03:40` | Similarly, exponential 2 power N is faster growing than any polynomial. So 2 power N is big omega of N to the power C for every constant C | 2^N = Ω(N^c) ∀c |
| `00:03:52` | These are sort of facts that you **should know** | 以上事实必知 |
| `00:04:10` | It's **useful to know** at least this proof technique (mathematical induction) | 数学归纳法 |
| `00:02:04` | The materials that **will be relevant for the exam** are the lecture notes. But also do look at the recordings and the tutorials and solutions | 复习范围：课件+录像+教程+解答 |

### Week 2: Divide & Conquer + Master Method

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:04:34` | You **should be aware of** the general strategy of divide and conquer | D&C 策略 |
| `00:04:42` | You **should know** merge sort at this point | Merge Sort |
| `00:04:46` | You **should know** how the merge operation works. You **should be able to implement** it yourself | Merge 操作+实现 |
| `00:04:50` | You **should know** the running time of merge sort. Running time of the merge operation. Both worst and average cases: O(N log N) | Merge Sort 运行时间 |
| `00:05:04` | You **should also know** this recurrence relation that arises as the running time of merge sort | 递推关系 |
| `00:05:18` | You **should also be aware of** this substitution method... there might be examples of recurrences that you can't solve using the master method | Substitution Method |
| `00:05:36` | If I ask you T(N) = T(N-1) + 1... this is an example where you could use the substitution method and plug and chug | 非 Master Method 可解的递推 |
| `00:05:58` | If you know the definition of the maximum subarray problem... Just this divide and conquer approach is good enough | Max Subarray D&C 方法 |
| `00:06:12` | Matrix multiplication is another thing you **should be familiar with** | 矩阵乘法 |
| `00:06:32` | I **will expect you to remember** things like... Strassen's algorithm, it's a divide and conquer algorithm that reduces one N×N matrix multiplication to seven recursive N/2×N/2 multiplications | Strassen: 7子问题 |
| `00:06:49` | You **should know** that it gives you this recurrence relation. And you **should also know** how to solve this recurrence relation using the master method, which gives you this running time | 递推关系+Master Method 解→O(n^log₂7) |
| `00:07:17` | This [Master Method case 3] of course **is important** | Master Method Case 3 |
| `00:07:27` | You **should be aware of** the three cases otherwise | 三个 Case 全要会 |
| `00:07:33` | You can **definitely expect** me to ask you some questions just directly use the master method | Master Method 直接出题 |
| `00:07:47` | If you want to refresh your memory with examples there are several here as well as in the tutorials | 教程中的例题 |
| `00:08:00` | Variable change is another thing that you **might want to remember** as well | 变量替换技巧 |

### Week 3: Randomized Algorithms

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:08:50` | You **should be familiar with** basic probability theory which you already are | 基础概率论 |
| `00:09:04` | You **should know** things like sample spaces, events, how to calculate some simple probabilities | 样本空间/事件/概率计算 |
| `00:09:10` | Random variables, expectations, these are all things **I expect you to know** | 随机变量+期望 |
| `00:09:24` | You **should definitely know** for a fact that if I toss a fair coin repeatedly, you expect to see the first heads after two tosses | 公平硬币 E=2 次 |
| `00:09:34` | You **should know** quicksort, how it works. And you **should also know** the worst case running time as well as the average case running time when the pivot is chosen randomly. Worst case O(N²), expected O(N log N) | Quicksort |
| `00:10:14` | Randomized Select... You **should know** this fact here basically... it's just a variant of randomized quicksort. Expected running time O(N) | Randomized Select |
| `00:10:57` | The worst case running time is O(N²). And the expected running time is O(N). Both of these are facts that you **should know** | Select 最坏+期望 |

### Week 4: Sorting

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:11:33` | You **should definitely know** what the sorting problem is | 排序问题定义 |
| `00:11:37` | This is a claim that you **should know** as well. Every comparison based sorting algorithm must make at least Ω(N log N) comparisons in the worst case. This is the only instance in this course where we saw a lower bound | 比较排序下界 Ω(N log N) |
| `00:12:09` | But the final result you **should know** | 下界结论必知 |
| `00:12:21` | Priority queues and heaps. You **should know** the definitions of these things. You **should know** all of the operations that heaps allow for | 优先队列+堆定义 |
| `00:12:27` | And you **should also know** how all of these operations work: insert element, remove max, taking the size, finding the max element | 全部堆操作 |
| `00:12:45` | You **should know** the running time of each of these. And how they work | 每个操作运行时间 |
| `00:12:55` | And you **should be aware of** this running time of heapsort as well | HeapSort 运行时间 |
| `00:13:32` | You **should remember** how counting sort works. The fact that its worst case running time is O(N+K) | Counting Sort O(N+K) |
| `00:13:48` | You **should know** the definition of what a stable sorting algorithm is | 稳定排序定义 |
| `00:14:02` | You **should know** how the [radix sort] algorithm proceeds. And then you **should also know** the running time O(D×N) | Radix Sort O(D×N) |

### Week 5: Greedy + Dynamic Programming

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:15:06` | You **should definitely know** the problem description [of 0-1 Knapsack] | 0-1 Knapsack 定义 |
| `00:15:16` | You **should know** the dynamic programming based solution that we saw to it. You **should be able to implement** it yourself | DP 解法+能实现 |
| `00:15:22` | I might give you an instance to the knapsack problem and ask you what's the best solution | 可能考具体实例求解 |
| `00:15:32` | And you **need to know** the running time of the DP algorithm... O(N×W) | O(N×W) |
| `00:16:25` | You **should know** this memoization based approach which gives a linear time algorithm [for Fibonacci] | Fibonacci 备忘录 O(N) |
| `00:16:35` | You **should also know** this repeated squaring based algorithm that gives you O(log N) | Fibonacci 重复平方 O(log N) |
| `00:17:18` | What I **expect you to remember** is how we defined this quantity, this power series. And basically up to here is what I expect you to remember. How to derive this expression relating the power series to itself | Fibonacci 闭式前半段 |
| `00:17:47` | And how we got until this line. And then after that you can skip the proof | 推导到某一行即可 |
| `00:18:04` | The main thing to remember: the number of bits needed to represent the ith Fibonacci number in binary is O(i) | Fibonacci 位数 O(i) |
| `00:18:28` | This fact you **need to remember** | 必记 |
| `00:18:57` | I **expect you to remember** the problem definition here [Fractional Knapsack]. Again I might ask you: here's a fractional knapsack instance, can you solve it? | Fractional Knapsack 定义+实例求解 |
| `00:19:13` | You **should know** the problem description and how to solve it. And you **should also know** that this running time is O(N log N) because the main running time cost is in the sorting | 贪心 value/weight 排序 O(N log N) |
| `00:20:02` | You **should be aware of** the problem description here: what is the interval scheduling problem? | Interval Scheduling 定义 |
| `00:20:23` | This is another thing that you **should know**: these three greedy approaches do not work, provably | 知道哪三种贪心是错的 |
| `00:20:38` | What **does work** is choose the task that finishes the earliest. Remove that and so on, repeat | 正确贪心=最早结束 |
| `00:20:54` | You **should know** the running time of the algorithm and the algorithm itself | 算法+运行时间 |

### Week 6: Graphs & Shortest Paths

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:21:37` | We saw what graphs are. Directed graphs, undirected graphs. You **should know** definitions of these | 有向/无向图定义 |
| `00:21:45` | You **should know**... well at least you **should remember** these definitions. I know there is a lot of them but it is **useful to remember** them | 全部图论定义 |
| `00:22:04` | There is also a file called **proofs.pdf** on Canvas. That is also **very important** | **proofs.pdf 点名必看** |
| `00:22:16` | I might ask you which of the following properties is true about graphs? | 图的性质会出 MCQ |
| `00:22:26` | What's the sum of degrees of vertices in an undirected graph. And you **should know** it is 2 times number of edges | 度和=2×边数 |
| `00:22:44` | So yeah **please do look at** this proofs.pdf file | 再次强调 |
| `00:22:59` | You **should know** these theorem statements at least | 图论定理陈述 |
| `00:23:01` | You **should know** these 2 ways of representing a graph: the adjacency list and adjacency matrix use. Both of them | 邻接表+邻接矩阵 |
| `00:23:12` | You **should know** what a directed graph is | 有向图 |
| `00:23:49` | We saw weighted graphs. And then the single source shortest path problem | 加权图+单源最短路 |
| `00:24:26` | What you **do need to remember** is the Bellman-Ford algorithm | Bellman-Ford |
| `00:24:37` | There is also an example of a run of Bellman-Ford in one of the PDFs on Canvas. I recommend going through that example | Bellman-Ford 示例 PDF |
| `00:24:41` | You **should know** how the algorithm works. You **should know** the running time. You **should know** what it does. If there is a negative weight cycle, the algorithm output fails; otherwise it outputs single source shortest path | 算法流程+运行时间+负环行为 |
| `00:25:00` | **Please do look at** the digital PDF on the execution of Bellman-Ford that is on Canvas | 第三次强调 PDF |
| `00:25:28` | All pairs shortest paths... We could do Bellman-Ford repeatedly which would give O(N⁴). But we saw a more clever algorithm using min-plus matrix multiplication | All-pairs SP |
| `00:25:49` | You **should know** how we came up with these definitions [min-plus]. And you **should know** how this min plus matrix multiplication actually works | Min-plus 定义+操作 |
| `00:26:23` | And then you **should know** that the running time of this algorithm is O(N³ log N). But **definitely** this one [O(N³ log N)] **is important** | O(N³ log N) 重点 |

### Week 7: Network Flows

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:27:02` | You **should definitely know** what a flow network is | 流网络定义 |
| `00:27:07` | You **should know** this maximum flow problem, what it is | 最大流问题 |
| `00:27:22` | You **should know** the Ford-Fulkerson method, how it works. What augmenting paths are. How to augment along them | Ford-Fulkerson |
| `00:27:32` | Basically if I give you a flow network, you **should be able to execute** the Ford-Fulkerson method | **会手动执行** |
| `00:27:35` | At any stage you are allowed to choose any augmenting path | 任选增广路径 |
| `00:27:43` | You **should definitely know** what an augmenting path is. How to augment flow along that. How to come up with a residual network | 增广路径+流量增广+残差网络 |
| `00:27:55` | Actually you need to understand everything in this pseudo code over here. Forward edges, backward edges | 伪代码+前向边+后向边 |
| `00:28:18` | I recommend that you go through the example that we ran through here. You **should be able to do** this example yourself | 会跑示例 |
| `00:28:31` | I **expect you to know** what the definition of a cut is | 割的定义 |
| `00:28:41` | What the capacity of a cut is | 割容量 |
| `00:28:43` | You **should know** what value of flow is. You **should know** what capacity of cut is | 流值+割容量 |
| `00:28:47` | And you **should know** the statement of the **max flow min cut theorem** | Max-Flow Min-Cut 定理陈述 |
| `00:29:02` | Also the running time of the Ford-Fulkerson method. At least the naive Ford-Fulkerson method which is O(max_flow × m) | O(max_flow × m) |

### Week 8: Cryptography & RSA

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:30:25` | I **expect you to know** this general notion of encryption and decryption, what cryptography is | 密码学基本概念 |
| `00:30:33` | You **should know** what a symmetric encryption scheme is | 对称加密 |
| `00:30:36` | You **should know** what a substitution cipher is. As an example the Caesar cipher | 替换密码+凯撒密码 |
| `00:30:48` | Something that you might not have seen in other modules, which is **definitely important**, is the **one time pad** | **One-Time Pad 重点** |
| `00:30:52` | You **should know** how the one time pad works. I would again **highly recommend** that you go through this example. Remember its advantages, disadvantages | OTP 原理+优缺点 |
| `00:31:28` | You **should know** what public key cryptography is | 公钥加密 |
| `00:31:34` | You **should be aware of** basic number theory properties which I expect you already know: modulo operation, what GCD is, prime numbers, composite numbers | 模运算/GCD/质数/合数 |
| `00:31:51` | This theorem I **expect you to know** in the statement at least | 数论定理陈述 |
| `00:32:01` | You **should know** the modulo operator, congruence | 模同余 |
| `00:32:08` | I **definitely expect you to know Euclid's algorithm** | **Euclid 算法必考** |
| `00:32:19` | Euclid's algorithm is basically just repeat this line over and over again. You **should know** how it works. You **should be able to execute** it on two numbers | 会手动执行 Euclid |
| `00:32:25` | I **strongly recommend** that you look at these examples that we went through. And the Canvas page should have a separate PDF that runs through these examples | Euclid 示例 PDF |
| `00:32:41` | You **should know** the running time of Euclid's algorithm | Euclid 运行时间 |
| `00:33:30` | I **expect you definitely to know RSA** — this **is important**. You **can expect a few questions from here** | **RSA 重点出题区，必有多题** |
| `00:33:42` | How does the RSA method work? What are the properties of the keys chosen in RSA? You **should know** all of keys. You **should know** how encryption works, how decryption works | RSA 全流程：密钥/加密/解密 |
| `00:34:09` | What I **do expect you to know** is that the running time of RSA... every component of RSA is O(log n) | RSA 运行时间 O(log n) |
| `00:34:20` | You **should know** this concept of digital signatures, how RSA permits that | 数字签名 |
| `00:34:25` | You **should know** fast exponentiation — we have already seen repeated squaring | 快速幂（重复平方） |
| `00:34:39` | You **should know** this theorem statement over here | RSA 相关定理 |

### Week 9: NP-Completeness

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:35:04` | You **should know** this fundamental difference between decision and optimization problems | 判定问题 vs 优化问题 |
| `00:35:12` | You **should be aware of** the fact that this whole theory of NP-completeness only deals with decision problems | NP=只针对判定问题 |
| `00:35:34` | You **should be familiar with** this concept of efficient certification or efficient verification | 高效验证 |
| `00:35:44` | You **need to know** this definition of NP: class of decision problems for which there is an efficient certifier, polynomial time certifier. You **should know** this | NP = 有多项式时间验证器 |
| `00:35:57` | You **should know** the full form of the acronym: non-deterministic polynomial time. And P is polynomial time | NP = 非确定性多项式时间 |
| `00:36:16` | You **should remember** the definitions of these problems at the very least: Hamiltonian cycle, subset sum | NP 问题定义 |
| `00:36:25` | P is a subset of NP. You **should know** these inequalities. Whether P=NP or not is not known. These are things that you **should be aware of** | P⊆NP, P=?NP 未知 |
| `00:36:35` | You **should know** the definition of Circuit SAT and the fact that it is NP | Circuit SAT |
| `00:36:40` | You **should know** the definition of co-NP as well | Co-NP 定义 |
| `00:36:44` | This picture [P/NP/co-NP relationship diagram] is a good thing to **keep in mind** | P/NP/Co-NP 关系图 |
| `00:36:47` | P is a subset of NP ∩ co-NP but we don't know if everything collapses. We don't know if NP = co-NP or not | NP ?= co-NP 未知 |
| `00:37:00` | You **should know** the notion of polynomial time reduction. What it means to do a reduction | 多项式归约概念 |
| `00:37:15` | But all you **need to know** is the definition | 只记定义 |
| `00:37:17` | This theorem **is important**. You **should know** this | 归约定理 |
| `00:37:20` | You **should at least be familiar with** this framework on how you can use the notion of reduction to prove that something is NP complete | 归约框架 |
| `00:37:48` | I only **expect you to remember** the theorem statement. You **should remember** that vertex cover is NP complete. You **should remember** that clique is NP complete. You **should know** their definitions | Vertex Cover, Clique = NP-Complete |
| `00:38:19` | All of these [CNF-SAT, 3SAT, 0-1 Integer Programming, 3Coloring] are things that you **should remember the definitions of** | SAT/3SAT/整数规划/3Coloring 定义 |

### Week 10: Approximation Algorithms (Joachim Spoerhase — 仅前半)

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:38:48` | Only the **first part is relevant** [of Joachim's lecture] | 仅前半在考纲 |
| `00:39:02` | You **should know** essentially this slide | Vertex Cover 近似 |
| `00:39:07` | You **should know** what is this greedy algorithm for vertex cover. How does it work? This one and the correctness guarantee which is: you always get a vertex cover that's within a factor 2 of your optimal | VC 2-近似算法+保证 |

---

## 不在考纲 — 原文证据

| 时间戳 | 原话 | 含义 |
|--------|------|------|
| `00:38:33` | The second one is **completely out of syllabus** — the one by John on randomized algorithms | **Week10 Lec 2 完全不考** |
| `00:06:28` | You can **safely skip** this if you're preparing for the exam — these seven quantities [Strassen details] | Strassen 7 个具体矩阵量 |
| `00:07:05` | You **don't need to know** the proof of the master method (×3) | Master Method 证明 |
| `00:07:21` | You **don't need to worry** about this clause over here [Case 3 regularity condition] | Case 3 正则条件 |
| `00:08:06` | Fast multiplication of integers is something that I **will not be asking in the exam**. So you don't need to revise this part... everything starting here until here | 快速整数乘法整段 |
| `00:09:20` | I **don't expect you to remember** this calculus page proof | 公平硬币微积分证明 |
| `00:11:09` | You **don't need to know** the deterministic algorithm. You don't even need to know the fact that you can do deterministic select in linear time | Deterministic Select |
| `00:13:01` | I **don't expect you to remember** this. Big O of N build heap procedure from the tutorial. That's **not necessary** | O(N) Build-Heap |
| `00:14:12` | You **don't need to know** the proof of correctness (×2) | 计数/基数排序正确性证明 |
| `00:16:55` | I **don't expect you to remember** this diagonalization | Fibonacci 对角化 |
| `00:17:57` | After that you can **skip the proof** | Fibonacci 闭式后半段 |
| `00:18:20` | I **don't expect you to remember** the proof of this | Fibonacci 位数证明 |
| `00:19:37` | You **don't need to remember** the proof of why fractional knapsack satisfies the greedy choice property | 分数背包贪心证明 |
| `00:20:49` | You **don't need to remember** this proof | 区间调度贪心证明 |
| `00:21:05` | You **don't need to remember** the correctness | 区间调度正确性证明 |
| `00:22:54` | You **don't need to know** these proofs. But you should know these theorem statements at least | 图论证明(只要定理陈述) |
| `00:23:24` | I am **not going to ask you questions about DFS BFS**. DFS and BFS was not in this module scope | DFS/BFS |
| `00:23:38` | You **don't need to know** anything about DFS and BFS | DFS/BFS (再次) |
| `00:24:07` | Dijkstra's algorithm — but this is **not going to be in this module scope** | Dijkstra |
| `00:25:19` | You **don't need to know** the correctness [of Bellman-Ford] | Bellman-Ford 正确性证明 |
| `00:26:35` | I **don't expect you to remember** any of these last three bullet points | All-pairs 最后三点 |
| `00:29:22` | You **don't need to remember** that... there are ways to improve Ford-Fulkerson | FF 改进方法 |
| `00:29:36` | So this [Maximum Matching using Max Flow] is **not going to be in the exam**. Starting from this slide until this slide over here | **最大匹配整段不考** |
| `00:33:02` | But this [Extended Euclidean Algorithm] is **not going to be in the exam**. Starting from the slide until this slide | **扩展欧几里得不考** |
| `00:33:59` | Since I **don't expect you to remember** extended Euclidean algorithm. I **don't expect you to know** how to come up with D if you don't know phi of n | 不需算 RSA 的 d |
| `00:35:27` | You can **ignore** a lot of these formalities | NP 形式化细节 |
| `00:37:09` | I am **not going to ask you** questions that involve doing any reduction | NP 归约操作 |
| `00:37:40` | I **will not ask you** to apply this framework. I am **not going to ask you** for example... | NP-Completeness 证明框架 |
| `00:38:00` | I **don't expect you to remember** this proof that I covered | NP 归约证明 |
| `00:39:28` | You **don't need to know** anything from here on | Steiner Tree 及之后 |
| `00:39:30` | This slide and anything after it is **not relevant** (×3) | 近似算法后半段 |
| `00:39:33` | I **don't expect you to know** the Steiner tree problem or the approximation algorithm that you can present for the metric version | Steiner Tree 不考 |
| `00:01:58` | This motivation [Week 1 intro] is **not really relevant for the exam** | Week 1 引入部分 |
| `00:02:40` | I'm **not going to ask you to prove** correctness of anything in the exam | 不考书面证明 |
| `00:04:16` | Well, when I say I'm not going to ask you to prove things formally, I will not expect written proofs. But there might be MCQ questions on things related to proofs | 不考书面证明(但可能有证明相关MCQ) |
| `00:06:00` | You **don't need to know** the linear time solution that was in the tutorial | Max Subarray 线性解法 |

---

## Canvas 公告补充

| 来源 | 原话 | 含义 |
|------|------|------|
| Announcement 2026-04-17 | Dr. Joachim Spoerhase will deliver a lecture on approximation algorithms. **The material covered in this lecture is in the syllabus for the final exam.** | Week10 Lec 1 在考纲 |
| Announcement 2026-04-17 | Dr. John Sylvester will deliver a lecture on randomised algorithms. **The material covered in this lecture is not in the syllabus for the final exam.** | Week10 Lec 2 不在考纲 |
| Announcement 2026-04-23 | In the 28th April lecture slot, I will do a revision lecture: here I will quickly go over the list of contents we covered in this course and **point out certain topics that might be relevant/irrelevant for the final exam** | 本 Revision Lecture 即为考纲依据 |
| Announcement 2026-04-23 | In the 1st May lecture slot, I will do a "fun" lecture on Complexity of Algorithms for the Rubik's cube. This is **completely optional to attend, and out of syllabus for the exam**. I will not be recording this lecture. | Rubik's Cube 不考 |
| Announcement 2026-04-24 | Some **mock questions and past papers** have now been uploaded to the Modules section under Revision materials | Mock Questions + 真题 = 题型参考 |

---

## 特别强调

1. **`proofs.pdf`** — Nikhil 点名 2 次 (`00:22:10` `00:22:44`)，图的性质会出 MCQ
2. **RSA** — `00:33:38` "You can expect a few questions from here"（重点出题区）
3. **Bellman-Ford** — `00:24:37` `00:25:00` 两次强调 Canvas 上的执行示例 PDF
4. **Ford-Fulkerson** — `00:27:32` 明确说了会给你一个流网络让你手动执行
5. **Master Method** — `00:07:33` "definitely expect me to ask... directly use the master method"
6. **One-Time Pad** — `00:30:48` "definitely important"
7. **Repeated Squaring / Fast Exponentiation** — `00:34:25` 贯穿课程始终
8. **不考证明作但可能有证明相关 MCQ** — `00:04:26` 明确区分

















