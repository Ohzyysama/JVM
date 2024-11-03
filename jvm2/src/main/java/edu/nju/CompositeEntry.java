package edu.nju;

import java.io.IOException;

/**
 * format : dir/subdir;dir/subdir/*;dir/target.jar*
 */
public class CompositeEntry extends Entry{
    public CompositeEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException, ClassNotFoundException {
        String[] paths = this.classpath.split(PATH_SEPARATOR);
        for(String path : paths)
        {
            Entry re = ClassFileReader.chooseEntryType(path);
            byte[] ret;
            ret = re.readClassFile(className);
            if(ret != null)
                return ret;
        }
        return null;
    }
}

