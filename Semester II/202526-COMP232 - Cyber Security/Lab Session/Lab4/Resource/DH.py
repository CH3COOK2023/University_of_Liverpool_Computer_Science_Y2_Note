from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import dh
from cryptography.hazmat.primitives.kdf.hkdf import HKDF

from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad



parameters = dh.generate_parameters(generator=2, key_size=1024)

print(f'The prime number p: {parameters.parameter_numbers().p}')
print(f'The generator g: {parameters.parameter_numbers().g}')


# let us generate Alice's private x and public key = g^x
alice_SK = parameters.generate_private_key()
# in real world case, Alice should send Alice_PK to Bob
alice_PK = alice_SK.public_key()
print(f"Alice's private key x: {alice_SK.private_numbers().x}")
print(f"Alice's public key y = g^x: {alice_PK.public_numbers().y}")


# generate Bob's private key x and public key = g^x
bob_SK = parameters.generate_private_key()
# in real world case, Bob should send bob_PK to Alice
bob_PK = bob_SK.public_key()
print(f"Bob's private key x: {bob_SK.private_numbers().x}")
print(f"Bob's public key y = g^x: {bob_PK.public_numbers().y}")

# exchange keys for both Alice and Bob,
alice_shared_key = alice_SK.exchange(bob_PK)
bob_shared_key = bob_SK.exchange(alice_PK)

print(f"Alice's shared key: {alice_shared_key.hex()}")
print(f"Bob's shared key:   {bob_shared_key.hex()}")

# now we can key derive AES keys
alice_derived_key = HKDF(
    algorithm=hashes.SHA256(),
    length=32,
    salt=None,
    info=b'handshake data',
).derive(alice_shared_key)


bob_derived_key = HKDF(
    algorithm=hashes.SHA256(),
    length=32,
    salt=None,
    info=b'handshake data',
).derive(bob_shared_key)

print(f"Alice's AES key: {alice_derived_key.hex()}")
print(f"Bob's AES key:   {bob_derived_key.hex()}")


# Let's do AES encryption using CBC mode
cipher = AES.new(bob_derived_key, AES.MODE_CBC)
message = b'Privacy Security|Data here goes to second block!'
# we need to pad to  16 bytes in CBC mode
ciphertext = cipher.encrypt(pad(message, 16))

# we need IV to recover the first block message, try delete this parameter to see how the decryption goes
iv = cipher.iv
cipher = AES.new(alice_derived_key, AES.MODE_CBC, iv)
plaintext = unpad(cipher.decrypt(ciphertext), 16)
print(f'Encypted messages: {ciphertext.hex()}')
print(f'Decrypted messages: {plaintext}')