import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.Random;

public class OnePerson extends Thread {
  static final int CAPACITY = 3;
  static final int FAIRNESS_POLICY = 2;
  private final int FEMALE = 0;
  private final int MALE = 1;
  /*
  @a mutex lock to provide mutual exclusion into the monitor
  */
  static ReentrantLock lock;

  /*
  @Two condition variables
              ok_for_male, ok_for_female
    used to block males/females to enter
  */
  static Condition ok_for_male;
  static Condition ok_for_female;

  /*
  @Two counters for capacity
              males_inside, females_inside
    keep track of the number of males and number of females
    currently using the facilities.
  */
  static int males_inside;
  static int females_inside;

  /*
  @Two counters for fairness
              males_let_in = 0, females_let_in = 0
    keep track of the number of males/females that entered
    while a person of opposite gender was waiting (< 2)
    males_let_in (while female(s) are already waiting)
    females_let_in (while male(s) are already waiting)
  */
  static int males_let_in;
  static int females_let_in;

  /*
  @Two counters for checking if signal was used by the signalled person
              males_let_in_inside = 0, females_let_in_inside = 0
    keep track of the number of males/females that have been signalled by
      a departing person and actually enterred the facility. We need this
      because the signalling thread does not know whether the signalled thread
      has used it or not.
  */
  static int males_let_in_inside;
  static int females_let_in_inside;

  private int id;
  private int gender;
  private int time;

  static int departure_index;

