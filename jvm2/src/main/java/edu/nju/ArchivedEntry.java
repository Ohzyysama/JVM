package edu.nju;

import lombok.var;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
/**
 * format : dir/subdir/target.jar
 */
public class ArchivedEntry extends Entry{
    public ArchivedEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException, ClassNotFoundException {
        JarFile jarFile = new JarFile(classpath);
        JarEntry jarEntry = jarFile.getJarEntry(className);

        if (jarEntry == null) {
            throw new ClassNotFoundException();
        }

        int fileSize = (int) jarEntry.getSize();
        byte[] buffer = new byte[fileSize];

        try (var inputStream = jarFile.getInputStream(jarEntry)) {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead != fileSize) {
                throw new IOException("Failed to read class file: " + className);
            }
        }

        return buffer;
    }

    }

