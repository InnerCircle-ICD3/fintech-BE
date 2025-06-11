package com.fastcampus.paymentmethod.entity;

public enum UseYn {

    Y ("Y"),
    N ("N");

    private String yn;

    UseYn(String yn) {
        this.yn = yn;
    }

    public String getYn() {
        return yn;
    }
}
