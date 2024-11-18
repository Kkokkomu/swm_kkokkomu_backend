package com.kkokkomu.short_news.core.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class RSSUtil {
    public String getNewsisHotNewsUrl() {
        try {
            // 뉴시스 RSS URL
            String rssUrl = "https://www.newsis.com/RSS/sokbo.xml";
            URL url = new URL(rssUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream inputStream = connection.getInputStream();

            // Parse the XML response
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            doc.getDocumentElement().normalize();

            // Get the first <item> element's <link> value
            NodeList itemNodes = doc.getElementsByTagName("item");
            if (itemNodes.getLength() > 0) {
                NodeList linkNodes = ((org.w3c.dom.Element) itemNodes.item(0)).getElementsByTagName("link");
                if (linkNodes.getLength() > 0) {
                    return linkNodes.item(0).getTextContent().trim();
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
