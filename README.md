# SmallChatApp
A small chat application written in Java during my sophomore year at SIUE.

A makefile for this project will be provided soon for those weary of jars and unwilling to compile it.

To use the chat application:

    1. Run the server application.
  
      a. Skip this step if you just want to run the client and server on your machine.
         If you intend to connect to the server remotely using a public IP, 
         you may need to port forward port 60100. The port is currently hard-coded (sorry)..
 
    2. Run the client application.
      
      a. Enter the host address (either IP, host address, or localhost)
      b. Enter a custom username (ex. AwesomeChuck)
      c. Enter a 16-Byte encryption key

            - "16-Bytes = 16 characters/numbers"

            - This can be anything you want and is intended to test

            - Information will (optionally) be encrypted using TEA or Tiny Encryption Algorithm, 
              a 128-bit encryption algorithm invented by David Wheeler in the 1940s. 
              To read more about David Wheeler visit:
              https://en.wikipedia.org/wiki/David_Wheeler_(computer_scientist)

            - Encryption is optional to see how the information looks to others (including the server)
              when user KEYs match, mismatch, and are not used.

            - ABSOLUTELY DO NOT SEND PERSONAL/PRIVATE INFORMATION USING THIS APPLICATION.
              Even with encryption, this application provides no guarentee that your data is safe from the bad guys!
      
      d. Have fun using the chat application! Try the following activities:

            - Connect multiple users on the same machine or other machines if you have port forwarded.

            - Try sending information to someone else when KEYs match, mismatch, and are not used.
          
            - Try out the various settings available in the menu to personalize your chat application!

            - Turn the volume up on your machine to get notification sounds!
                - NOTE you won't hear sounds for messages you send yourself.
