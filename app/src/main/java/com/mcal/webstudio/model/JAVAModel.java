package com.mcal.webstudio.model;

public class JAVAModel {
    public static String getAttrJAVA(String name) throws Exception {
        String result = "";
        switch (name) {
            case "true":
                result = "boolean";
                break;
            case "false":
                result = "boolean";
                break;
            case "prototype":
                result = "num";
                break;
            case "constructor":
                result = "num";
                break;
            default:
        }
        return result;
    }
}
