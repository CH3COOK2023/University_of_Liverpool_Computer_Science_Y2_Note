  Q: TCP 和 UDP 的区别？
  A: TCP 面向连接、可靠、慢；UDP 无连接、不可靠、快。DNS 用 UDP，HTTP 用 TCP。

  Q: Socket 是什么？
  A: 网络上两个程序之间的双向通信端点，绑定端口号。不是端口本身、不是进程、不是线程。

  Q: RPC 是什么？
  A: 远程过程调用。让调远程函数像调本地函数一样，靠 client stub ↔ server stub 打包拆包透明传输。

  Q: RMI 是什么？
  A: Java 的 RPC——让一个 JVM 里的对象调另一个 JVM 里的对象的方法。

  Q: RPC 发消息时 server 离线会怎样？
  A: 消息丢失。RPC = 瞬时通信，双方必须同时在线。

  Q: MOM 发消息时 server 离线会怎样？
  A: 消息存在消息队列里，等 server 上线后投递。MOM = 持久异步通信。

  Q: Message Broker 干什么？
  A: 把不同系统的消息格式互相转换（老系统 ↔ 新系统集成）。

  Q: 四种通信组合？
  A: 瞬时×异步(双方在线发完不管)、瞬时×同步(双方在线等回复)、持久×异步(MOM)、持久×同步(持久等回复)

  Q: Jitter 怎么缓解？
  A: ① Buffering 缓冲 ② 交错打包（连续帧分到不同包，丢一个不连续卡）

  Q: Stateless vs Stateful？
  A: Stateless 不记客户端状态(如 Web)，Stateful 维护连接状态(如 FTP、文件服务器)。

  Q: Desktop Virtualisation？
  A: 在 Host OS 里把一个完整的 Guest OS 当作进程跑（如 VirtualBox）。

  Q: DNS 五个功能？
  A: 域名→IP、邮件主机(MX)、反向解析(IP→域名)、主机信息(硬件/OS)、知名服务列表。

  Q: Iterative vs Recursive DNS？
  A: Iterative = client 逐个问；Recursive = server 替 client 爬链。跨国用 Recursive 省长途。

  Q: Clock Skew？
  A: 晶振物理误差累积，一次校准不够，必须持续同步。

  Q: Cristian 算法？
  A: 连 UTC 的时间服务器，估计 = T_B + (T₁−T₀)/2。不能往回拨（时间不能倒退）。

  Q: Berkeley 算法？
  A: Master 轮询 slaves → 算平均值 → 广播"你需要调快/慢多少"。

  Q: Lamport 时钟？
  A: 不靠物理时间。每事件 +1，收消息：max(自己,消息戳)+1。保证 happened-before 因果。

  Q: 事务两大功能？
  A: ① 保护共享资源防并发冲突 ② 一次原子化操作多个数据（半成功可整体回滚）。

  Q: ACID 各是什么？
  A: Atomic(全或无)、Consistent(不变量)、Isolated(串行化等价)、Durable(提交即永存)。

  Q: Nested 事务缺点？
  A: 子事务提交后父事务 abort → 违反 Atomicity（已提交的东西要撤回来）。

  Q: 回滚事务的两个方法？
  A: Private Workspace(commit 前读写都在私有副本) / Writeahead Log(先写日志再改文件，abort 读日志恢复)。

  Q: 死锁怎么产生？
  A: T1 锁 X 等 Y，T2 锁 Y 等 X → 互相等。

  Q: 互斥四段？
  A: Entry(抢锁) → Critical(临界区) → Exit(放锁) → Remainder(其他)。互斥算法只管 Entry+Exit。

  Q: Centralised vs Distributed 互斥？
  A: Centralised = 单 coordinator（简单但单点故障）；Distributed = 组内协商（复杂但无单点）。

  Q: 为什么复制？
  A: 可靠性(一台挂了另一台顶上) + 性能(数据靠近用户/负载分担)。

  Q: Sequential Consistency？
  A: 所有进程看到写操作的顺序完全相同——存在一个全局统一顺序解释所有人的 read。

  Q: Push vs Pull？
  A: Push = server 主动推(高一致性)；Pull = client 按需拉(如浏览器缓存)；折中 = Leases。

  Q: 故障三种频率？
  A: Transient(一次消失，鸟飞过微波)、Intermittent(反复出现，接触不良)、Permanent(持续到更换，烧芯片)。

  Q: Crash vs Byzantine？
  A: Crash = 正常干活然后停；Byzantine = 任意异常甚至恶意（更难处理）。

  Q: 冗余三类型？
  A: Information(校验位/Hamming码) 、Time(重做/事务retry) 、Physical(加副本/replication)。

  Q: Forward vs Backward Recovery？
  A: Backward = checkpoint 回滚(有快照开销) ；Forward = 直接从错误修到正确(需穷举故障)。

  Q: 对称 vs 非对称加密？
  A: 对称 = 同钥加解密；非对称(Public-key) = 公钥加密私钥解密。RSA = 非对称，依赖大素数分解。

---
  Algorithms 快速 Q&A

  Q: 分布式算法三个性能度量？
  A: Time complexity(轮数)、Communication complexity(消息数)、Space complexity(内存)

  Q: Correctness Proof 目的？
  A: 形式化证明算法做了预期的事情（不是证复杂度、不是证不可能）

  Q: 给定生成树广播 → 时间？通信？
  A: 时间 = 树深度 d（≤ n-1），通信 = n-1 条消息

  Q: 无树+有根广播+构建 BFS → 时间？通信？
  A: 时间 = O(D)（D=网络直径），通信 = O(m)（m=边数）

  Q: 无根建树的两种策略？
  A: Everything(全部转发 n 棵树) vs Maximum Prevails(只留 max ID 的树)

  Q: LCR 算法做什么？
  A: 有向环选 leader。每节点传当前见过的 max ID，被更大的 ID 淘汰。

  Q: LCR 时间？通信？
  A: 时间 = n+1，通信最坏 = Θ(n²)(逆时针递增ID)、最好 = 2n-1(顺时针递增ID)

  Q: 环上全同处理器能选 leader 吗？
  A: 不能——归纳法证：每轮后所有人状态永远相同，无法分出 leader。

  Q: FloodMax 干什么？
  A: 知道直径 D，每轮全员广播见过的最大 ID，D 轮后持有最大 ID 的当选。

  Q: FloodMax 时间？通信？
  A: 时间 = D+1，通信 = D·m（每轮每条边都传消息）

  Q: OptFloodMax 改进了什么？
  A: 只在收到更大 ID 时才发送，不是每轮盲发。但最坏情况未改善（仍是 Θ(n³)）。

  Q: FloodSet 解决什么问题？
  A: 完全图的共识问题——≤f 个 crash，最终所有人输出同一个值。

  Q: FloodSet 怎么跑？
  A: 每节点维护 W={初始值}，跑 f+1 轮，每轮广播+合并。最终 |W|=1→选它，>1→选 s₀。

  Q: FloodSet 时间？通信？
  A: 时间 = f+1 轮，通信 = O((f+1)n²)

  Q: 为什么必须 f+1 轮？
  A: 鸽巢原理——最多 f 个故障，f+1 轮保证至少有 1 轮没人挂，那轮大家 W 同步。

  Q: Coordinated Attack 结论？
  A: 消息可能丢失时 consensus 不可能——即使只有两个进程，也达不成一致。

  Q: Agreement Problem 三条件？
  A: Agreement(输出同一个值)、Validity(全同输入→必须选它)、Termination(最终决定)
