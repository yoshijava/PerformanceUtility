import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class Test {
    public static void main(String... args) throws IOException {
        {
            long t1 = System.nanoTime();
            UtilityClass.oldJustGetFirstLine("c:/tmp/Test.java");
            long t2 = System.nanoTime();
            System.out.println("Old I/O: " + (t2-t1));
        }
        {
            long t1 = System.nanoTime();
            UtilityClass.justGetFirstLine("c:/tmp/Test.java");
            long t2 = System.nanoTime();
            System.out.println("New I/O: " + (t2-t1));
        }
    }
}
