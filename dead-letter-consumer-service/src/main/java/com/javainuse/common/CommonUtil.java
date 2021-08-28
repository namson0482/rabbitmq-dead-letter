package com.javainuse.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtil {

    public final static String convertStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();
        return sStackTrace;
    }

    public final static boolean isException(ObjectMapper objectMapper, String json) throws Exception {
        JsonNode parentNode = objectMapper.readTree(json);
        String requestTypCd = parentNode.get("requestTypCd").asText();
        return requestTypCd.equalsIgnoreCase("Exception");
    }
}
