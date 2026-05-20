# COMP232 2025 Exam — Model Answers (中英双语)

> 基于 PPT 和笔记。每题原文 + 翻译 + 答案。与 Mock 重复的题标注"同 Mock"。

---

## Q1(a) — 包过滤防火墙 (6 marks)

> **EN**: *Describe the packet-filtering technique used in firewalls. What are advantages and disadvantages of this technique?*

> **CN**: 描述防火墙中的包过滤技术及其优缺点。

---

**Packet-Filtering Router (包过滤路由器)**：工作在网络层 (Network Layer)。对每个 IP 包应用规则，匹配源/目的 IP、端口号、协议字段 → 转发 (Forward) 或丢弃 (Discard)。

| Advantages (优点) | Disadvantages (缺点) |
|------|------|
| **Transparent to users** (对用户透明) | **Lack of upper-layer functionality** (缺乏上层功能) |
| **Simple & fast** (简单、速度快) | Cannot support advanced user authentication (不支持高级认证) |
| | Cannot block specific application commands — either the whole app is allowed or denied |

**Two policies (两种策略)**:

| Policy | Logic | Safety |
|--------|-------|--------|
| **Discard (丢弃)** | What is not explicitly permitted is prohibited | Conservative, more secure |
| **Forward (转发)** | What is not explicitly prohibited is permitted | Convenient, less secure |

> PPT: COMP232-15-Firewalls-26

---

## Q1(b) — 弱碰撞抵抗 (6 marks)

> **EN**: *What is a weak collision resistance property of a hash function and what role does it play in applications of hash functions?*

> **CN**: 什么是哈希函数的弱碰撞抵抗属性？它在应用中起什么作用？

---

**Weak Collision Resistance (弱碰撞抵抗)**：

Given a specific message $x$, it is computationally infeasible to find another message $x' \neq x$ such that $H(x) = H(x')$.

给定特定 $x$，找不到另一个 $x' \neq x$ 使 $H(x) = H(x')$。

| Property | Definition | Attack Complexity |
|----------|-----------|:---:|
| **Weak Collision Resistance** | Given $x$, cannot find $x' \neq x$ with $H(x)=H(x')$ | $\approx 2^n$ |
| **Strong Collision Resistance** | Cannot find any pair $(x, x')$ with $H(x)=H(x')$ | $\approx 2^{n/2}$ (Birthday Attack) |
| **One-way (Preimage Resistance)** | Given $h$, cannot find $x$ with $H(x)=h$ | $\approx 2^n$ |

**Role in applications (作用)**：

In **digital signatures**: if Alice signs $H(m)$, an attacker who intercepts $(m, \text{Sign}(H(m)))$ cannot forge a different $m'$ with the same hash. Weak collision resistance protects message integrity — the attacker can't swap the message without changing the hash.

→ 数字签名中：攻击者截获 $(m, \text{Sign}(H(m)))$ 后无法伪造 $m'$ 使哈希相同，签名保护了消息完整性。

> PPT: COMP232-7-9-Algorithms-26, Page 35-36

---

## Q1(c) — 三方 DH 密钥交换 (13 marks)

> **EN**: *Propose a variant of the Diffie-Hellman (DH) Key Exchange algorithm which would allow to exchange secret keys between three participants. How do you argue it is secure algorithm?*

> **CN**: 提出三方 DH 密钥交换变体，并论证其安全性。

---

**3-party DH Extension (三方 DH, 2 轮)**：

| Round | A → B | B → C | C → A |
|:---:|------|------|------|
| **1** | $G^a$ | $G^b$ | $G^c$ |
| **2** | $G^{ca}$ | $G^{ab}$ | $G^{bc}$ |

**Final key computation**:

| Party | Receives (round 2) | Powers with own private key | Shared Key |
|-------|---------------------|-----------------------------|:---------:|
| A | $G^{bc}$ | $(G^{bc})^a$ | $G^{abc}$ |
| B | $G^{ca}$ | $(G^{ca})^b$ | $G^{abc}$ |
| C | $G^{ab}$ | $(G^{ab})^c$ | $G^{abc}$ |

