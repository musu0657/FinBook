package com.example.finbook;

import java.io.Serializable;

/**
 * 账单数据模型类
 * 实现 Serializable 接口，便于在 Activity 之间通过 Intent 传递
 */
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库自增主键 id */
    private int id;
    /** 收支类型：收入 / 支出 */
    private String type;
    /** 分类：餐饮、交通、购物等 */
    private String category;
    /** 金额 */
    private double amount;
    /** 备注 */
    private String remark;
    /** 记录时间（格式化后的字符串，如 "2026-10-28 10:00"） */
    private String time;

    /** 无参构造方法 */
    public Bill() {
    }

    /** 全参构造方法 */
    public Bill(String type, String category, double amount, String remark, String time) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.remark = remark;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
