# Lab 21 : Custom Keycloak Required Action

Keycloak has some built-in and default Required Actions, which are executed upon authentication of a user. Required actions are e.g. "update password", "update profile" or "configure otp", etc.

But, like with most of Keycloak's functionality, you can also implement your own custom Required Actions with your desired functionality and logic.

This lab provides an example on how to implement a custom RequiredAction to enforce the user to enter its mobile phone number, if not already set.. But it's not just limited to this, you can implement the logic you want and what is needed do be executed by a user after a successful login.