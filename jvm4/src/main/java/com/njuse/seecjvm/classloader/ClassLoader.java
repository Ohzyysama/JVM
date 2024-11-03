package com.njuse.seecjvm.classloader;

import com.njuse.seecjvm.classloader.classfileparser.ClassFile;
import com.njuse.seecjvm.classloader.classfilereader.ClassFileReader;
import com.njuse.seecjvm.classloader.classfilereader.classpath.EntryType;
import com.njuse.seecjvm.memory.MethodArea;
import com.njuse.seecjvm.memory.jclass.Field;
import com.njuse.seecjvm.memory.jclass.InitState;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.DoubleWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.FloatWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.IntWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.LongWrapper;
import com.njuse.seecjvm.runtime.Vars;
import com.njuse.seecjvm.runtime.struct.NullObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

public class ClassLoader {
    private static ClassLoader classLoader = new ClassLoader();
    private ClassFileReader classFileReader;
    private MethodArea methodArea;

    private ClassLoader() {
        classFileReader = ClassFileReader.getInstance();
        methodArea = MethodArea.getInstance();
    }

    public static ClassLoader getInstance() {
        return classLoader;
    }

    /**
     * load phase
     *
     * @param className       name of class
     * @param initiatingEntry null value represents load MainClass
     */
    public JClass loadClass(String className, EntryType initiatingEntry) throws ClassNotFoundException {
        JClass ret;
        if ((ret = methodArea.findClass(className)) == null) {
            return loadNonArrayClass(className, initiatingEntry);
        }
        return ret;
    }

