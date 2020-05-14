package com.scrcu.ebank.ebap.batch.common.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class EbapFileFilter implements FileFilter
{
	private final Pattern pattern;

	public EbapFileFilter(String pattern)
	{
		if (pattern == null)
		{
			throw new IllegalArgumentException("Pattern is missing");
		} 
		else
		{
			this.pattern = Pattern.compile(pattern);
			return;
		}
	}


	public EbapFileFilter(Pattern pattern)
	{
		if (pattern == null)
		{
			throw new IllegalArgumentException("Pattern is missing");
		} 
		else
		{
			this.pattern = pattern;
			return;
		}
	}

	public boolean accept(File file)
	{
		if(file.isDirectory())
		{
			return false;
		}
		String fileName = file.getName();
		return pattern.matcher(fileName).matches();
	}
	
}
