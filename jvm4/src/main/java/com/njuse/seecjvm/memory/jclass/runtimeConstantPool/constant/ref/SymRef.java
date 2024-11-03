package com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.ref;

import com.njuse.seecjvm.classloader.ClassLoader;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SymRef implements Constant {
    public RuntimeConstantPool runtimeConstantPool;
    public String className;    //format : java/lang/Object
    public JClass clazz;

    public void resolveClassRef() throws ClassNotFoundException, IllegalAccessException {
        //todo
        /**
         * Add some codes here.
         *
         * You can get a Jclass from runtimeConstantPool.getClazz()
         *
         * step 1
         * Complete the method isAccessibleTo() in Jclass
         * Make sure you know what is caller and what is callee.
         *
         * step2
         * Use ClassLoader.getInstance() to get the classloader
         * You should load class or interface C with initiating Loader of D
         *
         * step3
         * Check the permission and throw IllegalAccessException
         * Don't forget to set the value of clazz with loaded class
         */
        // Step 1: Complete the method isAccessibleTo() in JClass
        // This method should check if the class represented by this JClass object is accessible to the caller's class loader
        clazz = runtimeConstantPool.getClazz();
        // Step 2: Use ClassLoader.getInstance() to get the classloader
        ClassLoader classLoader = ClassLoader.getInstance();

        // Step 3: Load the class or interface represented by className with initiating Loader of D
        JClass resolvedClass = classLoader.loadClass(className, clazz.getLoadEntryType());

        // Step 4: Check the permission and throw IllegalAccessException if necessary
        if (!resolvedClass.isAccessibleTo(clazz)) {
            throw new IllegalAccessException("Access denied");
        }

        // Step 5: Set the value of clazz with the loaded class
        this.clazz = resolvedClass;
    }
}
