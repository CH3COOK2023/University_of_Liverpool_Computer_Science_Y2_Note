from Crypto.PublicKey import RSA
from Crypto.Signature import pss
from Crypto.Hash import SHA256
from Crypto.Cipher import PKCS1_v1_5

mykey = RSA.generate(2048)


# get the public key for the generated key
PK = mykey.public_key()
print(f'Public key e: {PK.e}')
print(f'Public key n: {PK.n}')

# secrete/private key is in the generated key

print(mykey.has_private())
print(f'Secrete key d: {mykey.d}')
print(f'Secrete key n: {mykey.n}')

# encrypt a message
# make sure you have the right key to do the encryption
message = b'security and privacy'

cipher = PKCS1_v1_5.new(PK)

#  RAS is normally used to encrypt symmetric keys, e.g. AES keys, we just encrypt a message for demonstration
ciphertext = cipher.encrypt(message)

print(f'The encrypted message: {ciphertext.hex()}')

# decryption
# make sure you use the right key to do decryption
cipher = PKCS1_v1_5.new(mykey)
sentinal = 'Error' # this is returned when an error is detected in decryption
message = cipher.decrypt(ciphertext, sentinal)
print(f'The decyrpted message is: {message}')
# generate digital signature
message = b'security and privacy'

hash = SHA256.new(message)

signature = pss.new(mykey).sign(hash)

print(f'The generated signature: {signature.hex()}')

# verify digital signature
message = b'security and privacy'
hash = SHA256.new(message)

verifier = pss.new(PK)

# the verify process
try:
    verifier.verify(hash, signature)
    print('The signature is valid.')
except ValueError:
    print('The signature is not valid.')