  /*
    Constructor
  */
  public OnePerson(int id, int gender, int time) {
    this.departure_index = 1;

    this.id = id;
    this.gender = gender;
    this.time = time;

    lock = new ReentrantLock();

    ok_for_male = lock.newCondition();
    ok_for_female = lock.newCondition();

    this.males_inside = 0;
    this.females_inside = 0;

    this.males_let_in = 0;
    this.females_let_in = 0;

    males_let_in_inside = 0;
    females_let_in_inside = 0;
  }
    public void Arrive(int id, int gender) {
      lock.lock();

      boolean was_signalled = false;

      try{
        if(gender == MALE) {
          Thread thr = Thread.currentThread();
          System.out.println("Male " + id + " ARRIVES");

          /*
              check if the there are:
              1) females currently using the UseFacilities OR
              2) if there are 3 males currently in (the maximum capacity) OR
              3) if there are females waiting AND there have already been 2 AND
                 males let in after the female(s) started waiting (arrived)
          */
          while(this.females_inside > 0 ||
                this.males_inside == this.CAPACITY ||
                  (lock.hasWaiters(this.ok_for_female) &&
                  this.males_let_in == this.FAIRNESS_POLICY &&
                  this.males_let_in_inside == this.males_let_in)) {
                System.out.println("Male " + id + " STARTS WAITING");
                //why is the male waiting?
                System.out.println("************Reason*****************");
                if(females_inside > 0) {
                  System.out.println("There are currently female(s) inside");
                }
                else if(males_inside == CAPACITY) {
                  System.out.println("The facility is full");
                }
                else if(males_let_in == this.FAIRNESS_POLICY) {
                  System.out.println("It is not fair anymore to let in another male");
                }
                System.out.println("*****************************************");
                try {
                    ok_for_male.await();
                }
                catch(InterruptedException e) {
                    System.err.println(e.getMessage());
                }


                was_signalled = true;

          }//endwhile

          if(lock.hasWaiters(this.ok_for_female) &&
              was_signalled == false) {
             males_let_in++;
          }

        }
        //gender == FEMALE
        else {
            System.out.println("Female " + id + " ARRIVES");
            while(this.males_inside > 0 ||
                this.females_inside == this.CAPACITY ||
                  (lock.hasWaiters(this.ok_for_male) &&
                  this.females_let_in == this.FAIRNESS_POLICY &&
                  this.females_let_in_inside == this.females_let_in)) {
                System.out.println("Female " + id + " STARTS WAITING");
                System.out.println("************Reason*****************");
                if(males_inside > 0) {
                  System.out.println("There are currently male(s) inside");
                }
                else if(females_inside == CAPACITY) {
                  System.out.println("The facility is full");
                }
                else if(females_let_in == this.FAIRNESS_POLICY) {
                  System.out.println("It is not fair anymore to let in another female");
                }
                System.out.println("*****************************************");
                try {
                  //if(lock.isHeldByCurrentThread()) {
                    ok_for_female.await();
                  //}
                }
                catch(InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                was_signalled = true;
              }
              /*
                if there is a male already waiting, then utilize the fairness
                policy, and increment the females_let_in by 1
              */
              if(lock.hasWaiters(this.ok_for_male) &&
                  was_signalled == false) {
                 females_let_in++;
              }
        }

      }finally {
        if(gender == MALE) {
          males_inside++;
          System.out.println("Male " + id + " ENTERS");
        }
        else{
          females_inside++;
          System.out.println("Female " + id + " ENTERS");
        }
          lock.unlock();
      }
    }

    public void UseFacilities(int id, int gender, int time) {
        lock.lock();
        try {
          try{
            if(gender == MALE) {
              //letting other males know that another signalled male has
              //   actually responded to the signal and is using the facility
              if(males_let_in > 0) {
                males_let_in_inside++;
              }
              System.out.println("Male " + id + " IS USING THE FACILITY");
              System.out.println("**************SNOPSHOT******************");
              System.out.print("   males_inside = " + males_inside);
              System.out.println("  females_inside = " + females_inside);
              System.out.println("   males_waiting = " +
                lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                lock.getWaitQueueLength(ok_for_female));
              System.out.println("   males_let_in = " + males_let_in +
                ", females_let_in = " + females_let_in);
              System.out.println("   males_let_in_inside = " + males_let_in_inside +
                ", females_let_in_inside = " + females_let_in_inside);
              System.out.println("****************************************");

            }
            else {
              //letting other females know that another signalled female has
              //   actually responded to the signal and is using the facility
              if(females_let_in > 0) {
                females_let_in_inside++;
              }
              System.out.println("Female " + id + " IS USING THE FACILITY");
              System.out.println("**************SNOPSHOT******************");
              System.out.print("   males_inside = " + males_inside);
              System.out.println("  females_inside = " + females_inside);
              System.out.println("   males_waiting = " +
                lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                lock.getWaitQueueLength(ok_for_female));
              System.out.println("   males_let_in = " + males_let_in +
                ", females_let_in = " + females_let_in);
              System.out.println("   males_let_in_inside = " + males_let_in_inside +
                ", females_let_in_inside = " + females_let_in_inside);
              System.out.println("****************************************");
            }

            lock.unlock();
            Thread.sleep(this.time * 1000);
            lock.lock();
          }
          catch(InterruptedException e) {
              //System.err.println(e.getMessage());
          }
      }finally {
          lock.unlock();
      }
    }//end of UseFacilities
    public void Depart(int id, int gender) {
      lock.lock();

      try {
      //if the departing person is male
      if(gender == MALE){
        System.out.println("Male " + id + " DEPARTS. DEPARTURE INDEX = " +
          departure_index);
        //the male departs
        males_inside--;
        departure_index++;
        System.out.println("**************SNOPSHOT AFTER DEPARTUTE******************");
        System.out.print("   males_inside = " + males_inside);
        System.out.println("  females_inside = " + females_inside);
        System.out.println("   males_waiting = " +
          lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
          lock.getWaitQueueLength(ok_for_female));
        System.out.println("   males_let_in = " + males_let_in +
          ", females_let_in = " + females_let_in);
        System.out.println("   males_let_in_inside = " + males_let_in_inside +
          ", females_let_in_inside = " + females_let_in_inside);
        System.out.println("*********************************************************");
        //if there are males waiting and it is still fair to signal another male
        if(lock.hasWaiters(ok_for_male) &&
              males_let_in < this.FAIRNESS_POLICY &&
              females_let_in_inside == females_let_in){
          //males_let_in and females_let_in are mutually exclusive:
          //   that is, if one is non-zero, the other must be 0
          females_let_in = 0;
          females_let_in_inside = 0;


          try {
            //keep waking up waiting males as long as there are ones waiting
            //   and as long as it is FAIR
            int number_of_waiters = lock.getWaitQueueLength(ok_for_male);
            for(int i = 0; i < CAPACITY - males_inside &&
                    i < number_of_waiters &&
                    males_let_in < FAIRNESS_POLICY; i++) {


                if(lock.hasWaiters(ok_for_female)) {
                    males_let_in++;
                }
                  //only increment the fairness policy males_let_in if there are
                  //people of opposite gender waiting
                  ok_for_male.signal();

                  System.out.println("         Male " + id + " Signals Male");
                  System.out.println("**************SNOPSHOT AFTER SIGNAL******************");
                  System.out.print("   males_inside = " + males_inside);
                  System.out.println("   females_inside = " + females_inside);
                  System.out.println("   males_waiting = " +
                    lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                    lock.getWaitQueueLength(ok_for_female));
                  System.out.println("   males_let_in = " + males_let_in +
                    ", females_let_in = " + females_let_in);
                  System.out.println("   males_let_in_inside = " + males_let_in_inside +
                    ", females_let_in_inside = " + females_let_in_inside);
                  System.out.println("******************************************************");
            }
          }
          catch(IllegalMonitorStateException e) {
            //System.err.println(e.getMessage());
          }
        }
        //either no males waiting, or not fair to signal another male,
        //   so if no more males inside, signal a female
        else{
          //if this departing male is the last male inside (males_inside == 0)
          //   and females are waiting, utilize the fairness policy
          //   if there are still other males inside other than this male,
          //   do not signal a female. So only signal the opposing gender
          //   if no more of the current gender are inside
          if(males_inside == 0 &&
            lock.hasWaiters(ok_for_female) &&
            males_let_in_inside == males_let_in) {

            males_let_in = 0;
            males_let_in_inside = 0;

            try {
                //keep waking up waiting females as long as there are
                //   ones waiting and as long as it is FAIR
                int number_of_waiters = lock.getWaitQueueLength(ok_for_female);
                for(int i = 0; i < CAPACITY - females_inside &&
                        i < number_of_waiters &&
                        females_let_in < FAIRNESS_POLICY; i++) {
                  if(lock.hasWaiters(ok_for_male)) {
                     females_let_in++;
                  }

                    //only increment the fairness policy males_let_in if there are
                    //people of opposite gender waiting
                    ok_for_female.signal();

                    System.out.println("         Male " + id + " Signals Female");
                    System.out.println("**************SNOPSHOT AFTER SIGNAL******************");
                    System.out.print("   males_inside = " + males_inside);
                    System.out.println("   females_inside = " + females_inside);
                    System.out.println("   males_waiting = " +
                      lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                      lock.getWaitQueueLength(ok_for_female));
                    System.out.println("   males_let_in = " + males_let_in +
                      ", females_let_in = " + females_let_in);
                    System.out.println("   males_let_in_inside = " + males_let_in_inside +
                      ", females_let_in_inside = " + females_let_in_inside);
                    System.out.println("******************************************************");
                }
            }

          catch(IllegalMonitorStateException e) {
            //System.err.println(e.getMessage());
          }
        }
        }
      }
      //if the departing person is female
      else {
        System.out.println("Female " + id + " DEPARTS. DEPARTURE INDEX = " +
          departure_index);
        females_inside--;
        departure_index++;
        System.out.println("**************SNOPSHOT AFTER DEPARTURE******************");
        System.out.print("   males_inside = " + males_inside);
        System.out.println("  females_inside = " + females_inside);
        System.out.println("   males_waiting = " +
          lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
          lock.getWaitQueueLength(ok_for_female));
        System.out.println("   males_let_in = " + males_let_in +
          ", females_let_in = " + females_let_in);
        System.out.println("   males_let_in_inside = " + males_let_in_inside +
          ", females_let_in_inside = " + females_let_in_inside);
        System.out.println("*********************************************************");

        //if there are females waiting and it is still fair to let
        //   another female
        if(lock.hasWaiters(ok_for_female) &&
              females_let_in < this.FAIRNESS_POLICY &&
              males_let_in_inside == males_let_in) {
                //males_let_in and females_let_in are mutually exclusive:
                //   that is, if one is non-zero, the other must be 0
          males_let_in = 0;
          males_let_in_inside = 0;


          try {
            //keep waking up waiting males as long as there are ones waiting
            //   and as long as it is FAIR
            int number_of_waiters = lock.getWaitQueueLength(ok_for_female);
            for(int i = 0; i < CAPACITY - females_inside &&
                  i < number_of_waiters &&
                  females_let_in < FAIRNESS_POLICY; i++) {


             if(lock.hasWaiters(ok_for_male)) {
                  females_let_in++;
              }
                  //only increment the fairness policy males_let_in if there are
                  //people of opposite gender waiting

                  ok_for_female.signal();

                  System.out.println("         Female " + id + " Signals Females");
                  System.out.println("**************SNOPSHOT AFTER SIGNAL******************");
                  System.out.print("   males_inside = " + males_inside);
                  System.out.println("   females_inside = " + females_inside);
                  System.out.println("   males_waiting = " +
                    lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                    lock.getWaitQueueLength(ok_for_female));
                  System.out.println("   males_let_in = " + males_let_in +
                    ", females_let_in = " + females_let_in);
                  System.out.println("   males_let_in_inside = " + males_let_in_inside +
                    ", females_let_in_inside = " + females_let_in_inside);
                  System.out.println("*******************************************************");
            }
          }
          catch(IllegalMonitorStateException e) {
            //System.err.println(e.getMessage());
          }
        }
        //either no females waiting, or not fair to signal another female,
        //   so if no more females inside, signal a male
        else{
          //if this departing female is the last male inside
          //   (females_inside == 0)
          //   and males are waiting, utilize the fairness policy
          //   if there are still other males inside other than this male,
          //   do not signal a female. So only signal the opposing gender
          //   if no more of the current gender are inside
          if(females_inside == 0 &&
            lock.hasWaiters(ok_for_male) &&
            females_let_in_inside == females_let_in) {
            //males_let_in and females_let_in are mutually exclusive:
            //   that is, if one is non-zero, the other must be 0
            females_let_in = 0;
            females_let_in_inside = 0;

            try {
             //keep waking up waiting males as long as there are ones waiting
             //   and as long as it is FAIR

            int number_of_waiters = lock.getWaitQueueLength(ok_for_male);
            for(int i = 0; i < CAPACITY - males_inside &&
                i < number_of_waiters &&
                males_let_in < FAIRNESS_POLICY; i++) {
              if(lock.hasWaiters(ok_for_female)) {
                males_let_in++;
              }

                //only increment the fairness policy males_let_in if there are
                //people of opposite gender waiting
                ok_for_male.signal();

                System.out.println("         Female " + id + " Signals Males");
                System.out.println("**************SNOPSHOT AFTER SIGNAL******************");
                System.out.print("   males_inside = " + males_inside);
                System.out.println("   females_inside = " + females_inside);
                System.out.println("   males_waiting = " +
                  lock.getWaitQueueLength(ok_for_male) + ",  females_waiting = " +
                  lock.getWaitQueueLength(ok_for_female));
                System.out.println("   males_let_in = " + males_let_in +
                  ", females_let_in = " + females_let_in);
                System.out.println("   males_let_in_inside = " + males_let_in_inside +
                  ", females_let_in_inside = " + females_let_in_inside);
                System.out.println("******************************************************");
            }
          }
          catch(IllegalMonitorStateException e) {
            //System.err.println(e.getMessage());
          }
        }
        }
      }
    }finally {
        lock.unlock();
    }//end of finally

  }//end of Depart

    public void run(){
      Arrive(id, gender);
      UseFacilities(id, gender, time);
      Depart(id, gender);
    }
}
