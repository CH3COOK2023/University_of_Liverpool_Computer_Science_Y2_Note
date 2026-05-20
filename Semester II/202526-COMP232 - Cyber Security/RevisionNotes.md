# COMP232 网络安全 — 复习笔记 (Revision Notes)

> 依据讲师复习指南整理。考试：5 选 4，每题 25 分，2 小时。

---

## Part 1: 身份认证与安全架构 (Authentication & Security Architecture)

### 密码 vs 令牌 vs 生物识别

| 类型 (Type) | 基础 (Basis) | 优点 (Pros) | 缺点 (Cons) |
|-------------|-------------|-------------|-------------|
| **密码 (Password)** | 你知道什么 (What you **KNOW**) | 无需硬件 | 短了可猜，长了难记 |
| **令牌 (Token)** | 你拥有什么 (What you **HAVE**) | 物理实体 | 丢失失效；可被复制/伪造 |
| **生物识别 (Biometrics)** | 你是什么 (What you **ARE**) | 不可分享/复制/丢失 | 假阳性/假阴性；硬件贵 |

- 令牌类型：**安全存储设备** (Secure Storage, 如银行卡) vs **主动设备** (Active Device, 生成 OTP: 时间同步 time-synchronous / 挑战-应答 challenge-response)
- **多因子认证 (Multi-factor Authentication, MFA)**：Token + PIN，或 Password + SMS

### 安全攻击 (Security Attacks)

| 分类 | 定义 | 应对思路 |
|------|------|---------|
| **被动攻击 (Passive Attack)** | 截获信息，不影响系统 | 难检测 → **预防** (Prevent) 为主，如加密 |
| **主动攻击 (Active Attack)** | 篡改系统/资源 | 会留痕迹 → **检测** (Detect) 并恢复 |

**四种攻击类型与 CIA 对应**：

| 攻击 | 破坏的属性 |
|------|-----------|
| **中断 (Interruption)** | 可用性 (Availability) |
| **拦截 (Interception)** | 机密性 (Confidentiality) —— 被动攻击 |
| **篡改 (Modification)** | 完整性 (Integrity) |
| **伪造 (Fabrication)** | 真实性 (Authenticity) |

**暴力破解 (Brute-force Attack)**：系统性穷举密钥。对策：密钥更长 → 复杂度指数增长 (如 AES-256 vs AES-128)。

### 安全服务 (Security Services, X.800)

- **认证 (Authentication)** — 验证身份
- **访问控制 (Access Control)** — 限制资源访问
- **数据机密性 (Data Confidentiality)** — 防止未授权泄露
- **数据完整性 (Data Integrity)** — 检测篡改
- **不可抵赖性 (Non-repudiation)** — 不能否认行为
- **可用性 (Availability)** — 需时可用

**安全机制 (Security Mechanisms)**：加密 (Encipherment)、数字签名 (Digital Signature)、访问控制、数据完整性机制、认证交换 (Authentication Exchange)、流量填充 (Traffic Padding)、路由控制 (Routing Control)、公证 (Notarisation)

---

## Part 2: 密码学 (Cryptography)

### 对称加密 (Symmetric Encryption)

加解密用**同一密钥**。核心原则：**安全性依赖于密钥保密，而非算法保密**。

```
明文 (Plaintext) → [加密 Encrypt] → 密文 (Ciphertext) → [解密 Decrypt] → 明文
                        ↑                                    ↑
                    密钥 (Secret Key)  ================   密钥
```

#### DES (Data Encryption Standard)

- 分块 (Block)：64 位；密钥 (Key)：**56 位**；轮数 (Rounds)：16
- 基于 **Feistel 网络**结构
- 问题：56 位密钥太短 → 90 年代中已可暴力破解

#### 3DES

- DES 执行 **3 次**，用 **3 把不同密钥**：加密 → 解密 → 加密 (EDE)
- 更安全，但更慢

#### AES (Advanced Encryption Standard)

