package com.tutorial;

import android.util.Log;

import com.tutorial.util.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * XML Parser.
 *
 * Created by lihg on 13-7-5.
 */
public class XMLParser {
    /**
     * Gets XML from URI by HTTP request.
     *
     * @param url XML file URL
     * @return content of XML
     */
    public String getXmlFromUrl(String url) {
        String xml = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            xml = EntityUtils.toString(entity, "UTF-8");
            return xml;
        } catch (ClientProtocolException cpe) {
            Log.e(LogUtils.LOG_ID, cpe.getMessage());
        } catch (IOException ioe) {
            Log.e(LogUtils.LOG_ID, ioe.getMessage());
        }
        return xml;
    }

    /**
     * Gets XML DOM element.
     *
     * @param xml xml content
     * @return document describe this xml
     */
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException pce) {
            Log.e(LogUtils.LOG_ID, pce.getMessage());
        } catch (SAXException saxe) {
            Log.e(LogUtils.LOG_ID, saxe.getMessage());
        } catch (IOException ioe) {
            Log.e(LogUtils.LOG_ID, ioe.getMessage());
        }
        return doc;
    }

    /**
     * Gets node value.
     *
     * @param elem an element node
     * @return value for this element
     */
    public final String getElementValue(Node elem) {
        if (elem != null && elem.hasChildNodes()) {
            for (Node child = elem.getFirstChild(); child != null; child = elem.getNextSibling()) {
                if (child.getNodeType() == Node.TEXT_NODE)
                    return child.getNodeValue();
            }
        }
        return "";
    }

    public String getValue(Element item, String tagName) {
        NodeList nodeList = item.getElementsByTagName(tagName);
        return getElementValue(nodeList.item(0));
    }
}
