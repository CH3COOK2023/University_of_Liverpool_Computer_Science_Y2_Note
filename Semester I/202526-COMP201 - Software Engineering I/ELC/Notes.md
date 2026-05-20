

绘制用例图：

1. 找到Actors
2. 说出他们要干什么（动词）
3. 绘图





图书馆的图书借阅系统有哪两个主要**用户（Users）**？
- **Members**
- 学校图书管理员（**Librarian**）



这些人类参与者如何与图书借阅系统交互？他们需要完成哪些常见任务？**提示：使用动词**

**Members：**

- [认证用户]+查询个人信息（Search for books）
- [认证用户]+借书（Borrow the books）
- [认证用户]+还书（Return the books）
- [认证用户]+支付滞纳金

**Librarian**：

- **删除图书（Delete a book）**
- **增加图书（Add a book）**
- **查询任何记录**
- [认证用户] + **添加会员（Add a member）**
- [认证用户] + **删除会员（Delete a member）**
- [认证用户] + **给会员办理借书业务（Issue books to members）**
- [认证用户] + **给会员办理还书业务（Accept returned book from members）**
- [认证用户] + **更改会员信息** 
- **收取滞纳金（Collect late fees）**





（这里仅注重Human Actors）

饮料机示例：

Operator（实际拥有这台机器靠这台机器赚钱的人）

1. [鉴权] + **机器当前库存**
2. [鉴权] + **机器现金水平**
3. [鉴权] + 下**载账户信息**
4. 发送配方给机器
5. 创建账户



Engineer（工程师，加料的）

1. 添加咖啡
2. 取现金
3. 



或许有很多Use case，但是只需要找出最主要的15个。每个Use case 画出这种图

| ID              | UC1                                                          |
| --------------- | ------------------------------------------------------------ |
| Actors          | Librarian, Member                                            |
| Name            | Issue Book to Member                                         |
| Description     | The librarian issues a book to a member, updating the system to reflect the book’s status as on loan. |
| Pre- conditions | The member is authenticated (using a library card or proof of address). The book is available in the system. |
| Event flow      | 1. The librarian verifies the member’s identity using a library card. 2. The librarian checks the availability of the requested book in the system. 3. The librarian assigns the book to the member’s account and marks it as "on loan". 4. The system updates the borrowing record for both the member and the book. |
| Post- condition | The book’s status is updated to "on loan", and the member’s borrowing record is updated. |
| Includes        | Authenticate member                                          |
| Extensions      | None                                                         |
| Triggers        | Member requests to borrow a book.                            |























