package com.hfc.xmlformat;

import com.hfc.xmlformat.utils.StringXmlsImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Iterator;

public class FormatTest extends TestCase {


    public void test1() {
//       FormatUtils. initMapUseSax()
        StringXmlsImpl xmls =new StringXmlsImpl();
        try {
            xmls.init("F:/ASProject/ScanDemo/app/src/main/res/layout/activity_main.xml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaxParser() throws Exception {
        //1、通过DOM解析器获取一个工厂实例
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //2、通过工厂对象获取解析器对象
        DocumentBuilder db = dbf.newDocumentBuilder();
        //3、使用这个解析器对象获取xml文件
        Document doc = db.parse("src/com/hfc/xmlformat/file2.xml");


        //4、使用doc操作xml文件,获取元素名是"Brand"的信息
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//initialize StreamResult with File object to save to file
    /*    StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);*/
//        String xmlString = result.getWriter().toString();
       /* System.out.println(xmlString);
        System.out.println(doc.getTextContent());*/
        NodeList nl = doc.getElementsByTagName("*");
        //5、遍历NodeList 获取里面的元素信息
        for (int i = 0; i < nl.getLength(); i++) {
            //5、2 Element是元素
            Element element = (Element) nl.item(i);
            //5、3获取第一个name属性的元素节点
            String value = element.getAttribute(getTextKey());
            // element.setAttribute(getTextKey(),);
            if (value!=null&&!value.isEmpty()){
                System.out.println(value);
                element.setAttribute(getTextKey(),"qqqqqqqqqqqq");
            }
            NamedNodeMap namedNodeMap = element.getAttributes();
            for (int j = 0; j < namedNodeMap.getLength(); j++) {
                String value2 =   namedNodeMap.item(j).getNodeValue();
                String key2 =   namedNodeMap.item(j).getNodeName();
                System.out.println("--------------"+key2+value2+"--------------");
            }

    /*        //5、4获取子级节点列表
            NodeList ChildNodes = element.getElementsByTagName("Type");
            for (int j = 0; j < ChildNodes.getLength(); j++) {
                Element Childelement = (Element) ChildNodes.item(j);
                String Childvalue = Childelement.getAttribute("name");
                System.out.println(value + ":" + Childvalue);
            }*/

        }
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
//        System.out.println(xmlString);
    }

    public void test2() {
        File file = new File("src/com/hfc/xmlformat/file.xml");
        try {
            FileUtils.startSax(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
      /*   String xml =readFileContent("src/com/hfc/xmlformat/file.xml");
             System.out.println(xml);
         String[] _lines = xml.split(lineSeparator());
         for (int i = 0; i < _lines.length; i++) {
             System.out.println("lines"+i+_lines[i]);
         }*/
//             FormatUtils. initMapUseSax(file);

    }

    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                System.out.println("readFileContent: ---" + tempStr + "----");
                if (!(tempStr.trim().equals(""))) {
                    sbf.append(tempStr + "\n");
                }
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    private static String lineSeparator() {
        return "\n";
    }

    private void systemReformat(final AnActionEvent event) {
        event.getActionManager()
                .getAction(IdeActions.ACTION_EDITOR_REFORMAT)
                .actionPerformed(event);
    }

    public void test() throws ParserConfigurationException {
        try {
                // 1.得到DOM解析器的工厂实例
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // 2.从DOM工厂里获取DOM解析器
                DocumentBuilder db = dbf.newDocumentBuilder();
                // 3.解析XML文档，得到document，即DOM树
                Document doc = db.parse("src/com/hfc/xmlformat/file.xml");
                System.out.println("处理该文档的DomImplementation对象  = " + doc.getImplementation());
                //得到文档名称为Student的元素的节点列表
                NodeList nList = doc.getElementsByTagName("Brand");
                //遍历该集合，显示结合中的元素及其子元素的名字
                for (int i = 0; i < nList.getLength(); i++) {
                    Element node = (Element) nList.item(i);
                    System.out.println("Name: " + node.getElementsByTagName("name").item(0));
                    /*System.out.println("Num: " + node.getElementsByTagName("Num").item(0).getFirstChild().getNodeValue());
                    System.out.println("Classes: " + node.getElementsByTagName("Classes").item(0).getFirstChild().getNodeValue());
                    System.out.println("Address: " + node.getElementsByTagName("Address").item(0).getFirstChild().getNodeValue());
                    System.out.println("Tel: " + node.getElementsByTagName("Tel").item(0).getFirstChild().getNodeValue());*/
                 }
        }catch(Exception e){
            // TODO: handle exception
            e.printStackTrace();
        }
/*            System.out.println(doc.getElementsByTagName("Brand").item(1));
            // 创建节点
            Element brandElement = doc.createElement("Brand");
            System.out.println(brandElement.getAttribute("name")+"!");
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
*//*            Transformer transformer=transformerFactory.newTransformer();
            DOMSource domSource=new DOMSource(doc);*//*
            //设置编码类型
           *//* transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            StreamResult result=new StreamResult(new FileOutputStream("src/com/hfc/xmlformat/file.xml"));
            //把DOM树转换为xml文件
            transformer.transform(domSource, result);*//*
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }*/

    }
    private String getTextKey(){
        return "android:text";
    }
}

