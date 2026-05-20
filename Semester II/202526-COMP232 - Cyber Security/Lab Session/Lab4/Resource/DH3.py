# three_party_dh.py
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import dh
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad

# Setup DH parameters 
parameters = dh.generate_parameters(generator=2, key_size=1024)
p = parameters.parameter_numbers().p
g = parameters.parameter_numbers().g
print(f"The prime number p: {p}")
print(f"The generator g: {g}\n")

# Generate private/public for Alice, Bob, Carol
alice_SK = parameters.generate_private_key()
alice_x = alice_SK.private_numbers().x
alice_PK_y = alice_SK.public_key().public_numbers().y
print(f"Alice private a: {alice_x}")
print(f"Alice public A = g^a: {alice_PK_y}\n")

bob_SK = parameters.generate_private_key()
bob_x = bob_SK.private_numbers().x
bob_PK_y = bob_SK.public_key().public_numbers().y
print(f"Bob private b: {bob_x}")
print(f"Bob public B = g^b: {bob_PK_y}\n")

carol_SK = parameters.generate_private_key()
carol_x = carol_SK.private_numbers().x
carol_PK_y = carol_SK.public_key().public_numbers().y
print(f"Carol private b: {carol_x}")
print(f"Carol public C = g^c: {carol_PK_y}\n")

# Round 1: broadcast public values A, B, C (we already have alice_PK_y, etc.)

# Round 2: compute one-step intermediates and "send" them cyclically
# Alice computes B^a and sends to Carol
alice_intermediate_for_carol = pow(bob_PK_y, alice_x, p)    # g^{ab}
# Bob computes C^b and sends to Alice
bob_intermediate_for_alice = pow(carol_PK_y, bob_x, p)      # g^{bc}
# Carol computes A^c and sends to Bob
carol_intermediate_for_bob = pow(alice_PK_y, carol_x, p)    # g^{ac}

print("Intermediates (simulated exchanged values):")
print(f" Alice -> Carol : B^a = {alice_intermediate_for_carol}")
print(f" Bob   -> Alice : C^b = {bob_intermediate_for_alice}")
print(f" Carol -> Bob   : A^c = {carol_intermediate_for_bob}\n")

# Final step: each raises the received intermediate to their private key
# Alice receives bob_intermediate_for_alice (which is C^b) and computes (C^b)^a = g^{abc}
alice_shared_int = pow(bob_intermediate_for_alice, alice_x, p)
# Bob receives carol_intermediate_for_bob (which is A^c) and computes (A^c)^b = g^{abc}
bob_shared_int = pow(carol_intermediate_for_bob, bob_x, p)
# Carol receives alice_intermediate_for_carol (which is B^a) and computes (B^a)^c = g^{abc}
carol_shared_int = pow(alice_intermediate_for_carol, carol_x, p)

print("Final shared integers (should all match):")
print(f"Alice shared int: {alice_shared_int}")
print(f"Bob   shared int: {bob_shared_int}")
print(f"Carol shared int: {carol_shared_int}\n")

assert alice_shared_int == bob_shared_int == carol_shared_int, "Shared secrets do not match!"

# Convert integer shared secret -> bytes (big-endian)

def int_to_bytes(i: int) -> bytes:
    # length = number of bytes needed
    length = (i.bit_length() + 7) // 8
    return i.to_bytes(length, byteorder='big')


shared_bytes = int_to_bytes(alice_shared_int)
print(f"Shared secret: {shared_bytes.hex()}")

# Derive AES-256 key with HKDF (SHA-256)
derived_key = HKDF(
    algorithm=hashes.SHA256(),
    length=32,
    salt=None,
    info=b'3party handshake',
).derive(shared_bytes)

print(f"Derived AES-256 key: {derived_key.hex()}")

# Use AES-CBC to encrypt a message (Bob will encrypt, Alice will decrypt)
message = b'Privacy Security|Data here goes to second block!'
cipher = AES.new(derived_key, AES.MODE_CBC)   # random IV generated
iv = cipher.iv
ciphertext = cipher.encrypt(pad(message, AES.block_size))
print(f"Ciphertext (hex): {ciphertext.hex()}")
print(f"IV (hex): {iv.hex()}\n")

# Decryption by Alice (simulate another party using same derived_key)
cipher = AES.new(derived_key, AES.MODE_CBC, iv)
plaintext = unpad(cipher.decrypt(ciphertext), AES.block_size)
print(f"Decrypted plaintext: {plaintext!r}")
