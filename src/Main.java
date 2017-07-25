import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by chaomaer on 7/19/17.
 */
public class Main {

    public static void main(String[] args) {
        File file = new File("3.txt");
        ArrayList<String> arrayList = new ArrayList<>();
        String ss;
        ss = init(file,arrayList);
        Language language = new Language(ss,arrayList,"S");
        language.generateSLR();
    }

    public static String init(File file, ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String[] tmp= bufferedReader.readLine().split(" ");
            arrayList.addAll(Arrays.asList(tmp));
            String line = null;
            boolean first = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (first) {
                    sb.append(line);
                    first = false;
                } else {
                    sb.append("\n");
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