General rule: $n$ parties need **$n-1$ rounds** of round-robin exchange.

**Security argument (安全论证)**：

| Point | Explanation |
|-------|------------|
| **Based on DLP** | Same as 2-party DH. Each party only sees intermediate values ($G^a, G^b, G^c, G^{ab}, G^{bc}, G^{ca}$). Computing any private key from these requires solving the **Discrete Logarithm Problem**. |
| **Forward computation** | $G^a \bmod P$ is fast ($O(\log a)$ via modular exponentiation) |
| **Reverse computation** | Given $G^a \bmod P$, finding $a$ is computationally infeasible when $P$ is a sufficiently large prime (e.g. 2048 bits) |
| **No extra leakage** | Each round adds one exponent layer. Attacker sees combinations but cannot extract individual keys without DLP. |

---

## Q2(a) — BB84 防御被动攻击 (10 marks)

> **EN**: *How is protection against passive attacks guaranteed in BB84 quantum key exchange algorithm?*

> **CN**: BB84 量子密钥交换算法如何保证防御被动攻击？

---

**Passive Attack (被动攻击)** = eavesdrop only, no system alteration. In classical networks: can silently copy signals — **undetectable**.

Classical defense: **prevention** (encryption). You can't detect the eavesdropper, you only hope they can't read it.

**BB84 changes this fundamentally**:

| Step | What happens |
|------|-------------|
| Alice sends photons | Random polarization in random bases (+ or ×) |
| Eve intercepts | Must **measure** each photon to read it → must guess basis (50% correct) |
| Eve guesses wrong | Measurement **collapses** the photon to a random state → original state destroyed → Eve re-transmits a "corrupted" photon |
| Alice & Bob compare | Discard mismatched bases (~50% retained) → raw key |
| Alice & Bob sacrifice check bits | Compare a random subset of raw key publicly |
| Result | No Eve: error rate ≈ 0%. **Eve present: error rate ≈ 25%** → detected! |

**Why ~25% error rate (仅 Alice-Bob 基匹配的位上计算)**：

```
Eve guesses basis:
  ┌─ 50% correct → perfect copy → 0% error
  └─ 50% wrong  → destroys photon → Bob gets random result → 50% error
Total = 0.5 × 0% + 0.5 × 50% = 25%
```

**The fundamental change (根本变化)**：

Classical: passive attack = undetectable  
Quantum (BB84): **you cannot measure a quantum system without disturbing it**  
→ Eve's "looking" IS a measurement → changes the state → introduces detectable errors  
→ **Undetectable passive attack becomes detectable active interference**

> PPT: COMP232-19-20-Quantum-26

---

## Q2(b) — RSA 部分同态 (7 marks)

> **EN**: *Explain why RSA can be considered as a partially homomorphic encryption algorithm.*

> **CN**: 解释为什么 RSA 可被视为部分同态加密算法。

---

同 Mock Q2(b)。

---

## Q2(c) — 认证技术三类 (8 marks)

> **EN**: *List three main categories of authentication techniques. Discuss advantages and disadvantages of using physical or behavioural characteristics for authentication.*

> **CN**: 列出三大认证技术类别。讨论使用物理或行为特征进行认证的优缺点。

---

**Three Categories (三大类别)**：

| Category | Basis | Examples |
|----------|-------|----------|
| **Password (密码)** | What you **KNOW** (Knowledge) | PIN, passphrase |
| **Token (令牌)** | What you **HAVE** (Possession) | Bank card, smart card, OTP device |
| **Biometrics (生物识别)** | What you **ARE** (Inherence) | Fingerprint, iris, face, voice |

**Token types (令牌子类)**：

| Type | Example | Feature |
|------|---------|---------|
| **Secure Storage Device** | Magnetic bank card | Stores static secret |
| **Active Device** | Authenticator app, chip card | Generates OTP (time-synchronous / challenge-response) |

**Biometrics — Advantages & Disadvantages (生物识别优缺点)**：

