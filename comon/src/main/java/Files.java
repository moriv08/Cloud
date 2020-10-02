import java.io.*;
import java.util.HashMap;
import java.util.function.DoubleToIntFunction;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

public class Files {
    public static void main(String[] args) throws IOException{

        String filePath = "comon/files/";
        boolean isTime = false;

        if (isTime){


            HashMap<String, Integer> m = new HashMap<String, Integer>();
            m.put("aaa", 222);
            m.put(null, 222);


            File one = new File(filePath + "one.txt");

            File two = new File(filePath + "two.txt");
            two.createNewFile();

            InputStream in = new FileInputStream(one);

            OutputStream out = new FileOutputStream(two);

//        int count;
//        while ((count = in.read()) != -1){
//            out.write(count);
//        }

            int count;
            byte[] buff = new byte[256];
            while ((count = in.read(buff)) != -1){
                out.write(buff, 0, count);
            }

            System.out.println(two.length());
            System.out.println(one.length());


        }


    }
}