- 分块：128 位；密钥：**128 / 192 / 256** 位
- 轮数 = 密钥长度/32 + 6：10 / 12 / 14
- 当前标准

### 分组密码模式 (Block Cipher Modes)

| 模式 | 原理 | 关键特性 |
|------|------|---------|
| **ECB** (电子密码本 Electronic Codebook) | 每块独立加密 | 相同明文 → 相同密文 (**不安全**，模式可见) |
| **CBC** (密码块链 Cipher Block Chaining) | $C_i = E_K(C_{i-1} \oplus P_i)$ | 相同明文 → 不同密文；需要 **IV** (初始化向量) |
| **CFB** (密码反馈 Cipher Feedback) | 将块加密转为**流加密 (Stream Cipher)** | 参数 $s$ = 传输单元大小；需要 IV |

**ECB 主要缺点**：明文模式暴露。修复 → 用 CBC。

### 公钥加密 (Public Key Encryption / Asymmetric Encryption)

- **加密密钥 ≠ 解密密钥**
- 公钥 (Public Key) 加密；**私钥 (Private Key)** 解密

#### RSA

- 安全性基于：**大整数分解的困难性** (Difficulty of Factoring Large Integers)
- 密钥：公钥 = $(e, n)$，私钥 = $(d, n)$
- 加密：$C = m^e \bmod n$；解密：$m = C^d \bmod n$
- **乘法部分同态**：$E(x) \times E(y) = E(xy)$
- 用于**混合加密系统 (Hybrid Cryptosystem)**：RSA 加密 AES 会话密钥 → AES 加密数据（因为 AES 比 RSA 快得多）

**其他公钥算法**：ECDSA、DSA、DH

#### Diffie-Hellman (DH) 密钥交换

- 两方在**不安全信道**上协商出**共享密钥**
- 安全性基于：**离散对数问题 (Discrete Logarithm Problem)** 的困难性
- 公开参数：大质数 $P$，生成元 $G$
- Alice：私钥 $a$，发送 $A = G^a \bmod P$
- Bob：私钥 $b$，发送 $B = G^b \bmod P$
- 共享密钥：$S = B^a \bmod P = A^b \bmod P$
- 可扩展到 **$n$ 方**：需要 $n-1$ 轮交换

### 哈希函数与消息认证

#### 哈希函数 (Hash Function)

- 任意长输入 → **固定长**输出（摘要 Digest / 指纹 Fingerprint）
- 性质：
  - **单向性 (One-way / Preimage Resistance)**：给 $h$，求不出 $m$ 使得 $H(m)=h$
  - **弱碰撞抵抗 (Weak Collision Resistance)**：给 $m$，找不到 $m' \neq m$ 使 $H(m)=H(m')$
  - **强碰撞抵抗 (Strong Collision Resistance)**：找不到任意 $m \neq m'$ 使 $H(m)=H(m')$

#### 生日攻击 (Birthday Attack)

基于生日悖论 (Birthday Paradox)：23 人 → 50% 概率两人同生日。
应用于哈希：找到**任意一个碰撞**只需 $\approx 2^{n/2}$ 次尝试，不是 $2^n$。
SHA-1 (160 位) 在 $\approx 2^{80}$ 即被破解 → **必须用 SHA-256**。

Google 2017 年展示了两个不同内容但 SHA-1 相同的 PDF。

#### 数字签名 (Digital Signature)

1. Alice 计算 $H(m)$ → 用**自己的私钥**加密 → 签名
2. Bob 用 Alice 的公钥解密 → 与他自己算的 $H(m)$ 比较
3. 匹配 → 同时保证**真实性 (Authenticity)** + **完整性 (Integrity)**

#### MAC (Message Authentication Code, 消息认证码)

- $MAC = F(K, M)$ — 对称密钥 + 消息
- 验证完整性 + 真实性（**不提供不可抵赖性**——双方共享密钥）
- 比加密整个文件**快得多**
- **HMAC-SHA256**：行业标准

