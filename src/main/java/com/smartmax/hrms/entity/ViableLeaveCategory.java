package com.smartmax.hrms.entity;

public class ViableLeaveCategory {
    private String categoryName;
    private String icon;
    private int remainingDays;
    private String rotate;
    public ViableLeaveCategory(String categoryName,String icon,int remainingDays,String rotate){
        this.categoryName = categoryName;
        this.icon = icon;
        this.remainingDays= remainingDays;
        this.rotate =  rotate;
    }
    public ViableLeaveCategory(){super();}

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public int getRemainingDays() {
        return remainingDays;
    }
    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
    public String getRotate(){
        return rotate;
    }
    public void setRotate(String rotate){
        this.rotate =  rotate;
    }

    @Override
    public String toString() {
        return "ViableLeaveCategory{" +
                "categoryName='" + categoryName + '\'' +
                ", remainingDays=" + remainingDays +
                '}';
    }
}
