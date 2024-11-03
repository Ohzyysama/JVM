package edu.nju;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class WildEntry extends Entry {

    public WildEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException, ClassNotFoundException {
        String rootPath = this.classpath.substring(0, this.classpath.length() - 1);
        File dir = new File(rootPath);
        File[] files = dir.listFiles();
        if (files == null)
        {
            return null;
        }
        String newPath = "";
        for (File file : files)
        {
            if (file.getName().endsWith(".jar") || file.getName().endsWith(".JAR") || file.getName().endsWith(".zip") || file.getName().endsWith(".ZIP"))
            {
                String path = rootPath + FILE_SEPARATOR + file.getName();
                if(newPath.isEmpty()){
                    newPath = path;
                }else{
                    newPath = newPath + PATH_SEPARATOR + path;
                }

            }
        }
        return newPath.isEmpty() ? null : new CompositeEntry(newPath).readClassFile(className);
    }


}