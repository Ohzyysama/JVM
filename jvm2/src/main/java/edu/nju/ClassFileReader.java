package edu.nju;

import java.io.File;
import java.io.IOException;

public class ClassFileReader {
    private static final String FILE_SEPARATOR = File.separator;
    private static final String PATH_SEPARATOR = File.pathSeparator;

    public static Entry chooseEntryType(String classpath){

        if (classpath.contains(PATH_SEPARATOR)){
            return new CompositeEntry(classpath);
        }
        else if (new File(classpath).isFile() && (classpath.endsWith(".jar")) || (classpath.endsWith(".JAR")) || (classpath.endsWith(".zip")) || (classpath.endsWith(".ZIP"))){
            return new ArchivedEntry(classpath);
        } else if (classpath.contains("*")) {
            return new WildEntry(classpath);
        } else {
            return new DirEntry(classpath);
        }
    }

    /**
     *
     * @param classpath where to find target class
     * @param className format: /package/.../class
     * @return content of classfile
     */
    public static byte[] readClassFile(String classpath,String className) throws ClassNotFoundException{
        if(classpath == null){
            throw new ClassNotFoundException();
        }
        className = IOUtil.transform(className);
        className += ".class";
        Entry bootStrapReader = chooseEntryType(classpath);
        byte[] ret = new byte[0];
        try {
            ret = bootStrapReader.readClassFile(className);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ret==null)throw new ClassNotFoundException();
        return ret;
    }
}
