package com.king.baseutils.custom.bean;

/**
 * 悬浮子菜单实体Bean
 * Created by NaiveKing on 2021/06/25.
 */
public class DragMenu {

    private int menuName;

    private int menuIcon = -1;

    public DragMenu() {
    }

    public DragMenu(int menuName) {
        this.menuName = menuName;
        
    }

    public DragMenu(int menuName, int menuIcon) {
        this.menuName = menuName;
        this.menuIcon = menuIcon;
    }

    public int getMenuName() {
        return menuName;
    }

    public void setMenuName(int menuName) {
        this.menuName = menuName;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }
}