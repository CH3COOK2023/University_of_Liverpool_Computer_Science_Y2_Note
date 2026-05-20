from Crypto.Protocol.KDF import PBKDF2
from Crypto.Hash import SHA512
from Crypto.Random import get_random_bytes
from Crypto.Cipher import DES
from Crypto.Util.Padding import pad, unpad


# Reference: https://pycryptodome.readthedocs.io/en/latest/src/protocol/kdf.html#pbkdf2

# Define the password and salt
password = b'password'  # Password must be in bytes
salt = get_random_bytes(16)  # Generate a random 16-byte salt

# Derive a 8-byte key using PBKDF2
key_length = 8  # Desired key length in bytes
iterations = 1000  # Number of iterations for PBKDF2
key = PBKDF2(password, salt, dkLen=key_length, count=iterations, hmac_hash_module=SHA512)

print(f"Derived Key: {key.hex()}")
print(f"Salt: {salt.hex()}")

# use the derived key to encrypt a message using DES
message = b'Privacy & Security'
cipher = DES.new(key, DES.MODE_CBC)
ciphertext = cipher.encrypt(pad(message, 8))

print(ciphertext.hex())




