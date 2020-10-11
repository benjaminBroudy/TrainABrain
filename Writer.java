
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    public Writer() {}

    public static void write(String fileName, String message) throws IOException {

    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writer.write(message);
     
    writer.close();
    
}

}
