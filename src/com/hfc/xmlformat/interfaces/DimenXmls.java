package com.hfc.xmlformat.interfaces;

import java.util.Map;

public interface DimenXmls {

    //获取values 中strings的所有值
    Map<String,String> getDimenValues();
    //添加key-value
    void addDimenValue(String key, String value);
    //存在的话返回key
    String iscontainsDimenValue(String value);
    //保存strings
    void saveMaps();

}
