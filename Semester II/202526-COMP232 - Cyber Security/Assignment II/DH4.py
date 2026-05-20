# four_party_dh.py
# Four-party Diffie-Hellman key exchange (binary tree approach).
# Extends the 2-party (DH.py) and 3-party (DH3.py) patterns from Lab 4.
#
# Protocol (2 rounds):
#   R1: Pair (A,B) -> K_AB = g^(ab),  Pair (C,D) -> K_CD = g^(cd)
#   R2: A,B raise K_AB to c then d -> g^(abcd)
#       C,D raise K_CD to a then b -> g^(abcd)

from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import dh
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad

parameters = dh.generate_parameters(generator=2, key_size=1024)
p = parameters.parameter_numbers().p
g = parameters.parameter_numbers().g

alice_SK = parameters.generate_private_key()
a = alice_SK.private_numbers().x
A = alice_SK.public_key().public_numbers().y

bob_SK = parameters.generate_private_key()
b = bob_SK.private_numbers().x
B = bob_SK.public_key().public_numbers().y

carol_SK = parameters.generate_private_key()
c = carol_SK.private_numbers().x
C = carol_SK.public_key().public_numbers().y

dave_SK = parameters.generate_private_key()
d = dave_SK.private_numbers().x
D = dave_SK.public_key().public_numbers().y

# Round 1: pairwise DH
K_AB = pow(B, a, p)
assert pow(A, b, p) == K_AB, "K_AB mismatch"

K_CD = pow(D, c, p)
assert pow(C, d, p) == K_CD, "K_CD mismatch"

print("=== Round 1: Pairwise DH ===")
print(f"Alice computes K_AB = B^a mod p = g^(ab)")
print(f"Bob   verifies K_AB = A^b mod p = g^(ab)  [OK]")
print(f"Carol computes K_CD = D^c mod p = g^(cd)")
print(f"Dave  verifies K_CD = C^d mod p = g^(cd)  [OK]")
print()

# Round 2: cross-exchange
K_alice = pow(pow(K_AB, c, p), d, p)  # (g^ab)^c^d
K_bob   = pow(pow(K_AB, c, p), d, p)
K_carol = pow(pow(K_CD, a, p), b, p)  # (g^cd)^a^b
K_dave  = pow(pow(K_CD, a, p), b, p)

assert K_alice == K_bob == K_carol == K_dave, "Key mismatch!"
K = K_alice

print("=== Round 2: Cross-Exchange ===")
print(f"Alice: pow(pow(K_AB, c, p), d, p) = g^(abcd)")
print(f"Bob:   pow(pow(K_AB, c, p), d, p) = g^(abcd)")
print(f"Carol: pow(pow(K_CD, a, p), b, p) = g^(abcd)")
print(f"Dave:  pow(pow(K_CD, a, p), b, p) = g^(abcd)")
print()
print("=== Verification ===")
print(f"All four shared secrets match: {K_alice == K_bob == K_carol == K_dave}")
print()

def int_to_bytes(i: int) -> bytes:
    length = (i.bit_length() + 7) // 8
    return i.to_bytes(length, byteorder='big')

K_bytes = int_to_bytes(K)
print(f"Shared secret K = g^(abcd) (hex, {len(K_bytes)} bytes):")
print(K_bytes.hex())
print()

derived_key = HKDF(
    algorithm=hashes.SHA256(), length=32, salt=None,
    info=b'4party-DH-handshake',
).derive(K_bytes)

print(f"Derived AES-256 key (HKDF-SHA256, 32 bytes):")
print(derived_key.hex())
print()

message = b'Privacy Security|Bob -> Alice via 4-party DH!'
cipher = AES.new(derived_key, AES.MODE_CBC)
iv = cipher.iv
ciphertext = cipher.encrypt(pad(message, AES.block_size))

print(f"Plaintext  (Bob): {message}")
print(f"Ciphertext (Bob, AES-256-CBC, hex): {ciphertext.hex()}")
print(f"IV: {iv.hex()}")
print()

cipher2 = AES.new(derived_key, AES.MODE_CBC, iv)
plaintext = unpad(cipher2.decrypt(ciphertext), AES.block_size)
print(f"Decrypted (Alice): {plaintext}")