| 对比 | SHA-1 | SHA-256 |
|------|-------|---------|
| 输出长度 | 160 位 | 256 位 |
| 安全性 | 已被破解 (2017) | 极高 |
| 现状 | 淘汰 | **主流推荐** |

### 量子密码学 (Quantum Cryptography)

#### BB84 量子密钥分发 (QKD)

核心：**你无法在不干扰系统的情况下测量量子系统** (you cannot measure without disturbing)。

- 两个基：**+** (直线基 Rectilinear: →, ↑) 和 **×** (对角基 Diagonal: ↗, ↘)
- Alice 随机选偏振态发光子；Bob 随机选基测量
- 事后比对：保留基匹配的位置 (~50%) → 形成原始密钥 (Raw Key)
- **Eve 窃听** → 每光子必须猜基 (50% 对) → 猜错会改变光子状态 → 引入约 **25% 错误率** → Alice & Bob 牺牲少量比特公开对比 → 发现高错误率 → 检测到窃听

**BB84 为何防御被动攻击**：被动攻击 = 只偷看不干预。经典网络可静默复制信号。量子力学中"看"= 测量 = 改变量子态 → 被动攻击变成可检测的主动干扰。

#### QRNG (量子随机数生成 Quantum Random Number Generator)

- 利用量子测量不可预测性 → **真随机数 (Truly Random)**
- vs 伪随机生成器 PRNG：确定性算法 → 知道种子 (Seed) 即可预测
- 优点：根本性随机 (Fundamental Randomness)；部分支持 self-testing
- 缺点：更复杂、更昂贵

### 高级密码学

#### 全同态加密 (Fully Homomorphic Encryption, FHE)

- 可直接在密文上计算：$Enc(x) \cdot Enc(y) = Enc(xy)$ 且 $Enc(x) + Enc(y) = Enc(x+y)$
- 能执行**任意计算 (Arbitrary Computation)**
- 2009 年 IBM 首次实现，基于**格密码学 (Lattice Cryptography)**
- **部分同态**：如 RSA (乘法) / Paillier (加法)。能同时做 XOR + AND 即可模拟任意电路。
- 问题：比明文慢 **1,000 ~ 1,000,000 倍**；密钥体积大
- 安全性：基于 LWE (Learning With Errors) → **抗量子**
- 应用：云上加密计算、加密数据上训练 ML

#### CryptDB

- **洋葱分层 SQL 感知加密 (Onion-layered SQL-Aware Encryption)**
- 数据被多层不同加密包裹 → 根据查询需求**动态剥皮**
- 四种洋葱：Search (文本搜索)、Add (加法同态)、Eq (确定性加密, 等值查询)、Ord (保序加密, 排序/范围)
- 剥越多 → 功能越强，但泄露越多

#### 零知识证明 (Zero-Knowledge Proof, ZKP)

