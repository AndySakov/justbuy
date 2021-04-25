# scala-rest-api-with-auth
A template/starter pack for a rest api with authentication written in Scala  
Developed and maintained by [Obafemi Teminife](https://www.github.com/AndySakov)  

## Routes
1. __AUTHENTICATE USER__  
  `POST` _SERVER_/auth/user  
  This route handles authentication of users.  
  __Form Structure__  
    `username`: The username to authenticate  
    `pass`: The password to use  
    
2. __CREATE NEW USER__  
  `POST` _SERVER_/create/user  
  __Form Structure__  
    `username`: The username of the new user  
    `pass`: The password of the new user  
    `fullname`: The fullname of the new user  
    `dob`: The date of birth of the new user formatted like this -> `yyyy-MM-dd`  
    
3. __UPDATE USER DETAIL__  
  `PUT` _SERVER_/update/user/_`detail`_  
   _detail in the url is the user detail to update_  
   __Form Structure__  
   `username`: The username of the user to update  
   `pass`: The password of the user to authenticate  
   `new_detail`: The new detail to replace the old one  
   
4. __DELETE USER__  
  `DELETE` _SERVER_/remove/user  
  __Form Structure__  
    `username`: The username for the user to delete  
    `pass`: The password of the user to delete  
    
## Note
The api requires that all requests are authorized with a special token in the header  
Contact any of the contributors to this project to obtain auth access  

## How to contribute to this project?
* Please follow all Scala naming and coding conventions
* Write comprehensive and helpful comments for your code to aid review
* Write tests to cover all your changes and or revisions  
  _See the following link for how to write a test_ [How to Write a Test in Scala](http://www.scalatest.org/user_guide/writing_your_first_test)
* Open a PR and wait for one of the admins to review

## Conclusion
If you spot any bugs feel free to leave an issue. And if it's an improvement do the same.  
