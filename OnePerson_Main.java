import java.util.Random;

public class OnePerson_Main {
  public static void main(String[] args) throws Exception {
    final int WAIT_TIME = 5;
    final int FEMALE = 0;
    final int MALE = 1;

    OnePerson people[] = new OnePerson[20];
    int genders[] = new int[20];

    double FEMALE_PROBABILITY = 0.6;

    int seed = 9;
    Random generator = new Random(seed);

    for(int i = 0; i < 20; i++) {
      if(generator.nextDouble() < FEMALE_PROBABILITY){
        genders[i] = FEMALE;
      }
      else {
        genders[i] = MALE;
      }
      people[i] = new OnePerson(i + 1, genders[i], WAIT_TIME);
    }

/*
//Trial (i)
    System.out.println("Trial 1");
    people[0].start();
    people[1].start();
    people[2].start();
    people[3].start();
    people[4].start();
    Thread.sleep(10000);
    people[5].start();
    people[6].start();
    people[7].start();
    people[8].start();
    people[9].start();
    Thread.sleep(10000);
    people[10].start();
    people[11].start();
    people[12].start();
    people[13].start();
    people[14].start();
    Thread.sleep(10000);
    people[15].start();
    people[16].start();
    people[17].start();
    people[18].start();
    people[19].start();
*/


//Trial (ii)
    System.out.println("Trial 2");
    people[0].start();
    people[1].start();
    people[2].start();
    people[3].start();
    people[4].start();
    people[5].start();
    people[6].start();
    people[7].start();
    people[8].start();
    people[9].start();
    Thread.sleep(10000);
    people[10].start();
    people[11].start();
    people[12].start();
    people[13].start();
    people[14].start();
    people[15].start();
    people[16].start();
    people[17].start();
    people[18].start();
    people[19].start();


/*
//Trial (iii)
    System.out.println("Trial 3");
    people[0].start();
    people[1].start();
    people[2].start();
    people[3].start();
    people[4].start();
    people[5].start();
    people[6].start();
    people[7].start();
    people[8].start();
    people[9].start();
    people[10].start();
    people[11].start();
    people[12].start();
    people[13].start();
    people[14].start();
    people[15].start();
    people[16].start();
    people[17].start();
    people[18].start();
    people[19].start();
*/
  }
}