- **不泄露秘密本身**，证明你拥有它
- 三大性质：**完备性 (Completeness)**、**可靠性 (Soundness)**、**零知识性 (Zero-Knowledge(ness))**
- 经典例子：红绿卡片实验、找沃尔多 (Where's Waldo?)、图 3-着色问题 (Graph 3-colorability)、图非同构 (Graph Non-isomorphism)
- **zk-SNARK**：简洁、非交互式；用于区块链

#### 安全多方计算 (Secure Multi-Party Computation, SMPC)

- 多方计算 $F(x, y)$ 但互不暴露输入 $x, y$
- **姚氏百万富翁问题 (Yao's Millionaire Problem, 1982)**：谁更富有但不透露资产？
- 实现方式：**混淆电路 (Garbled Circuits)** + **不经意传输 (Oblivious Transfer, OT)**

#### 隐写术 (Steganography)

- **隐藏通信存在性**（Level 3 保护）
- 载体 (Cover Object)：文本、图像、音频、网络数据包
- **LSB (最低有效位 Least Significant Bit)**：修改像素最低位 → 人眼不可察觉。高容量但稳健性差。
- **Jsteg**：变换域 (DCT 系数)，更隐蔽
- 数字水印 (Digital Watermarking)：额外要求**鲁棒性 (Robustness)**
- 适用任何含**冗余 (Redundancy)** 的数据对象

---

## Part 3: 安全协议与形式化分析 (Security Protocols & Formal Analysis)

### Needham-Schroeder 协议

**共享密钥版**：A 和 B 通过认证服务器 S 获得共享会话密钥 $K_{AB}$。

**公钥版**（原始有漏洞）：

```
1. A → B: {A, N_A}_{PK(B)}
2. B → A: {N_A, N_B}_{PK(A)}
3. A → B: {N_B}_{PK(B)}
```

**Lowe 攻击 (1995)**：入侵者 I (已腐化的合法参与者) 利用并行会话——A 与 I 通信，I 冒充 A 与 B 通信。通过**模型检测**发现——协议发布 **17 年后**！

```
1a. A → I: {A, N_A}_{PK(I)}       A 想和 I 通信
1b. I → B: {A, N_A}_{PK(B)}       I 冒充 A 联系 B
2b. B → I: {N_A, N_B}_{PK(A)}     B 回复
2a. I → A: {N_A, N_B}_{PK(A)}     I 转发给 A
3a. A → I: {N_B}_{PK(I)}          A 解密后返回 N_B
3b. I → B: {N_B}_{PK(B)}          B 以为在和 A 通信！
```

**Lowe 修复**：Msg 2 加入 B 的身份 → $\{B, N_A, N_B\}_{PK(A)}$。A 收到后发现 B 的身份与自己发 nonce 给 I 不一致 → 检测中止。

### 协议形式化分析方法

**为什么需要？** 协议逐步检查看似正确，但在攻击者跨会话利用下仍有漏洞。人类专家会遗漏。

#### 模型检测 (Model Checking)

- **穷举**所有可能状态 → 看能否到达"坏状态"
- 计算机模拟攻击者：拦截、重放、用已知密钥生成新消息
- 发现 Lowe 攻击——这是人类肉眼无法做到的
- **优点**：全自动，输出攻击路径。**缺点**：限于有限状态

#### 定理证明 (Theorem Proving)

- 数学证明不存在攻击 ($F_s \rightarrow P$)
- **优点**：可处理无限状态。**缺点**：一般不可判定，常需人工交互
- 工具：Isabelle、PVS、TAPS

#### ProVerif

- 基于逻辑编程；协议 = Horn 子句；检查攻击目标是否可达
- 可处理**无界会话数**

### 考试：协议分析 5 条清单 (Protocol Flaw Analysis)

拿到协议逐条对照：

| # | 检查点 | 关键问题 |
|---|--------|---------|
| 1 | **加密对象对不对？** | 用自己公钥加密 → 只有自己能解密，对方收了个寂寞 |
| 2 | **有没有身份信息？** | 没发送者/接收者 ID → 可冒充 |
| 3 | **有没有防重放？** | 没 nonce / 时间戳 → 旧消息可重放 |
| 4 | **nonce 是否被正确回显？** | 最后一步有没有把初始 nonce 正确返回？ |
| 5 | **会话是否绑定？** | nonce 是否绑定当前会话？能否跨会话利用？ |

---

## Part 4: 监控、入侵检测与防火墙 (Monitoring, IDS & Firewalls)

### 入侵检测方法两大类别

| 方法 | 原理 | 优点 | 缺点 |
|------|------|------|------|
| **基于特征 (Signature-based / Knowledge-based)** | 匹配已知攻击模式 | 低误报，实现简单 | **不能检测未知攻击** |
| **基于异常 (Anomaly-based / Behaviour-based)** | 学习正常行为 → 标记偏差 | 能检测新型攻击 | 高误报，需持续更新基准 |

### 7 种入侵检测技术

1. **统计分析 (Statistical Analysis)**：多变量指标，超阈值报警
2. **神经网络/机器学习 (Neural Networks / ML)**：学习非线性行为模式
3. **基于规则/专家系统 (Rule-based / Expert Systems)**：if-then-else + 推理引擎
4. **基于特征码 (Signature-based)**：字符串匹配 (Snort, Suricata, Zeek)
5. **状态转换分析 (State-Transition Analysis)**：攻击 = 状态转换序列
6. **用户意图识别 (User Intention Identification)**：匹配预期任务序列
7. **计算机免疫学 (Computer Immunology)**：正常系统调用短序列建模，类比人体免疫
8. **数据挖掘 (Data Mining)**：从海量审计数据中提取未知模式，适合**离线批处理**，不适合流式实时检测。生成决策树、关联规则判定攻击（如 SYN 洪水）

### 防火墙 (Firewall)

所有**内→外**和**外→内**流量必须经过防火墙。

#### 三种类型

| 类型 | OSI 层 | 原理 | 优点 | 缺点 |
|------|--------|------|------|------|
| **包过滤 (Packet Filter)** | 网络层 (IP) | 逐包匹配规则 | 透明、快 | 无上层过滤 |
| **电路网关 (Circuit Gateway)** | 传输层 (TCP) | 中继 TCP 连接 | 隐藏内部网络 | 不过滤单个包 |
| **应用网关 (Application Gateway)** | 应用层 | 每应用单独代理 | 安全性高 | 性能影响大 |

**过滤策略**：丢弃策略 Discard (默认拒绝, 保守) vs 转发策略 Forward (默认允许, 便捷但风险高)。

**下一代防火墙需求**：识别应用而非端口；识别用户而非 IP；实时深度包检测；数千兆吞吐。

**防火墙局限性**：不能防内部威胁；不能防绕过攻击；不能防病毒文件传输。

### 恶意程序 (Malicious Programs) — 了解

| 类型 | 核心特征 |
|------|---------|
| **陷门/后门 (Trapdoor/Backdoor)** | 绕过正常认证的秘密入口 |
| **逻辑炸弹 (Logic Bomb)** | 满足条件 (日期/文件/用户) 引爆 |
| **特洛伊木马 (Trojan Horse)** | 伪装成有用程序 |
| **僵尸/僵尸网络 (Zombie/Botnet)** | 远程控制发动 DDoS/垃圾邮件 |
| **病毒 (Virus)** | 感染程序；4 阶段：休眠 → 传播 → 触发 → 执行 |
| **蠕虫 (Worm)** | 网络自主传播；Morris (1988)、Slammer (2003, 10 分钟感染全球) |

**病毒检测不可判定性**：F. Cohen 证明病毒检测问题在一般情况下**不可判定 (Undecidable)**。

**反病毒四代**：简单扫描器 → 启发式扫描器 → 活动陷阱 → 综合防护包

**蠕虫检测**：基于阈值 (Threshold, 流量突发) / 基于趋势 (Trend, 指数传播特征)

**SQL 注入 (SQL Injection)**：用户输入被当 SQL 执行。防御：参数化查询 (Parameterized Query)。

**XSS**：用户输入含 `<script>` 被浏览器执行。防御：清理输入 (Sanitize Input)。

---

## Part 5: 隐私、匿名与法律 (Privacy, Anonymity & Legal)

### 匿名通信系统

| 系统 | 原理 | 特点 |
|------|------|------|
| **匿名器 (Anonymizer)** | 中央代理，剥离身份信息 | 单点信任/故障 |
| **混合网络 (Mix Network)** | 链式节点，每个解密并重排序 | 更强隐私，但更贵 |
| **人群 (Crowds)** | 群组成员随机转发 | 无单点攻击；高效 (无需加解密) |
| **Tor (洋葱路由)** | 多层加密，3 跳电路 | 入口知道谁，出口知道什么，无人同时知道两者 |

**Tor 工作机制**：结合 Crowds + Mix-networks。公钥协商对称密钥 → 对称加密通信 (效率)。**双向虚拟电路** (Bidirectional Virtual Circuits)。

### 信息隐私 (Information Privacy)

| 概念 | 说明 |
|------|------|
| **个人可识别信息 (PII)** | 可推导出个人身份 |
| **匿名信息 (Anonymized Info)** | 无法推断身份 (如仅年龄) |
| **汇总信息 (Aggregated Info)** | 从大量个体统计汇总 |
| ⚠️ **汇总 + 匿名 → 可能重新识别** | 例：马萨诸塞州州长病历泄露事件 |

### OSINT (开源情报 Open-Source Intelligence)

从公开来源收集分析信息：新闻、人口普查数据、行业期刊、公开会议等。

### 法律问题

**密码学监管**：US 出口管制历程 (40 位限制 → Clipper Chip → 2000 年放开)。Wassenaar Arrangement。

**OECD 隐私八原则 (1980)**：
1. 收集限制 (Collection Limitation)
2. 数据质量 (Data Quality)
3. 目的明确 (Purpose Specification)
4. 使用限制 (Use Limitation)
5. 安全保障 (Security Safeguards)
6. 公开性 (Openness)
7. 个人参与 (Individual Participation)
8. 问责制 (Accountability)

不具法律效力；30 个成员国采用为指导。

**GDPR (2018)**：欧盟通用数据保护条例。UK 对应：Data Protection Act 2018。

**DMCA (US 1998)**：将规避版权保护的技术定为犯罪。争议：Dmitry Sklyarov 案 (2001)——版权保护 vs 密码分析研究合法性？

---

## 真题考点分布 (Past Paper Questions)

### COMP232_2025 (5 选 4)

| Q# | 考点 | 分值 |
|:--:|------|:----:|
| 1(a) | 包过滤防火墙 (Packet Filtering Firewall) | 6 |
| 1(b) | 弱碰撞抵抗 (Weak Collision Resistance) | 6 |
| 1(c) | 三方 DH 密钥交换 | 13 |
| 2(a) | BB84 防御被动攻击 | 10 |
| 2(b) | RSA 部分同态性 | 7 |
| 2(c) | 认证技术三类 | 8 |
| 3(a) | 公钥实现数字签名 | 6 |
| 3(b) | 信息保护四个等级 | 4 |
| 3(c) | 混合网络 (Mix-network) | 7 |
| 3(d) | 安全多方计算 (SMPC) + 应用 | 8 |
| 4(a) | 迭代次数与暴力破解 | 7 |
| 4(b) | 入侵检测两大类别 | 5 |
| **4(c)** | **协议漏洞分析与修复** | **9** |
| 4(d) | 聚集信息 vs 匿名信息 | 4 |
| 5(a) | ECB 模式缺点与修复 | 7 |
| 5(b) | Tor 为何同时用对称和公钥 | 8 |
| 5(c) | CryptDB 多层加密 | 10 |

### COMP232_MOCK (5 选 4)

| Q# | 考点 | 分值 |
|:--:|------|:----:|
| 1(a) | 算法保密的安全性 | 5 |
| 1(b) | 哈希碰撞改进攻击 (生日攻击) | 10 |
| 1(c) | DH + 离散对数攻击 | 10 |
| 2(a) | 被动攻击 + 量子密码学影响 | 8 |
| 2(b) | RSA 部分同态 | 7 |
| 2(c) | 错误哈希算法分析 | 10 |
| 3(a) | QRNG 优缺点 | 5 |
| 3(b) | RSA 盲签名 + 电子支付/投票 | 10 |
| 3(c) | 零知识证明 (ZKP) + 应用 | 10 |
| 4(a) | RSA 公因子攻击 | 8 |
| 4(b) | 入侵检测两大类别 | 7 |
| **4(c)** | **协议漏洞分析与修复** | **10** |
| 5(a) | CBC 模式 | 7 |
| 5(b) | 多因子认证 (MFA) | 8 |
| 5(c) | 全同态加密 (FHE) + 应用 | 10 |
