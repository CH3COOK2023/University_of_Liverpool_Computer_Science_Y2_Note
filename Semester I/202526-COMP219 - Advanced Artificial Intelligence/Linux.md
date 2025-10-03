安装 `pip`

```bash
# 使用 Python 内置的 ensurepip 安装
python3 -m ensurepip --user --upgrade
```

安装`huggingface-cli`

```bash
# 普通用户（无管理员权限）安装到用户目录
pip3 install --user huggingface-hub

# 有管理员权限（推荐系统级安装）
sudo pip3 install huggingface-hub
```

查看空间大小

```bash
df -h
```

