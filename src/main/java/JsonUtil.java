/////////////////////////////////////////////////////////
//
// File: JsonUtil.java
// Description: Source code of JsonUtil
//
//
// Copyright (c) 20115 by Thomson Reuters. All rights reserved.
//
// No portion of this software in any form may be used or
// reproduced in any manner without written consent from
// Thomson Reuters
//



import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import com.codesnippets4all.json.types.CollectionTypes;

import com.google.gson.*;

import java.util.*;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class JsonUtil {

    public static String prettifyJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        String pretty = gson.toJson(parser.parse(json));
        return pretty;
    }

    public static String toPrettyJson(Map map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(map);
        return json;
    }

    public static String toJson(Map map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    public static String toJson(Map map, boolean isPretty) {
        if(isPretty) {
            return toPrettyJson(map);
        } else {
            return toJson(map);
        }
    }

    public static Map<String, Object> fromJson(String json) {
        Gson gson = new Gson();
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map = gson.fromJson(json, map.getClass());
        return map;
    }


    public static boolean compareJson(String json1, String json2) {
        Map<String, Object> j1 = fromJson(json1);
        Map<String, Object> j2 = fromJson(json2);
        boolean is = j1.equals(j2);
        return is;
    }


    // the map should be specially formatted:
    // 0. only one outer most key
    // 1. `_attrs` nodes for attributes
    // 2. `_value` for value
    public static Document toXmlDoc(String json) throws Exception {
        // this parse all json values into string
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser(new CollectionTypes() {
            @Override
            public Map getMapType() {
                return new LinkedHashMap();
            }

            @Override
            public List getListType() {
                return new LinkedList();
            }
        });
        Map map = parser.parseJson(json);
        return toXmlDoc(map);
    }

    // the map should be specially formatted:
    // 0. only one key and all nested keys should be string
    // 1. `_attrs` nodes for attributes
    // 2. `_value` for value
    public static Document toXmlDoc(Map map) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Set keys = map.keySet();
        if(keys.size() > 1) {
            throw new Exception("only one key allowed in input map, current keys: " + keys.size());
        }
        String key = (String)keys.iterator().next();
        Node node = toXmlNode(doc, key, map.get(key));
        doc.appendChild(node);
        return doc;
    }

    private static Node toXmlNode(Document doc, String key, Object val) {
        Element el = doc.createElement(key);
        Map m = (Map)val;
        for(Object k : m.keySet()) {
            Object v = m.get(k);
            if(v == null || ((v instanceof String) && ((String)v).equals("null"))) {
                continue;
            }
            //if(k.equals("_attrs")) {
            if(k.equals(NormConstant.Attrs)) {
                for(Map.Entry<Object, Object> entry : ((Map<Object, Object>)v).entrySet()) {
                    Attr attr = doc.createAttribute(entry.getKey().toString());
                    attr.setValue(entry.getValue().toString());
                    el.setAttributeNode(attr);
                }
            } else if(k.equals(NormConstant.Value)) {
                Text text = doc.createTextNode(v.toString());
                el.appendChild(text);
            } else {
                Node n = toXmlNode(doc, k.toString(), v);
                el.appendChild(n);
            }
        }
        return el;
    }

}
