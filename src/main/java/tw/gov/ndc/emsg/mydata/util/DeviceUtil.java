package tw.gov.ndc.emsg.mydata.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import tw.gov.ndc.emsg.mydata.type.BrowserType;
import tw.gov.ndc.emsg.mydata.type.DeviceType;

public class DeviceUtil {

    public static BrowserType parseBrowser(String userAgent) {
        if(StringUtils.containsAny(userAgent, "MSIE", "Trident")) {
            return BrowserType.IE;
        } else if(StringUtils.contains(userAgent, "Edg")) {
            return BrowserType.Edge;
        } else if(StringUtils.contains(userAgent, "Firefox")) {
            return BrowserType.FireFox;
        } else if(StringUtils.contains(userAgent, "OPR")) {
            return BrowserType.Opera;
        } else if(StringUtils.containsAny(userAgent, "Chrome", "CriOS")) {
            return BrowserType.Chrome;
        } else if(StringUtils.contains(userAgent, "Safari")) {
            return BrowserType.Safari;
        }
        return BrowserType.Other;
    }

    public static DeviceType parseDevice(String userAgent) {
        String lowerUserAgent = userAgent.toLowerCase(Locale.ENGLISH);
        if (StringUtils.contains(lowerUserAgent, "windows")) {
            return DeviceType.Windows;
        } else if(StringUtils.contains(lowerUserAgent, "iphone os")) {
            return DeviceType.IPhone;
        } else if(StringUtils.contains(lowerUserAgent, "mac")) {
            return DeviceType.Mac;
        } else if(StringUtils.contains(lowerUserAgent, "x11")) {
            return DeviceType.Linux;
        } else if(StringUtils.contains(lowerUserAgent, "android")) {
            return DeviceType.Android;
        }
        return DeviceType.Other;
    }
}
