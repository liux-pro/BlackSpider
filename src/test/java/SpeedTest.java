import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class SpeedTest {
    //test speed of copy
    @Test
    public void testExchange() {
        int times=10000;

        Random random = new Random();
        byte[] bytes = new byte[1920*1080*4];
        int length = bytes.length;
        random.nextBytes(bytes);
        byte[] bytesClone = bytes.clone();
        System.out.println("cloned array is equal from old one?"+Arrays.equals(bytesClone, bytes));

        for (int i = 0; i < length/2; i++) {
            byte temp = bytes[i];
            bytes[i]=bytes[length-i-1];
            bytes[length-i-1]=temp;
        }
        System.out.println("exchange head foot,equal?"+Arrays.equals(bytesClone, bytes));

        for (int i = 0; i < length/2; i++) {
            byte temp = bytes[i];
            bytes[i]=bytes[length-i-1];
            bytes[length-i-1]=temp;
        }

        System.out.println("exchange head foot again,equal?"+Arrays.equals(bytesClone, bytes));

        long l = System.currentTimeMillis();

//        for (int j = 0; j < times; j++) {
//            for (int i = 0; i < length/2; i++) {
//                byte temp = bytes[i];
//                bytes[i]=bytes[length-i-1];
//                bytes[length-i-1]=temp;
//            }
//        }
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < 768; j++) {
                System.arraycopy(bytes,j*768,bytesClone,j*768,bytes.length/768);
            }
        }
        System.out.println("exchange head foot "+times+" times,spend (ms)"+(System.currentTimeMillis()-l));

        System.out.println("exchange head foot "+times+" times,equal?"+Arrays.equals(bytesClone, bytes));


    }

}
