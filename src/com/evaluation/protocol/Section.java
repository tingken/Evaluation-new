/**
 * 
 */
package com.evaluation.protocol;

/**
 * @author HP
 *
 */
public enum Section {

    BEGIN_FLAG(0),
    TYPE(1),
    DATA(2),
    END_FLAG(3);
    
    private int position;
    Section(int position){
        this.position = position;
    }
    
    public int pos(){
        return position;
    }
    
    public void advance(){
        ++position;
    }
    
    public static Section fromInt(int position){
        for(Section section : Section.values()){
            if(section.pos() == position){
                return section;
            }
        }
        return null;
    }
}