| Advantages (优点) | Disadvantages (缺点) |
|------|------|
| Cannot be readily shared, copied, or stolen (不可分享/复制/窃取) | Complicated technology (技术复杂) |
| Under normal circumstances, cannot be lost (不会丢失) | Requires specialized hardware (需专用硬件) |
| | High cost (高成本, 尽管在下降) |
| | **False positives** (假阳性/误报) — incorrectly accepting wrong person |
| | **False negatives** (假阴性/漏报) — failing to recognize correct person |

**Physical vs Behavioural characteristics (物理 vs 行为特征)**：

| Type | Examples |
|------|----------|
| **Physiological (生理)** | Fingerprints, retina, iris, face, hand geometry, vein thickness |
| **Behavioural (行为)** | Gait (步态), keystroke dynamics (击键动态), signature, voice patterns |

**Mitigation (应对缺陷)**: Multi-modal architecture — combine two or more biometrics (e.g. face + fingerprint) to reduce entropy and improve universality.

> PPT: COMP232-3-Authentication-26

---

## Q3(a) — 数字签名 (6 marks)

> **EN**: *Describe how a digital signature can be realised using a public-key cryptosystem.*

> **CN**: 描述如何用公钥密码体制实现数字签名。

---

**Digital Signature via RSA**:

| Step | Sender (Alice) | Receiver (Bob) |
|:---:|------|------|
| 1 | Compute $h = H(m)$ | — |
| 2 | Sign: $s = h^{d_A} \bmod n_A$ (with **own private key**) | — |
| 3 | Send $(m, s)$ | Receive $(m, s)$ |
| 4 | — | Decrypt: $h' = s^{e_A} \bmod n_A$ (with Alice's **public key**) |
| 5 | — | Compute own $H(m)$, compare with $h'$ |
| 6 | — | $H(m) = h'$ → signature valid ✓ |

**What this guarantees**:

| Property | How |
|----------|-----|
| **Authenticity (真实性)** | Only Alice has $d_A$ → only Alice could have created $s$ |
| **Integrity (完整性)** | If $m$ changed → $H(m)$ changes → $h' \neq H(m)$ → detected |
| **Non-repudiation (不可抵赖性)** | Alice cannot deny signing — her private key is unique to her |

> Note: in practice, sign $H(m)$ not $m$ directly — efficiency (fixed-length hash vs variable-length message).

> PPT: COMP232-6-Public-key-26

---

## Q3(b) — 信息保护四等级 (4 marks)

> **EN**: *Describe briefly 4 levels of information protection and the methods used at each level.*

> **CN**: 简述信息保护的四个等级及各自使用的方法。

---

| Level | What to Protect | Method | Analogy |
|:---:|------|------|------|
| **0** | Nothing (无) | None | 裸奔 |
| **1** | Message **Content** (内容) | **Encryption** (加密) | 锁信——别人知道你在通信但看不懂 |
| **2** | **Metadata** (元数据: 谁+何时+跟谁通信) | **PETs** (Privacy Enhancing Technologies, 隐私增强技术) | 匿名转发——隐藏通信关系 |
| **3** | Message **Existence** (通信存在性) | **Steganography** (隐写术) | 隐身——别人根本不知道你在通信 |

> PPT: COMP232-1-Intro-26

---

## Q3(c) — 混合网络 (7 marks)

> **EN**: *What is a mix-network? Describe functioning of a mix node and the process of communication in a mix-network.*

> **CN**: 什么是混合网络？描述混合节点的功能及通信过程。

---

**Mix Network (混合网络)**：

A chain of **Mix Nodes** providing anonymous communication. Each node receives multiple messages → processes them → outputs in random order → attacker **cannot match incoming and outgoing messages**.

**Mix Node functioning (混合节点功能)**：

| Step | Action |
|:---:|------|
| 1 | Receive multiple incoming messages from different senders |
| 2 | Decrypt one layer of encryption |
| 3 | **Re-order** all messages randomly |
| 4 | Forward to next hop / destination |

