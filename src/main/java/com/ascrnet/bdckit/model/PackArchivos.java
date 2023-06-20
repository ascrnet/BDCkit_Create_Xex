package com.ascrnet.bdckit.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PackArchivos {

	Map<String, String> packfile = new HashMap <String, String>();

	public Boolean add(String arch, String ruta)
	{
		if (!packfile.containsKey(arch))
		{
			packfile.put(arch,ruta);
			return true;
		}
		return false;
	}	
	
	public Map<String, String> get(){
		Map<String, String> sorted = new TreeMap<>();
		sorted.putAll(packfile);
		return sorted;
	}
	
	public Integer size() {
		return packfile.size();
	}
	
	public void clean() 
	{
		 packfile.clear();
	}

}
