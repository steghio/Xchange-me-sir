package com.groglogs.sample.exchange.pojo;

public class Xrate {

    private String currency, rate, date, error;

    public Xrate(String currency, String rate, String date){
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    public Xrate(String currency, String rate){
        this.currency = currency;
        this.rate = rate;
        this.date = null;
    }

    public Xrate(String error){
        this.currency = null;
        this.rate = null;
        this.date = null;
        this.error = error;
    }

    public String getCurrency(){
        return this.currency;
    }

    public String getRate(){
        return this.rate;
    }

    public String getDate(){
        return this.date;
    }

    public String getError(){
        return this.error;
    }
}
