import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.commons.lang3.math.NumberUtils.min;

/**
 * @Auther Scott Shao
 */

public class P1 {

    private static final String fileName = "1/example.inp";

    public static void main(String... ignored) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));

            br.mark(2000);
            int imageNum = 30;
            ArrayList stringArrayList = new ArrayList();
            String line;
            while ((line = br.readLine()) != null) {
                stringArrayList.add(line);
            }
            ArrayList spaces = new ArrayList();
            for (int i = 0; i < stringArrayList.size(); i += 1) {
                if (stringArrayList.get(i).equals("")) {
                    int value = 0;
                    for (int j = 0; j < spaces.size(); j += 1) {

                    }
                    System.out.println(imageNum);
                } else {
                    String processing = stringArrayList.get(i).toString();
                    int spaceNum = numOfSpaces(processing);
                    imageNum = min(imageNum, spaceNum);
                    spaces.add(spaceNum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static int numOfSpaces(String line) {
        return line.length() - line.replaceAll(
                " ", "").length();
    }


}
