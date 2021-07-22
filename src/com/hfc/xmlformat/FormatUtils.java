package com.hfc.xmlformat;

import org.apache.http.util.TextUtils;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class FormatUtils {
    private static LinkedHashMap<String,String> stringKeyValuesMap = new LinkedHashMap<String,String>();

    public static void getfile(String filePath) throws IOException, SAXException, ParserConfigurationException {
        // 1、创建 File 对象，映射 XML 文件
          File file = new File(filePath);
        // 2、创建 DocumentBuilderFactory 对象，用来创建 DocumentBuilder 对象
         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // 3、创建 DocumentBuilder 对象，用来将 XML 文件 转化为 Document 对象
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // 4、创建 Document 对象，解析 XML 文件
         Document document = documentBuilder.parse(file);
    }
    public static void initMapUseSax(File xmlFILE) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 2.创建解析器
        SAXParser parser = factory.newSAXParser();
        // 3.获取需要解析的文档，生成解析器,最后解析文档
        SaxHandler dh = new SaxHandler();
        parser.parse(xmlFILE, dh);
    }
    static int i =0 ;
    static class SaxHandler extends DefaultHandler {

        private String currentKey;

        /* 此方法有三个参数
                   arg0是传回来的字符数组，其包含元素内容
                   arg1和arg2分别是数组的开始位置和结束位置 */
        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            String content = new String(arg0, arg1, arg2);
            System.out.println("解析:1"+currentKey+"1");
            super.characters(arg0, arg1, arg2);
            if(!TextUtils.isEmpty(currentKey))
               stringKeyValuesMap.put(currentKey,content);
            else
                System.out.println("空:{"+(i++)+"}");
            currentKey = "";
        }

        @Override
        public void endDocument() throws SAXException {
            System.out.println("\n…………结束解析文档…………");
            super.endDocument();
        }

        /* arg0是名称空间
           arg1是包含名称空间的标签，如果没有名称空间，则为空
           arg2是不包含名称空间的标签 */
        @Override
        public void endElement(String arg0, String arg1, String arg2)
                throws SAXException {
            System.out.println("结束解析元素  " + arg2);
            super.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startDocument() throws SAXException {
            System.out.println("…………开始解析文档…………\n");
            super.startDocument();
        }

        /*arg0是名称空间
          arg1是包含名称空间的标签，如果没有名称空间，则为空
          arg2是不包含名称空间的标签
          arg3很明显是属性的集合 */
        @Override
        public void startElement(String arg0, String arg1, String arg2,
                                 Attributes arg3) throws SAXException {
            System.out.println("开始解析元素 " + arg2);
            if (arg3 != null) {
                for (int i = 0; i < arg3.getLength(); i++) {
                    // getQName()是获取属性名称，
                    System.out.print("arg3"+arg3.getQName(i) + "=\"" + arg3.getValue(i) + "\"");
                    if(arg3.getQName(i).equals("name")){
                        currentKey = arg3.getValue(i);
                        System.out.println("currentKey "+arg3.getQName(i));
                    }

                }
            }
            System.out.print(arg2 + ":");
            super.startElement(arg0, arg1, arg2, arg3);
        }
    }

}
