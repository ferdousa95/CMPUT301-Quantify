package com.example.quantify;

public class ResultCount {
    private double result;
    private int count;

    public ResultCount(double result, int count) {
        this.result = result;
        this.count = count;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
