# Message-Authentication-code-algoritham-using-java
A Message Authentication Code (MAC) is a block of few bytes that is used to authenticate a
message. The receiver can check this block and be sure that the message hasn’t been modified
by a third party. 

MAC requires two inputs- a message and a secret key known only to the originator of the message and the intended recipient(s). 
This allows the recipient of the message to verify the integrity of the message and authenticate that the messages’s sender
has the shared secret key.If a sender doesn’t know the secret key, the MAC value would be different, which would indicate 
to the recipient that the message was not from the original sender. 
