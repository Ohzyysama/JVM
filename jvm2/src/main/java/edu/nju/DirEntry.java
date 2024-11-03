package edu.nju;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * format : dir/subdir/.../
 */
public class DirEntry extends Entry{
    public DirEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
       String filepath = this.classpath.isEmpty() ? className : this.classpath + FILE_SEPARATOR + className;
       try{
           File file = new File(filepath);
           return IOUtil.readFileByBytes(new FileInputStream(file));
       }catch(FileNotFoundException e){
           return null;
        }
    }
}
