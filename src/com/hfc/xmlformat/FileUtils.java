package com.hfc.xmlformat;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.http.util.TextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class FileUtils {


    public void test(){
        try {
            // 1.得到DOM解析器的工厂实例
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // 2.从DOM工厂里获取DOM解析器
            DocumentBuilder db = dbf.newDocumentBuilder();
            // 3.解析XML文档，得到document，即DOM树
            Document doc = db.parse("src/test.xml");
//            doc.getTextContent()
            // 创建节点
            Element brandElement = doc.createElement("Brand");
            brandElement.setAttribute("name", "华为");
            //创建type节点
            Element typeElement=doc.createElement("Type");
            typeElement.setAttribute("name", "U8650");
            //添加父子关系
            brandElement.appendChild(typeElement);
            Element phoneElement=(Element)doc.getElementsByTagName("PhoneInfo").item(0);
            phoneElement.appendChild(brandElement);
            //保存xml文件
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            DOMSource domSource=new DOMSource(doc);
            //设置编码类型
            transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            StreamResult result=new StreamResult(new FileOutputStream("src/test.xml"));
            //把DOM树转换为xml文件
            transformer.transform(domSource, result);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
    public static  File createFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()){
            file.createNewFile();
        }
        return file;
    }
     Set<String>sets = new HashSet<>();
    //获取value string文件夹
    public static  void getStringPath(VirtualFile file) throws IOException {
       String path = file.getParent().getParent().getPath().concat("/").concat("values").concat("/").concat("strings.xml");
       File file1 = createFile(path);
    }
    //||!value.contains("dp")||!value.contains("sp")||!value.contains("pt")
    public static  Document addElement(Set<String> sets,File file ,String value) throws IOException, SAXException, ParserConfigurationException {
        if (sets.contains(value)){
            return null;
        }
        Document doc =getDocument(file);
        Element element = (Element) doc.getElementsByTagName("resources").item(0);

        Element elementChild = doc.createElement("string");
        elementChild.setAttribute("name", ChineseCharToEn.getAllFirstLetter(value) );
        elementChild.setNodeValue(value);

        element.appendChild(elementChild);

        return doc;
    }
    //保存file
    public void saveStringValuesStrings(File file,Document document) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file,false);//覆盖文件
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
            String strings = getDocumentContent(document);
            outputStreamWriter.write(strings);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

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
    public static  String getDocumentContent(Document document) throws TransformerConfigurationException , TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        System.out.println(xmlString);
        return  xmlString;
    }
    public static void startSax(File file) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 2.创建解析器
        SAXParser parser = factory.newSAXParser();
        // 3.获取需要解析的文档，生成解析器,最后解析文档
        SaxHandler dh = new SaxHandler(file.getPath());
        parser.parse(file, dh);
    }
    public static class SaxHandler extends DefaultHandler {

        private boolean flag = false;
        private TransformerHandler writeHandler;
        private String destination;

        public SaxHandler(String destination) {
            this.destination = destination;
        }

        @Override
        public void startDocument() throws SAXException {
            System.out.println("regenerated uuid: ");

            // prepare
            SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            try {
                this.writeHandler = transformerFactory.newTransformerHandler();
                Transformer transformer = writeHandler.getTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                File file = new File(this.destination);
                if(!file.exists()){
                    file.createNewFile();
                }
                StreamResult streamResult = new StreamResult(new FileOutputStream(file));
                this.writeHandler.setResult(streamResult);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }

            // begin
            this.writeHandler.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            // end
            this.writeHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //@string/
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value  = attributes.getValue(i);
                AttributesImpl attributes1 = (AttributesImpl) attributes;
                if(android("text").equals(key) && !value.startsWith("@")){
                    String mValue ="@string/"+ChineseCharToEn.getAllFirstLetter(value);
                    attributes1.setValue(i,value);

                     this.writeHandler.startElement(uri, localName, key, attributes);
                    this.writeHandler.characters(mValue.toCharArray(), 0, mValue.length());
                    this.writeHandler.endElement(uri, localName, key);
                }
            }

            this.writeHandler.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            this.flag = false;

            this.writeHandler.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

        }
    }

    private static String android(String name) {
        return String.format("android:%s", name);
    }
}
