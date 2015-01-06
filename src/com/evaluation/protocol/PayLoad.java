/**
 * 
 */
package com.evaluation.protocol;

/**
 * @author ahanqiankun@aliyun.com
 *
 */
public class PayLoad {

    private DataType type;
    private int data;
    /**
     * @return the type
     */
    public DataType getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(DataType type) {
        this.type = type;
    }
    /**
     * @return the data
     */
    public int getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(int data) {
        this.data = data;
    }
}