**Communication process (通信过程)**：

Messages encrypted from destination backwards:

$$E_{K_3}(E_{K_2}(E_{K_1}(msg)))$$

Each node peels one layer → re-orders → forwards. Each node only knows **previous hop** and **next hop** — never the full path.

| Comparison | Anonymizer | Mix Network | Crowds | Tor |
|------|:---:|:---:|:---:|:---:|
| Single attack point | ✗ (yes) | ✓ (no) | ✓ | ✓ |
| Crypto required | No | Yes | No | Yes |
| Defeats global attacker | ✗ | ✓ | ✗ | ✓ |
| Efficiency | High | Low | High | Medium |

> PPT: COMP232-11-Anonymity-26

---

## Q3(d) — 安全多方计算 (8 marks)

> **EN**: *Explain what are Secure Multi Party Computations and give an example of their possible applications.*

> **CN**: 解释什么是安全多方计算并给出可能的应用例子。

---

**SMPC (Secure Multi-Party Computation, 安全多方计算)**：

Multiple parties jointly compute a function $F(x_1, x_2, \dots, x_n)$ while keeping each party's private input $x_i$ **secret from all other parties**.

多方共同计算函数 $F$，各方输入保密互不暴露。

**Classic Example (经典例子)**：

**Yao's Millionaire Problem (姚氏百万富翁问题, 1982)**：
Two millionaires want to know who is richer **without revealing their actual wealth** to each other.

**How it works (简要原理)**：

| Component | Role |
|------|------|
| **Garbled Circuits (混淆电路)** | $P_1$ encrypts all possible outputs of $F(x,y)$ in a permuted table with two keys $(k_x, k_y)$. $P_2$ receives the table and uses keys to decrypt exactly one cell. |
| **Oblivious Transfer, OT (不经意传输)** | $P_2$ receives key $k_y$ corresponding to their private input $y$, without $P_1$ learning which key was chosen. |

**Applications (应用)**：

| Domain | Example |
|------|------|
| **Secure Auctions** (安全拍卖) | Danish sugar beet auction (2009) |
| **Voting** (投票) | Estonian Student Study (2015) |
| **Secure ML** (安全机器学习) | Train models on private data |
| **Privacy-Preserving Genomics** | Boston Wage Equity study (2017) |
| **Privacy-Preserving Genomics** | Analyze genetic data without exposing individuals |

> PPT: COMP232-16-ZKP-MPC-26

---

## Q4(a) — 迭代次数与暴力破解 (7 marks)

> **EN**: *What is an iteration count in implementations of password-based encryption? How may setting different values to iteration count affect the brute force search attack against an encryption algorithm?*

> **CN**: 基于密码的加密中迭代次数是什么？不同值如何影响暴力破解？

---

**Iteration Count (迭代次数)**：

In password-based key derivation (e.g. PBKDF2, bcrypt): the number of times a hash function is **repeatedly applied** to the password + salt before producing the final encryption key.

密码 + salt → 哈希 → 哈希 → 哈希 ... ($c$ 次) → 派生密钥

**Effect on brute force attack (对暴力破解的影响)**：

| $c$ value | Attacker cost | Legitimate user cost |
|:---:|---|---|
| **Low** (e.g. 1) | Fast to try each candidate password | Fast login |
| **High** (e.g. 100,000) | Each guess costs $c$ hash ops → **$c \times$ slower** | Login slightly slower (acceptable) |

**Key insight**：The iteration count is a **work factor multiplier**. Increasing $c$ slows down both attacker and legitimate user linearly — but this asymmetry helps: the attacker must try millions of passwords (each costing $c$ hashes), while the user authenticates once. Modern recommendations: $c \ge 100,000$ for PBKDF2.

> PPT: COMP232-7-9-Algorithms-26 (passwords)

---

## Q4(b) — 入侵检测两大类 (5 marks)

> **EN**: *What are the two main categories of intrusion detection methods? Describe advantages and disadvantages.*

> **CN**: 入侵检测方法的两个主要类别是什么？

