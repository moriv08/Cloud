import java.io.*;

public class FileReadWrite {
    public static void main(String[] args) {

        String path = "comon/src/main/resources/";

//        File src = new File(path + "JavaCore_3.1.zip");
//        File dest = new File(path + "JavaCore_3.1_copy.zip");


        File src = new File(path + "foto.jpg");
        File dest = new File(path + "foto_copy.jpg");

            makeFile(src, dest);



    }

    static void makeFile(File src, File dest){


        try(
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest)
            ){

            int x;

            while ((x = in.read()) != -1)
                out.write(x);

//            byte[] buffer = new byte[1024];
//
//            while (reader.read(buffer) != -1)
//                writer.write(buffer);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

//    public static void makeFastFile(File src, File dest) {
//
//        try(
//            InputStream in = new FileInputStream(src);
//            OutputStream out = new FileOutputStream(dest);) {
//
//            long start = System.currentTimeMillis();
//
//            byte[] arr = new byte[8152];
//
//            while (in.read(arr) != -1) {
//                out.write(arr);
//            }
//
//            long end = System.currentTimeMillis();
//            System.out.println(end - start);
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    public static void makeSlowFile(File src, File dest){
//
//        try(
//                InputStream input = new InputStreamReader()
//                )
//    }

}
