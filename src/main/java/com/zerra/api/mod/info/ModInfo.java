package com.zerra.api.mod.info;

public interface ModInfo
{
	String getDomain();

	String getModName();

	String getModVersion();

	String getZerraVersion();

	String[] getAuthors();

	String[] getDependencies();

	String getModDescription();

	String getWebsiteURL();

	String getCredits();
}
