package com.groglogs.sample.exchange.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

public class XrateLoader {
    private static final String resource_path = XrateLoader.class.getClassLoader().getResource(".").getFile();

    private static final Logger log = LoggerFactory.getLogger(XrateLoader.class);
    private static final String daily_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml", hist_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    private static final String daily_path = resource_path + "/eurofxref-daily.xml", hist_path = resource_path + "/eurofxref-hist-90d.xml";

    public static HashMap<String, String> xrates;
    public static HashMap<String, HashMap<String, String>> hist_xrates;

    //Download the XML files from the specific URL and store them in the resources folder
    private static void downloadXRatesFromURL(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    private static void downloadXrates(){
        try {
            downloadXRatesFromURL(daily_URL, daily_path);
        }catch(Exception e){
            log.debug("Error getting daily Xrate file: " + e.getMessage());
        }

        try {
            downloadXRatesFromURL(hist_URL, hist_path);
        }catch(Exception e){
            log.debug("Error getting history Xrate file: " + e.getMessage());
        }
    }

    //parse the Xrate files and populate the hashmaps
    private static void parseXrates(){
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document document;
        NodeList nodeList;
        //daily
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(new File(daily_path));
            //file is Cube->Cube time->list of Cube currency, rate
            nodeList = document.getElementsByTagName("Cube");
            for (int x = 0, size = nodeList.getLength(); x < size; x++) {
                Node n_currency = nodeList.item(x).getAttributes().getNamedItem("currency");
                Node n_rate = nodeList.item(x).getAttributes().getNamedItem("rate");
                //only consider the nodes with the currency and rate
                if (n_currency != null) {
                    xrates.put(n_currency.getNodeValue(), n_rate.getNodeValue());
                }
            }
        } catch(Exception e){
            log.debug("Error parsing daily Xrate file: " + e.getMessage());
        }
        //history
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(new File(hist_path));
            //file is Cube->Cube time->list of Cube currency, rate
            nodeList = document.getElementsByTagName("Cube");
            String time = "";
            HashMap<String, String> tmp = new HashMap<>();
            for (int x = 0, size = nodeList.getLength(); x < size; x++) {
                //get the current time for this batch
                Node n_time = nodeList.item(x).getAttributes().getNamedItem("time");
                if (n_time != null) {
                    //as soon as we get a new time, update the history map if we have data. Only the first time we set n_time we will have no data
                    if (tmp.size() > 0) {
                        hist_xrates.put(time, tmp);
                        tmp = new HashMap<>();
                    }
                    //store the current new time
                    time = n_time.getNodeValue();

                }
                //keep updating the temporary hashmap for this batch
                Node n_currency = nodeList.item(x).getAttributes().getNamedItem("currency");
                Node n_rate = nodeList.item(x).getAttributes().getNamedItem("rate");
                if (n_currency != null) {
                    tmp.put(n_currency.getNodeValue(), n_rate.getNodeValue());
                }
            }
        } catch(Exception e){
            log.debug("Error parsing history Xrate file: " + e.getMessage());
        }
    }

    private static void loadXrates() throws Exception{
        xrates = new HashMap<>();
        hist_xrates = new HashMap<>();
        downloadXrates();
        parseXrates();
    }

    public static void doLoadXRates(){
        Thread thread = new Thread(){
            public void run(){
                try {
                    loadXrates();
                } catch (Exception e){
                    log.debug("Error starting loader thread: " + e.getMessage());
                }
            }
        };

        thread.start();
    }
}
