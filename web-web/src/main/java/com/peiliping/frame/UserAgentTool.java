package com.peiliping.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;


public class UserAgentTool {


    protected final HashMap<String, Integer>                 INIT_WEIGHT_B = new HashMap<String, Integer>();
    {
        INIT_WEIGHT_B.put(Browser.IE.getName(), 1);
        INIT_WEIGHT_B.put(Browser.CHROME.getName(), 2);
        INIT_WEIGHT_B.put(Browser.FIREFOX.getName(), 3);
        INIT_WEIGHT_B.put(Browser.SAFARI.getName(), 4);
        INIT_WEIGHT_B.put(Browser.OPERA.getName(), 5);
    }

    protected volatile ArrayList<Browser>                    browsers      = new ArrayList<Browser>();
    {
        for (Browser b : Browser.values()) {
            browsers.add(b);
        }
        Collections.sort(browsers, new Comparator<Browser>() {
            @Override
            public int compare(Browser o1, Browser o2) {
                Integer t1 = INIT_WEIGHT_B.get(o1.getGroup().getName()) == null ? Integer.MAX_VALUE : INIT_WEIGHT_B.get(o1.getGroup().getName());
                Integer t2 = INIT_WEIGHT_B.get(o2.getGroup().getName()) == null ? Integer.MAX_VALUE : INIT_WEIGHT_B.get(o2.getGroup().getName());
                return t1.compareTo(t2);
            }
        });
    }

    protected final HashMap<String, Integer>                 INIT_WEIGHT_O = new HashMap<String, Integer>();
    {
        INIT_WEIGHT_O.put(OperatingSystem.WINDOWS.getName(), 1);
        INIT_WEIGHT_O.put(OperatingSystem.ANDROID.getName(), 2);
        INIT_WEIGHT_O.put(OperatingSystem.IOS.getName(), 3);
        INIT_WEIGHT_O.put(OperatingSystem.MAC_OS_X.getName(), 4);
        INIT_WEIGHT_O.put(OperatingSystem.MAC_OS.getName(), 5);
        INIT_WEIGHT_O.put(OperatingSystem.LINUX.getName(), 6);
    }
    protected volatile ArrayList<OperatingSystem>            oss           = new ArrayList<OperatingSystem>();
    {
        for (OperatingSystem os : OperatingSystem.values()) {
            oss.add(os);
        }
        Collections.sort(oss, new Comparator<OperatingSystem>() {
            @Override
            public int compare(OperatingSystem o1, OperatingSystem o2) {
                Integer t1 = INIT_WEIGHT_O.get(o1.getGroup().getName()) == null ? Integer.MAX_VALUE : INIT_WEIGHT_O.get(o1.getGroup().getName());
                Integer t2 = INIT_WEIGHT_O.get(o2.getGroup().getName()) == null ? Integer.MAX_VALUE : INIT_WEIGHT_O.get(o2.getGroup().getName());
                return t1.compareTo(t2);
            }
        });
    }

    protected ConcurrentHashMap<Browser, AtomicLong>         WEIGHT_B      = new ConcurrentHashMap<Browser, AtomicLong>();
    protected ConcurrentHashMap<OperatingSystem, AtomicLong> WEIGHT_O      = new ConcurrentHashMap<OperatingSystem, AtomicLong>();
    {
        initWeightMap();
    }

    @Setter
    @Getter
    private long                                             autoSortTime  = 1000 * 60 * 60;

    private boolean                                          needAutoSort  = false;

    private Timer                                            timer;

    private static final long                                LIMIT         = Long.MAX_VALUE - Long.MAX_VALUE / 10000;

    public UserAgentTool() {
        if (autoSortTime > 60000) {
            needAutoSort = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sortBrowsers();
                    sortOS();
                    initWeightMap();
                }
            }, autoSortTime, autoSortTime);
        }
    }

    public UserAgent parseUserAgentString(String userAgentString) {
        Browser browser = Browser.parseUserAgentString(userAgentString, browsers);
        OperatingSystem operatingSystem = OperatingSystem.UNKNOWN;
        if (browser != Browser.BOT) {
            operatingSystem = OperatingSystem.parseUserAgentString(userAgentString, oss);
        }
        if (needAutoSort) {
            if (browser != Browser.UNKNOWN && WEIGHT_B.get(browser).get() < LIMIT) {
                WEIGHT_B.get(browser).addAndGet(1);
            }
            if (operatingSystem != OperatingSystem.UNKNOWN && operatingSystem != OperatingSystem.UNKNOWN_MOBILE && operatingSystem != OperatingSystem.UNKNOWN_TABLET
                    && (WEIGHT_O.get(operatingSystem).get() < LIMIT)) {
                WEIGHT_O.get(operatingSystem).addAndGet(1);
            }
        }
        return new UserAgent(operatingSystem, browser);
    }

    private void initWeightMap() {
        for (Browser b : Browser.values()) {
            WEIGHT_B.put(b, new AtomicLong());
        }
        for (OperatingSystem os : OperatingSystem.values()) {
            WEIGHT_O.put(os, new AtomicLong());
        }
    }

    private void sortBrowsers() {
        ArrayList<Browser> replace = new ArrayList<Browser>(browsers);
        Collections.sort(replace, new Comparator<Browser>() {
            @Override
            public int compare(Browser o1, Browser o2) {
                Long a = WEIGHT_B.get(o1).get();
                Long b = WEIGHT_B.get(o2).get();
                return 0 - a.compareTo(b);
            }
        });
        browsers = replace;
    }

    private void sortOS() {
        ArrayList<OperatingSystem> replace = new ArrayList<OperatingSystem>(oss);
        Collections.sort(replace, new Comparator<OperatingSystem>() {
            @Override
            public int compare(OperatingSystem o1, OperatingSystem o2) {
                Long a = WEIGHT_O.get(o1).get();
                Long b = WEIGHT_O.get(o2).get();
                return 0 - a.compareTo(b);
            }
        });
        oss = replace;
    }

    public static void main(String[] args) {
        UserAgentTool uae = new UserAgentTool();
        String s = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
        int i = 0;
        while (i++ < 10000) {
            uae.parseUserAgentString(s);
        }
        uae.sortBrowsers();
        uae.sortOS();
        long t = System.currentTimeMillis();
        i = 0;
        while (i++ < 20000) {
//             uae.parseUserAgentString(s); //新的
             UserAgent.parseUserAgentString(s);//旧的
        }
        System.out.println(System.currentTimeMillis() - t);
    }
}
