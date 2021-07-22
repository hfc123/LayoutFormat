package com.hfc.xmlformat.interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface FileUitilInterface {


    String valueToKey(String value);
    String valueChanged(String value);
    void saveLayouts(Document document);
    String getLayoutFilePath();
    VirtualFile getLayoutFile();
    String getStringFilePath();
    File getStringFile();
    String getDimeonFilePath();
    File getDimeonFile();
    String replaceDimenAndString(StringXmls stringValues, DimenXmls dimenValues, VirtualFile file) throws TransformerException, IOException, SAXException, ParserConfigurationException;
}
