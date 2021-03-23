# Multithreading_in_Java
**The idea of the project is to work with multiple threads, and make sure they are executed 
in the order intended, without race conditions, deadlock, livelock, and starvation.**

There is a signle restroom, and ***only one 3 people (threads) can use the restroom at a time, all of the same gender.***
i.e, if a Male Thread is inside the restroom, only another Male Thread can enter the restroom, while 
the females have to wait. And vice versa; if there is a Female Thread in the restroom, only another
Female Thread can enter the restroom, and Male Threads have to wait, until Female Threads are done
using the restroom. 

The program must make sure ***there are no Male and Female Threads are in the restroom at the same time,***
  by using multithreading concepts and libraries including _mutex locks and condition variables._
  
Also, each thread will spend 5 seconds in the restroom, and it can easily be changed inside the code
  to allow more/less time for the threads to spend inside the restroom.

The fairness policy of the restroom usage is as follows:
  If the restroom is full, with, say, 3 Female Threads, and there are both Male and Female Threads 
  waiting outside, ***we can allow up to 2 more Female Threads to go in as the Female Threads inside 
  leave the restroom.*** After the limit of 2 waiting Female Threads enter the restroom, no more Female
  Threads are allowed in, in order to make sure Male Threads get a chance to use the restroom. After
  all the Female Threads are out, we start allowing the Male Threads into the restroom, with the same
  limit: ***no more than 2 waiting Male Threads will be allowed in after waiting for the Male Threads
  already in the restroom.***
  ***If, however, there are more than 3, say 10, Male Threads waiting, and no Female Thread shows up,
  then the program will allow all the Male Threads to go in, without imposing the limit of 2 stated above.***
  
The main of the program craetes 5 random threads, each with random gender, and "sends" them to the 
  restroom. Since there are 5 (more than the restroom limit of 3 people) trying to use the restroom,
  the Male and Female Threads will either go in or wait outside, according to the rules defined above.
  After 10 seconds, another group of 5 threads with random gender are being initialized. They are also
  being "sent" to the restroom simultaneously (based on the way threads are executed), and they will
  comply with the restroom rules already defined. A total of 20 threads with random gender are created,
  and the results of ***arriving, waiting for, entering, using, and leaving*** the restroom are displayed
  on the screen.
