package com.king.naiveutils.custom.bean;

/**
 * @author NaiveKing
 * @date 2022/11/23
 */
public class FloatMenuBean {
    private int menuType;

    private String menuName;

    public FloatMenuBean(int menuType, String menuName) {
        this.menuType = menuType;
        this.menuName = menuName;
    }

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
