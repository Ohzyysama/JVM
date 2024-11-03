package com.njuse.seecjvm.memory.jclass;

import com.njuse.seecjvm.classloader.classfileparser.MethodInfo;
import com.njuse.seecjvm.classloader.classfileparser.attribute.CodeAttribute;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Method extends ClassMember {
    private int maxStack;
    private int maxLocal;
    private int argc;
    private byte[] code;

    public Method(MethodInfo info, JClass clazz) {
        this.clazz = clazz;
        accessFlags = info.getAccessFlags();
        name = info.getName();
        descriptor = info.getDescriptor();

        CodeAttribute codeAttribute = info.getCodeAttribute();
        if (codeAttribute != null) {
            maxLocal = codeAttribute.getMaxLocal();
            maxStack = codeAttribute.getMaxStack();
            code = codeAttribute.getCode();
        }
        argc = calculateArgcFromDescriptor(descriptor);
    }

    //todo calculateArgcFromDescriptor
    private int calculateArgcFromDescriptor(String descriptor) {
        /**
         * Add some codes here.
         * Here are some examples in README!!!
         *
         * You should refer to JVM specification for more details
         *
         * Beware of long and double type
         */
        int argc = 0;
        int index;
        index = descriptor.indexOf('(') + 1;
       while(index < descriptor.indexOf(')')){
           switch(descriptor.charAt(index)){
               case 'J':
               case 'D':
                   argc += 2;
                   break;
               case 'F':
               case 'I':
               case 'B':
               case 'C':
               case 'S':
               case 'Z':
                   argc ++;
                   break;
               case 'L':
                   argc ++;
                   for(; index < descriptor.indexOf(')'); index ++){
                       if(descriptor.charAt(index) == ';'){
                           break;
                       }
                   }
               case '[':
                   break;
               default:
                   throw new UnsupportedOperationException();
           }
           index ++;
       }
       return argc;
    }
}