    private JClass loadNonArrayClass(String className, EntryType initiatingEntry) throws ClassNotFoundException {
        try {
            Pair<byte[], Integer> res = classFileReader.readClassFile(className, initiatingEntry);
            byte[] data = res.getKey();
            EntryType definingEntry = new EntryType(res.getValue());
            //define class
            JClass clazz = defineClass(data, definingEntry);
            //link class
            linkClass(clazz);
            return clazz;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    /**
     *
     * define class
     * @param data binary of class file
     * @param definingEntry defining loader of class
     */
    private JClass defineClass(byte[] data, EntryType definingEntry) throws ClassNotFoundException {
        ClassFile classFile = new ClassFile(data);
        JClass clazz = new JClass(classFile);
        //todo
        /**
         * Add some codes here.
         *
         * update load entry of the class
         * load superclass recursively
         * load interfaces of this class
         * add to method area
         */
        clazz.setLoadEntryType(definingEntry);

        // Load superclass recursively
        resolveSuperClass(clazz);

        // Load interfaces of this class
        resolveInterfaces(clazz);

        // Add to method area
        methodArea.addClass(clazz.getName(), clazz);
        return clazz;
    }

    /**
     * load superclass before add to method area
     */
    private void resolveSuperClass(JClass clazz) throws ClassNotFoundException {
        //todo
        /**
         * Add some codes here.
         *
         * Use the load entry(defining entry) as initiating entry of super class
         */
        if (!clazz.getSuperClassName().equals("")) {
            JClass superClass = loadClass(clazz.getSuperClassName(), clazz.getLoadEntryType());
            clazz.setSuperClass(superClass);
        }
    }

    /**
     * load interfaces before add to method area
     */
    private void resolveInterfaces(JClass clazz) throws ClassNotFoundException {
        //todo
        /**
         * Add some codes here.
         *
         * Use the load entry(defining entry) as initiating entry of interfaces
         */
        String[] interfaceNames = clazz.getInterfaceNames();
        JClass[] interfaces = new JClass[interfaceNames.length];
        for (int i = 0; i < interfaceNames.length; i++) {
            interfaces[i] = loadClass(interfaceNames[i], clazz.getLoadEntryType());
        }
        clazz.setInterfaces(interfaces);
    }


    /**
     * link phase
     */
    private void linkClass(JClass clazz) {
        verify(clazz);
        prepare(clazz);
    }

    /**
     * You don't need to write any code here.
     */
    private void verify(JClass clazz) {
        //do nothing
    }

    private void prepare(JClass clazz) {
        //todo
        /**
         * Add some codes here.
         *
         * step1 (We do it for you here)
         *      count the fields slot id in instance
         *      count the fields slot id in class
         * step2
         *      alloc memory for fields(We do it for you here) and init static vars
         * step3
         *      set the init state
         */
        calInstanceFieldSlotIDs(clazz);//from below
        calStaticFieldSlotIDs(clazz);//from below
        allocAndInitStaticVars(clazz);
        clazz.setInitState(InitState.PREPARED);
    }


    /**
     * count the number of field slots in instance
     * long and double takes two slots
     * the field is not static
     */
    private void calInstanceFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        if (clazz.getSuperClass() != null) {
            slotID = clazz.getSuperClass().getInstanceSlotCount();
        }
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (!f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setInstanceSlotCount(slotID);
    }

    /**
     * count the number of field slots in class
     * long and double takes two slots
     * the field is static
     */
    private void calStaticFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setStaticSlotCount(slotID);

    }

    /**
     * primitive type is set to 0
     * ref type is set to null
     */
    private void initDefaultValue(JClass clazz, Field field) {
        //todo
        /**
         * Add some codes here.
         * step 1
         *      get static vars of class
         * step 2
         *      get the slotID index of field
         * step 3
         *      switch by descriptor or some part of descriptor
         *      Handle basic type ZBCSIJDF and references (with new NullObject())
         */
        Vars staticVars = clazz.getStaticVars();

        // Step 2: Get the slotID index of field
        int slotId = field.getSlotID();

        // Step 3: Switch by descriptor or some part of descriptor
        String descriptor = field.getDescriptor();
        switch (descriptor.charAt(0)) {
            case 'Z': // boolean
            case 'B': // byte
            case 'C': // char
            case 'S': // short
            case 'I': // int
                staticVars.setInt(slotId, 0); // Initialize with default value (0 for numeric types)
                break;
            case 'J': // long
                staticVars.setLong(slotId, 0L); // Initialize with default value (0L for long)
                break;
            case 'F': // float
                staticVars.setFloat(slotId, 0.0f); // Initialize with default value (0.0f for float)
                break;
            case 'D': // double
                staticVars.setDouble(slotId, 0.0); // Initialize with default value (0.0 for double)
                break;

            default:
                break;
        }
    }

    /**
     * load const value from runtimeConstantPool for primitive type
     * String is not support now
     */
    private void loadValueFromRTCP(JClass clazz, Field field) {
        //todo
        /**
         * Add some codes here.
         *
         * step 1
         *      get static vars and runtime constant pool of class
         * step 2
         *      get the slotID and constantValue index of field
         * step 3
         *      switch by descriptor or some part of descriptor
         *      just handle basic type ZBCSIJDF, you don't have to throw any exception
         *      use wrapper to get value
         *
         *  Example
         *      long longVal = ((LongWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
         */
        // Step 1: Get static vars and runtime constant pool of class
        Vars staticVars = clazz.getStaticVars();
        RuntimeConstantPool rtcp = clazz.getRuntimeConstantPool();

        // Step 2: Get the slotID and constantValue index of field
        int slotId = field.getSlotID();
        int constantPoolIndex = field.getConstValueIndex();

        // Step 3: Switch by descriptor or some part of descriptor
        String descriptor = field.getDescriptor();
        switch (descriptor.charAt(0)) {
            case 'Z': // boolean
                staticVars.setInt(slotId, ((Integer) ((IntWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).intValue());
                break;
            case 'B': // byte
                staticVars.setInt(slotId, ((Integer) ((IntWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).intValue());
                break;
            case 'C': // char
                staticVars.setInt(slotId, ((Integer) ((IntWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).intValue());
                break;
            case 'S': // short
                staticVars.setInt(slotId, ((Integer) ((IntWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).intValue());
                break;
            case 'I': // int
                staticVars.setInt(slotId, ((Integer) ((IntWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).intValue());
                break;
            case 'J': // long
                staticVars.setLong(slotId, ((Long) ((LongWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).longValue());
                break;
            case 'D': // double
                staticVars.setDouble(slotId, ((Double) ((DoubleWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).doubleValue());
                break;
            case 'F': // float
                staticVars.setFloat(slotId, ((Float) ((FloatWrapper) rtcp.getConstant(constantPoolIndex)).getValue()).floatValue());
                break;
        }
    }

    /**
     * the value of static final field is in com.njuse.seecjvm.runtime constant pool
     * others will be set to default value
     */
    private void allocAndInitStaticVars(JClass clazz) {
        clazz.setStaticVars(new Vars(clazz.getStaticSlotCount()));
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            //todo
            /**
             * Add some codes here.
             *
             * Refer to manual for details.
             */
            if (f.isStatic() && f.getConstValueIndex() != 0)
            {
                loadValueFromRTCP(clazz, f);
            } else {
                initDefaultValue(clazz, f);
            }
        }
    }
}
