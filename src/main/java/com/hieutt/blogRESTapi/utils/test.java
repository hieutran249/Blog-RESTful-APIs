package com.hieutt.blogRESTapi.utils;

public class test {
    public static void main(String[] args) {
        String tags = "t    s";
        boolean result = tags.matches("^.*[^a-zA-Z0-9 ].*$");
        System.out.println(result);
    }
}