---

同 Mock Q4(b)。

---

## Q4(c) — 协议漏洞分析 (9 marks)

> **EN**: *What is the possible issue with this protocol? How can it be fixed?*
> ```
> 1. A → B: N_a
> 2. B → A: N_b
> 3. A → B: {k_ab, A, N_b}_{K_a}
> 4. B → A: {N_a}_{k_ab}
> ```

> **CN**: 以下协议有什么问题？如何修复？（$K_a$ 是 A 的公钥）

---

**Analysis — apply the 5-point checklist (协议分析清单)**：

| # | Check | Result |
|:--:|------|:---:|
| 1 | Encryption target correct? (加密对象对不对) | **✗ FAIL** — $K_a$ is A's public key → only A can decrypt → B **cannot read** Msg 3 |
| 2 | Identity included? (有身份信息吗) | ✓ A and N_b included |
| 3 | Freshness? (防重放吗) | ✓ Nonces $N_a, N_b$ present |
| 4 | Nonce echoed correctly? | ✓ $N_a$ returned in Msg 4 |
| 5 | Session binding? | Need signature for authenticity |

**Issue (问题)**：

Msg 3 $\{k_{ab}, A, N_b\}_{K_a}$ is encrypted with $K_a$ (A's public key). B receives it but **cannot decrypt** — only A's private key can decrypt. Protocol deadlocks at step 3 — B never gets $k_{ab}$.

**Fix (修复)**：

| Step | Fix | Explanation |
|------|-----|-------------|
| **Minimal fix** | Change $K_a$ to $K_b$: $\{k_{ab}, A, N_b\}_{K_b}$ | B can now decrypt with his private key |
| **Best practice** | Sign then encrypt: $\{\{k_{ab}, A, N_b\}_{sk_A}\}_{K_b}$ | Adds authentication — B verifies it's from A |

> See: `笔记.md [8] 协议分析实战套路`

---

## Q4(d) — 聚集 vs 匿名信息 (4 marks)

> **EN**: *What is aggregated information? What is anonymized information? Is privacy protected if only some records of anonymized and aggregated information are known about a person?*

> **CN**: 什么是聚集信息？匿名信息？仅有部分记录时隐私是否受保护？

---

| Concept | Definition | Example |
|------|------|------|
| **Aggregated Information (聚集信息)** | Statistical summary from **many individuals** into a single record | National census data (totals, averages) |
| **Anonymized Information (匿名信息)** | Identifying fields **removed**; cannot directly infer identity | Knowing only someone's age = 35 |
| **PII (Personally Identifiable Information)** | Information from which identity **can be derived** | Name, ID number, account |

**Is privacy protected? (隐私受保护吗) — NO**：

| Why | Explanation |
|------|------|
| **Cross-referencing risk** | Aggregated + Anonymized records can be combined to **re-identify** individuals |
| **Classic case** | **Massachusetts Governor Medical Record Case** — state insurance commission released "anonymized" hospital visit data + Census Bureau published aggregated statistics → researchers cross-referenced both → successfully identified Governor Weld's personal medical records |

Conclusion: privacy is **NOT** guaranteed even with only anonymized + aggregated records — cross-referencing can de-anonymize individuals.

> PPT: COMP232-11-Anonymity-26

---

## Q5(a) — ECB 模式 (7 marks)

> **EN**: *For a block cipher, what is the Electronic Codebook mode (ECB)? Describe the main disadvantage and how it can be fixed.*

> **CN**: 什么是 ECB 模式？主要缺点及如何修复？

---

**ECB (Electronic Codebook Mode, 电子密码本模式)**：

$$C_i = E_K(P_i)$$

Each plaintext block encrypted **independently** with the **same key**.

**Main Disadvantage (主要缺点)**：

| Problem | Explanation |
|------|------|
| **Reveals data patterns (暴露数据模式)** | Identical plaintext blocks → **identical ciphertext blocks** → attacker sees repetition and structure |
| **Insecure for long messages** | Real messages often contain repetition → ECB leaks this |

Example: encrypting a bitmap image with ECB → outlines still visible.

**Fix (修复)**：Use **CBC (Cipher Block Chaining, 密码块链模式)**

$$C_i = E_K(C_{i-1} \oplus P_i), \quad C_0 = IV$$

| ECB | CBC |
|------|------|
| Same plaintext → same ciphertext | Same plaintext → **different** ciphertext |
| Each block independent | Each block chained with previous |
| No IV needed | Requires **IV** (Initialization Vector) |

> PPT: COMP232-5-Crypto-Symmetric-2-26

---

## Q5(b) — Tor 为何同时用对称和公钥加密 (8 marks)

> **EN**: *What is the reason for the anonymity protection system Tor to use both symmetric and public-key encryption?*

> **CN**: Tor 同时使用对称加密和公钥加密的原因是什么？

---

**Two-phase encryption model (两阶段加密模型)**：

| Phase | Encryption | Purpose |
|------|:---:|------|
| **Circuit Setup (电路建立)** | **Public-key (DH)** | Client does DH key exchange with Entry/Middle/Exit → negotiates symmetric keys $K_1, K_2, K_3$ |
| **Data Transfer (数据传输)** | **Symmetric (AES)** | Data wrapped in 3 nested layers: $E_{K_1}(E_{K_2}(E_{K_3}(data)))$. Each relay peels one layer. |

**Why both? (为何两者都需要)**：

| Reason | Explanation |
|------|------|
| **Key Distribution (密钥分发)** | Client and relays have **no pre-shared secret**. DH uses each relay's public key to securely negotiate symmetric keys over insecure channels — no physical meeting or KDC needed. |
| **Efficiency (效率)** | RSA is orders of magnitude too slow for real-time web browsing. AES is fast. DH only runs once during circuit setup; all subsequent traffic uses AES. |
| **Layered encryption needs independent keys** | Entry must not see Middle's layer, Middle must not see Exit's layer. DH negotiates 3 **independent** symmetric keys, one per hop. |

This is the **Hybrid Cryptosystem (混合加密系统)** in practice: public-key for heavy key establishment, symmetric for fast bulk encryption.

> PPT: COMP232-11-Anonymity-26

---

## Q5(c) — CryptDB 多层加密 (10 marks)

> **EN**: *What is a purpose of using multiple layers of encryption in CryptDB approach to querying encrypted databases?*

> **CN**: CryptDB 在加密数据库查询中使用多层加密的目的是什么？

---

**Purpose (目的)**: **Dynamic balance between functionality and security (功能性与安全性的动态平衡)**.

CryptDB wraps each data item in **multiple nested encryption layers** like onion skins. Initially wrapped in the most secure (least functional) layer. When a SQL query arrives, layers are **dynamically peeled** to expose the minimum layer that supports the required operation — revealing as little information as possible.

**Four Onion Types (四种洋葱层)**：

| Onion | Encryption | Supports |
|------|------|------|
| **Onion Search** | Searchable encryption | Text search, `LIKE` |
| **Onion Add** | **HOM (Homomorphic)** — additive | `SUM`, arithmetic on integers |
| **Onion Eq** | **DET (Deterministic)** → **RND (Randomized)** outer | `WHERE x = 'y'`, equality joins |
| **Onion Ord** | **OPE (Order-Preserving Encryption)** | `ORDER BY`, range queries `>`, `<` |

**Principle (原则)**：

```
More layers peeled = more SQL functionality exposed = more information leaked to server
```

| Layer Peeling | Functionality | Info Leaked to Server |
|:---:|:---:|:---:|
| None (all layers on) | Minimal | Almost nothing |
| DET exposed | Equality queries | Which values are equal |
| OPE exposed | Order + range queries | Order relationships between values |
| HOM exposed | Arithmetic | Computation results only |

**Key insight**：Server performs queries on encrypted data it **never sees in plaintext**. Each query peels only the layers it needs — paying the minimum privacy cost for the required functionality.

> PPT: COMP232-12-HomomorphicEnc-26
