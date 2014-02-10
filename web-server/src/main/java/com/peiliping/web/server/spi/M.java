package com.peiliping.web.server.spi;

import java.util.ServiceLoader;

public class M {
	
	private static ServiceLoader<Make> serviceLoader = ServiceLoader.load(Make.class);
	
	public static void m (){	
        for (Make make : serviceLoader){
            make.make();
        }
        serviceLoader.reload();
	}
	
}
