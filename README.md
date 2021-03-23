# Multithreading_in_Java
The idea of the project is to work with multiple threads, and make sure they are executed 
in the order intended, without race conditions, deadlock, livelock, and starvation.

There is a signle restroom, and only one 3 people (threads) can use it at a time, all of the same gender.
i.e, if a Male Thread is inside the restroom, only another Male Thread can enter the restroom, while 
the females have to wait. And vice versa; if there is a Female Thread in the restroom, only another
Female Thread can enter the restroom, and Male Threads have to wait, until Female Threads are done
using the restroom. 

The program must make sure no Male and Female Threads are in the restroom at the same time, by using
  multithreading concepts and libraries including mutex locks and condition variables.
  
Also, each thread will spend 5 seconds in the restroom, and it can easily be changed inside the code
  to allow more/less time for the threads to spend inside the restroom.

The fairness policy of the restroom usage is as follows:
  If the restroom is full, with, say, 3 Female Threads, and there are both Male and Female Threads 
  waiting outside, we can allow up to 2 more Female Threads to go in as the Female Threads inside 
  leave the restroom. After the limit of 2 waiting Female Threads enter the restroom, no more Female
  Threads are allowed in, in order to make sure Male Threads get a chance to use the restroom. After
  all the Female Threads are out, we start allowing the Male Threads into the restroom, with the same
  limit: no more than 2 waiting Male Threads will be allowed in after waiting for the Male Threads
  already in the restroom.
