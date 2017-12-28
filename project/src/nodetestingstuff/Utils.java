package nodetestingstuff;

import java.util.Random;

public class Utils {
    public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
}