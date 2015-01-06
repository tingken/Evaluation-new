/**
 * 
 */
package com.evaluation.protocol;

/**
 * @author ahanqiankun@aliyun.com
 *
 */
public enum DataType {

    OPEN_CONNECTION(1),
    HEART_BEAT(2),
    CLOSE_CONNECTION(3),
    APPLY_EVALUATE(4),
    EVALUATE_RESULT(5),
    LEAVE_INFO(6),
    BACK_INFO(7),
    RESPONSE(8);
    
    private int flag;
    DataType(int flag){
        this.flag = flag;
    }
    
    public byte Flag(){
        return (byte) flag;
    }
    
    public static DataType fromInt(int flag){
        for(DataType type : DataType.values()){
            if(type.Flag() == flag){
                return type;
            }
        }
        return null;
    }
}
