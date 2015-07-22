import java.io.*;
import java.util.regex.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class UtilityClass {
    // for debug message
    public static void log(String s) {
    //    System.out.println(TAG + " " + s);
    //    if(AopGlobalInfo.isAopDebugMode()) {
    //        Log.d(AopGlobalInfo.AOP_TAG, s);
    //    }
    }

    // for debug message
    public static void logd(String s, Exception e) {
        // System.out.println(TAG + "[DEBUG] " + s);
        // if(AopGlobalInfo.isAopDebugMode()) {
        //     Log.d(AopGlobalInfo.AOP_TAG, s, e);
        // }
    }

    // for debug message
    public static void logd(String s) {
        // System.out.println(TAG + "[DEBUG] " + s);
        // if(AopGlobalInfo.isAopDebugMode()) {
        //     Log.d(AopGlobalInfo.AOP_TAG, s);
        // }
    }

    public static void checkOS() {
        String OS = System.getProperty("os.name");
        if( OS.equalsIgnoreCase("linux") == false ) {
            logd("Only linux OSs are supported");
            System.exit(1);
        }
    }

    public static void echo(String value, String path) {
        try {
            File file = new File(path);
            PrintStream ps = new PrintStream(file);
            ps.print(value);
            ps.flush();
            ps.close();
        }
        catch (IOException e) {
            logd("Echo exception", e);
        }
    }

    public static void echo(int value, String path) {
        echo( "" + value, path);
    }

    public static void echo(long value, String path) {
        echo( "" + value, path);
    }

    // public static double getStdDev(CircularFifoQueue<Double> elements) {
    //     double sumOfElement = 0;
    //     for(double element : elements) {
    //         sumOfElement += element;
    //     }
    //     double avg = sumOfElement / elements.size();
    //     double sum = 0;
    //     for(double element : elements) {
    //         double elementToDouble = (double) element;
    //         sum += Math.pow( elementToDouble - avg, 2);
    //     }
    //     double stdDev = Math.sqrt(sum/elements.size());
    //     return stdDev;
    // }

    private static String nioRead(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        FileChannel channel = stream.getChannel();
        MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        String content = Charset.defaultCharset().decode(mappedBuffer).toString();
        stream.close();
        return content;
    }

    public static String justGetFirstLine(String filename) throws IOException {
        return nioRead(filename);
    }

    @Deprecated
    public static String oldJustGetFirstLine(String filename) throws IOException {
        String line = null;
        FileReader fileReader = new FileReader( filename );
        BufferedReader bReader = new BufferedReader(fileReader);
        line = bReader.readLine();
        bReader.close();
        fileReader.close();
        return line;
    }

}
