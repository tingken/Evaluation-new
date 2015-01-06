/**
 * 
 */
package com.evaluation.protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author HP
 *
 */
public class DataHelper {
    
    public static byte BEGIN_BYTE = (byte) 0xa5;
    public static byte END_BYTE = (byte) 0x5a;

    public static PayLoad read(InputStream in) throws IOException{
        boolean end = false;
        PayLoad payLoad = new PayLoad();
        Section section = Section.BEGIN_FLAG;
        while(!end){
            byte current = (byte) in.read();
            if(current == BEGIN_BYTE){
                section = Section.TYPE;
                continue;
            }
            if(section.pos() > Section.BEGIN_FLAG.pos()){
                if(current == END_BYTE){
                    end = true;
                    break;
                }
                switch(section){
                case TYPE:
                    payLoad.setType(DataType.fromInt(current));
                    section = Section.DATA;
                    break;
                case DATA:
                    payLoad.setData(current);
                    section = Section.END_FLAG;
                    break;
                case END_FLAG:
                    // must not be here
                    throw new IOException("Data Exception with a error ending;");
                default:
                    break;
                }
            }
        }
        return payLoad;
    }
}
