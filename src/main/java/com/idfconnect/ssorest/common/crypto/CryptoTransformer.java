package com.idfconnect.ssorest.common.crypto;

import javassist.*;
import nl.topicus.plugins.maven.javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

/**
 * <p>CryptoTransformer class.</p>
 *
 * @author nghia
 */
public class CryptoTransformer extends ClassTransformer{

    private              Logger logger  =  LoggerFactory.getLogger(CryptoTransformer.class);
    private static final int MAX_LENGTH =  26;  //PASSWORD is constant 26 bytes array

    /** {@inheritDoc} */
    @Override public void applyTransformations(ClassPool classPool, CtClass ctClass) throws TransformationException {

        try {
            CtField plainTextPasswordField = ctClass.getDeclaredField("PLAIN_TEXT_PASSWORD");
            String password = plainTextPasswordField.getConstantValue().toString();
            logger.info("CryptoTransformer: plain text password = " + password);

            String sourceField ="private static final byte[] PASSWORD   = new byte[] {";
            byte[] bytes = password.getBytes(Charset.forName("UTF-8"));

            byte[] full_bytes= new byte[MAX_LENGTH];

            Random rn = new Random();
            int min =2;
            int max = MAX_LENGTH - bytes.length;
            int startIndex = rn.nextInt(max - min + 1) + min;

            //first 2 bytes store start and end index of actual data
            //other bytes will be dummy
            full_bytes[0]=(byte) startIndex;
            full_bytes[1] =(byte) (startIndex + bytes.length - 1);
            int i =2;

            for (i = 2;i<full_bytes[0];i++ ) {
                full_bytes[i] = (byte)rn.nextInt(127);
            }
            for (i = full_bytes[0];i<=full_bytes[1];i++ ) {
                full_bytes[i] = bytes[i-full_bytes[0]];
            }
            for (i = full_bytes[1] + 1;i<26;i++ ) {
                full_bytes[i] = (byte)rn.nextInt(127);
            }
            //XOR logic on mask window
            byte[] mask = {1,12,120};
            int j =0;
            for(i =0;i<full_bytes.length;i++){
                full_bytes[i] = (byte)(full_bytes[i] ^ mask[j]);
                if(j<2) {
                    j++;}
                else{
                    j=0;
                }
            }

            String bStr = Arrays.toString(full_bytes);
            sourceField = sourceField + bStr.substring(1,bStr.length()-1) +  "};";

            logger.info("CryptoTransformer: new field = " + sourceField);

            // and PASSWORD byte field
            CtField newField = CtField.make(sourceField,ctClass);
            ctClass.addField(newField);

            //remove plain text field
            ctClass.removeField(plainTextPasswordField);

            //Now is about building getPassword() method, remember to use the same logic and mask window as above
            StringBuilder sourceMethod =
            new StringBuilder("public static final String getPassword() {"
                           + "   byte[] mask = {1,12,120}; ");

            sourceMethod.append(" int j =0; ");
            sourceMethod.append(" byte[] bytes = new byte[PASSWORD.length]; ");
            sourceMethod.append(" for(int i =0;i<PASSWORD.length;i++){ ");
            sourceMethod.append("     bytes[i] = (byte)(PASSWORD[i] ^ mask[j]); ");
            sourceMethod.append("     if(j<2){");
            sourceMethod.append("       j++;"
                                  + " }else{");
            sourceMethod.append("       j=0;");
            sourceMethod.append("     }");
            sourceMethod.append(" }");
            sourceMethod.append(" byte[] abytes = new byte[bytes[1]-bytes[0] + 1]; ");
            sourceMethod.append(" System.arraycopy(bytes,bytes[0],abytes,0,abytes.length); ");

            sourceMethod.append(" return new String(abytes);"
                            + " };");

            CtMethod oldMethod = ctClass.getDeclaredMethod("getPassword");
            ctClass.removeMethod(oldMethod);

            CtMethod getPasswordMethod = CtNewMethod.make(sourceMethod.toString(), ctClass);
            ctClass.addMethod(getPasswordMethod);

            logger.info("Begin  " +  ctClass.getName());
            logger.info(sourceField);
            logger.info(sourceMethod.toString());
            logger.info("End  " +  ctClass.getName());

        }catch (Throwable ex){
            logger.error("Unable to transform class = " + ctClass.getName(),ex);
            throw new RuntimeException("Unable to transform class = " + ctClass.getName());
        }


    }
}
