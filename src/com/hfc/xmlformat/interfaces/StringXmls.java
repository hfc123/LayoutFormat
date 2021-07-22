package com.hfc.xmlformat.interfaces;

import org.apache.commons.compress.utils.Sets;

import java.util.Map;

public interface StringXmls {

    //获取values 中strings的所有值
    Map<String,String> getStringValues();
    //添加key-value
    void  addValue(String key , String value);
    //存在的话返回key
    String iscontainsValue(String value);
    //保存strings
    void saveMaps();

}
