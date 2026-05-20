import hmac, hashlib



# create a new HMAC object
key = b'mysecret'
message = b'privacy&security'
mac = hmac.HMAC(key, message, digestmod=hashlib.sha256)

# calculate the hash value
hmac_value = mac.digest()
print(f'The HMAC in Bytes  {hmac_value}')
print(f'The HMAC in hex:   {hmac_value.hex()}')
