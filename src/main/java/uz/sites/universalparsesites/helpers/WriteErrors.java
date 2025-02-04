package uz.sites.universalparsesites.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteErrors {

    public void write(String fileName, String error){
        try {
            File file = new File(fileName);
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write("\n" + error);
            br.close();
            fr.close();
        } catch (IOException ioe) {
            System.out.println("Couldn't write to file");
        }
    }
}
