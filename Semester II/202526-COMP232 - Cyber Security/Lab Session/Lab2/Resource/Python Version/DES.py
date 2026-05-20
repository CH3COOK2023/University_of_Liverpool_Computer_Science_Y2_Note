from Crypto.Cipher import DES
from Crypto.Util.Padding import pad, unpad


# Reference: https://pycryptodome.readthedocs.io/en/latest/src/cipher/des.html
# key needs to be 8 bytes
key = b'password'  # Password must be in bytes

# experiment more modes.
modes = [DES.MODE_ECB,
         DES.MODE_CBC,
         DES.MODE_CFB,
         DES.MODE_OFB,
         DES.MODE_CTR]


message = b'Privacy & Security'  # Message must be in bytes
ciphertexts = []

# DES Encryption in ECB mode
cipher = DES.new(key, DES.MODE_ECB)
ciphertext = cipher.encrypt(pad(message, 16))
plaintext = unpad(cipher.decrypt(ciphertext), 16)
# verify our decryption
print(plaintext)
ciphertexts.append(ciphertext)


# DES Encryption in CBC mode
cipher = DES.new(key, DES.MODE_CBC)
ciphertext = cipher.encrypt(pad(message, 8))
iv = cipher.iv
# we need to specify the initialisation vector in decryption
cipher = DES.new(key, DES.MODE_CBC, iv)
# cipher = DES.new(key, DES.MODE_CBC) # check the output without IV specified.
plaintext = unpad(cipher.decrypt(ciphertext), 8)
# verify our decryption
print(plaintext)

ciphertexts.append(ciphertext)
#  Out put the all ciphertexts
print(ciphertexts)
