package com.zerra.api.mod.info;

import java.io.FileInputStream;

import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.zerra.common.util.JsonWrapper;

public class ModInfoBuilder {

    public static String[] optionalJsonKeys = {
            "modDescription",
            "websiteURL",
            "credits",
            "authors",
            "dependencies"
    };

    public static String[] requiredJsonKeys = {
            "domain",
            "modname",
            "modVersion",
            "zerraVersion"
    };

    private static Logger logger = Logger.getLogger("ModInfoBuilder");

    private String domain;

    private String modName;

    private String modVersion;

    private String zerraVersion;

    private String modDescription;

    private String websiteURL;

    private String credits;

    private String[] authors;

    private String[] dependencies;

    public ModInfoBuilder(String domain, String modName, String modVersion, String zerraVersion) {
        this.domain = domain;
        this.modName = modName;
        this.modVersion = modVersion;
        this.zerraVersion = zerraVersion;
    }

    @Nullable
    public static ModInfo fromFile(FileInputStream fileInputStream) {
        JsonWrapper jsonWrapper = new JsonWrapper(fileInputStream);
        JsonObject object = jsonWrapper.getJson();
        boolean flag = true;
        for (String key : requiredJsonKeys) {
            flag &= object.has(key);
        }
        if (!flag) {
            logger.error("The file doesn't contain the required parameters");
            jsonWrapper.close();
            return null;
        }

        String domain = jsonWrapper.getString("domain");
        String modName = jsonWrapper.getString("modName");
        String modVersion = jsonWrapper.getString("modVersion");
        String zerraVersion = jsonWrapper.getString("zerraVersion");

        ModInfoBuilder builder = new ModInfoBuilder(domain, modName, modVersion, zerraVersion);

        for (String key : optionalJsonKeys) {
            if (object.has(key)) {
                builder.set(key, (String[]) jsonWrapper.get(key));
            }
        }
        jsonWrapper.close();
        return builder.build();
    }

    public ModInfoBuilder set(String name, String... value) {
        if (value.length < 1) {
            return this;
        }

    	String joined = String.join(" ", value);
    	
        switch (name) {
            case "modDescription":
                return setModDescription(joined);
            case "websiteURL":
                return setWebsiteURL(joined);
            case "credits":
                return setCredits(joined);
            case "authors":
                return setAuthors(value);
            case "dependencies":
                return setDependencies(value);
            default:
                return this;
        }

    }

    public ModInfoBuilder setModDescription(String modDescription) {
        this.modDescription = modDescription;
        return this;
    }

    public ModInfoBuilder setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
        return this;
    }

    public ModInfoBuilder setCredits(String credits) {
        this.credits = credits;
        return this;
    }

    public ModInfoBuilder setAuthors(String... authors) {
        this.authors = authors;
        return this;
    }

    public ModInfoBuilder setDependencies(String... dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public ModInfo build() {

        return new ModInfo() {
            @Override
            public String getDomain() {
                return domain;
            }

            @Override
            public String getModName() {
                return modName;
            }

            @Override
            public String getModVersion() {
                return modVersion;
            }

            @Override
            public String getZerraVersion() {
                return zerraVersion;
            }

            @Override
            public String[] getAuthors() {
                return authors;
            }

            @Override
            public String[] getDependencies() {
                return dependencies;
            }

            @Override
            public String getModDescription() {
                return modDescription;
            }

            @Override
            public String getWebsiteURL() {
                return websiteURL;
            }

            @Override
            public String getCredits() {
                return credits;
            }
        };
    }
}
