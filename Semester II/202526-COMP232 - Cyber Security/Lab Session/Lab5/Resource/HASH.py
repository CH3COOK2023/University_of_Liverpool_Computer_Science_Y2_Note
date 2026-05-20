from Crypto.Hash import SHA256, HMAC

# Reference: https: //pycryptodome.readthedocs.io/en/latest/src/hash/hash.html

message = b'Security and Privacy' # try replace i with l to see the hash output difference
hash = SHA256.new(message)

print(f"The hash value is : {hash.hexdigest()}")


# HMAC example
secret = b'MySecret'
hash = HMAC.new(secret, digestmod=SHA256)
message = b'Security and Privacy'
mac = hash.update(message).hexdigest()

print(f"The HMAC value is : {hash.hexdigest()}")
print(mac)

# verify HMAC
secret = b'MySecret'
hash = HMAC.new(secret, digestmod=SHA256)
hash.update(message)
try:
    hash.hexverify(mac)
    print(f'The message: {message} is valid.')
except ValueError:
    print("The message or the key is wrong")
