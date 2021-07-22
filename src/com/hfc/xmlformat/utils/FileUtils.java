package com.hfc.xmlformat.utils;

import com.hfc.xmlformat.ChineseCharToEn;
import com.hfc.xmlformat.Logger;
import com.hfc.xmlformat.interfaces.DimenXmls;
import com.hfc.xmlformat.interfaces.FileUitilInterface;
import com.hfc.xmlformat.interfaces.StringXmls;
import com.intellij.openapi.vfs.VirtualFile;
import groovyjarjarantlr.PreservingFileWriter;
import org.apache.http.util.TextUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtils implements FileUitilInterface {
    private String stringpath=null;
    private String dimenpath=null;
    private VirtualFile layoutFile=null;
    private  File stringFile=null;
    private  File dimenFile =null;
    public void init(VirtualFile file){
        String resPath= isLayout(file);

        layoutFile =file;
        if (resPath!=null){

            stringpath=resPath.concat("/").concat("values").concat("/").concat("strings.xml");
            dimenpath =(resPath +"/").concat("values").concat("/").concat("dimens.xml");
        }
         stringFile = new File(stringpath);
         dimenFile  = new File(dimenpath);
        if (!dimenFile.exists()){
            try {
                dimenFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!stringFile.exists()){
            try {
                stringFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //找到根目录，给根据根目录找到string value dimen文件夹
    private String  isLayout(VirtualFile file){
        System.out.println(file.getPath());
//        Logger.error(file.getPath());
        if (file==null){
            return null;
        }else if (file.isDirectory()&&file.getPath().endsWith("res")){
            return file.getPath();
        }else {
            return isLayout(file.getParent());
        }
    }
    //"@string/"+
    @Override
    public String valueToKey(@NotNull String value) {

        return ChineseCharToEn.getAllFirstLetter(value);
    }

    @Override
    public String valueChanged(String value) {
        return "@string/"+ChineseCharToEn.getAllFirstLetter(value);
    }

    public String valueChangedDimen(String value) {
        return "@dimen/dimen_"+value;
    }

    public void saveLayouts(Document document) {
            XMLOutputter out = new XMLOutputter();
//            FileWriter writer = new FileWriter(xmlPath);
//            Format format = Format.getPrettyFormat();
//            out.setFormat(format);
//            out.output(document, ((Writer) writer));
//            writer.close();

    }

    @Override
    public String getLayoutFilePath() {
        return null;
    }

    @Override
    public VirtualFile getLayoutFile() {
        return layoutFile;
    }

    @Override
    public String getStringFilePath() {
        return stringpath;
    }

    @Override
    public File getStringFile() {
        return null;
    }

    @Override
    public String getDimeonFilePath() {
        return dimenpath;
    }

    @Override
    public File getDimeonFile() {
        return null;
    }

    @Override
    public String replaceDimenAndString(StringXmls stringValues, DimenXmls dimenValues, VirtualFile file) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        Document doc =getDocument(new File(file.getPath()));
        NodeList nl = doc.getElementsByTagName("*");
        //5、遍历NodeList 获取里面的元素信息
        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            //获取属性的元素节点
            String value = element.getAttribute(getTextKey());
           /* NamedNodeMap namedNodeMap = element.getAttributes();
            for (int j = 0; j < namedNodeMap.getLength(); j++) {
             String va   namedNodeMap.item(j).getNodeValue()
            }*/
//            Logger.error(element.toString());
//            Logger.error(element.getTagName());

            // element.setAttribute(getTextKey(),);
            if (value!=null&&!value.isEmpty()&&!value.startsWith("@")){
                System.out.println(value);
                Map<String,String> stringMap =stringValues.getStringValues();
                if (stringMap.containsValue(value)){
                    element.setAttribute(getTextKey(),"@string/"+getKey(stringMap,value));
                }else {
                    //key1 @string
                    String key1 = valueToKey(value);
                    String androidkey1 = valueChanged(value);
                    stringMap.put(key1,value);
                    element.setAttribute(getTextKey(),androidkey1);
                }

            }
            NamedNodeMap namedNodeMap = element.getAttributes();
            for (int j = 0; j < namedNodeMap.getLength(); j++) {
                String value2 =   namedNodeMap.item(j).getNodeValue();
                String name2 =   namedNodeMap.item(j).getNodeName();
                System.out.println("--------------"+name2+value2+"--------------");
                if (!name2.endsWith("android:text")&&isDimenValue(value2)&&!value2.startsWith("@")){
                    Map<String,String> dimenMap =dimenValues.getDimenValues();
                    if (dimenMap.containsValue(value)){
                        element.setAttribute(name2,"@dimen/"+getKey(dimenMap,value2));
                    }else {
                        String key2 = "dimen_"+value2;
                        String androidkey2 = valueChangedDimen(value2);
                        dimenMap.put(key2,value2);
                        element.setAttribute(name2,androidkey2);
                    }
                }
            }


        }
        StringWriter stringWriter =  new StringWriter();
        StreamResult result1 = new StreamResult(stringWriter);
        doc.setXmlStandalone(true);
        DOMSource source2 = new DOMSource(doc);
        transformer.setOutputProperty("encoding","GB23121");//解决中文问题，试过用GBK不行
        transformer.transform(source2, result1);
        String xmlString = result1.getWriter().toString().replaceAll("\\r\\n","");
        return xmlString;
    }
    private String getTextKey(){
        return "android:text";
    }
    private boolean isDimenValue(String value) {
        if(TextUtils.isEmpty(value))
            return false;
        return value.endsWith("dp") || value.endsWith("in")|| value.endsWith("mm")|| value.endsWith("pt")|| value.endsWith("px")|| value.endsWith("sp");
    }
    private boolean isColorValue(String value) {
        if(TextUtils.isEmpty(value))
            return false;
        return value.startsWith("#")&&(value.length()==8||value.length()==7);
    }
    public static  Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException {
        //1、通过DOM解析器获取一个工厂实例
        DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
        //2、通过工厂对象获取解析器对象
        DocumentBuilder db=dbf.newDocumentBuilder();
        //3、使用这个解析器对象获取xml文件
        Document doc=db.parse(file);
        return doc;
    }
    public static String getKey(Map<String,String> map, Object value){
        String keyname =null;
        for(Object key: map.keySet()){
            if(map.get(key).equals(value)){
                keyname=((String) key);
            }
        }
        return keyname;
    }
}
