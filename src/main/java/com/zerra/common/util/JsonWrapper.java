package com.zerra.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;

public class JsonWrapper {

    private static final Gson gson = new Gson();
    //means that file is null
    boolean isReadOnly = false;
    private File file;
    private JsonObject json;
    private Reader reader;

    public JsonWrapper(FileInputStream fileInputStream) {
        reader = new InputStreamReader(fileInputStream);
        JsonReader jsonReader = gson.newJsonReader(reader);
        json = gson.fromJson(jsonReader, JsonObject.class);
        isReadOnly = true;
    }

    public JsonWrapper(File file) {
        try {
            FileUtils.touch(file);
            this.file = file;
            FileUtils.touch(this.file);
            this.reader = new FileReader(this.file);
            this.json = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(file.getPath());
        }
    }

    public JsonWrapper(String fileName) {
        try {
            this.file = new File(fileName);
            FileUtils.touch(file);
            this.reader = new FileReader(file);
            this.json = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void putForObject(String obj, String element, Object value) {
        if (isReadOnly)
            return;
        // TODO: Make it work automatically with nested objects.
        String[] nests = obj.split("/");

        JsonObject workingJsonObj = null;

        if (value instanceof Boolean) {
            this.getJsonObject(obj).addProperty(element, (boolean) value);
        } else if (value instanceof Character) {
            this.getJsonObject(obj).addProperty(element, (char) value);
        } else if (value instanceof String) {
            this.getJsonObject(obj).addProperty(element, (String) value);
        } else if (value instanceof Number) {
            this.getJsonObject(obj).addProperty(element, (Number) value);
        }

        this.write();
    }

    public JsonObject getJson() {
        return this.json;
    }

    public File getFile() {
        if (isReadOnly)
            return new File("");
        return this.file;
    }

    /**
     * A method that automatically determines what data type to add.
     *
     * @param element
     * @param obj
     */
    public void put(String element, Object obj) {
        if (isReadOnly)
            return;
        if (obj instanceof Boolean) {
            json.addProperty(element, (boolean) obj);
        } else if (obj instanceof Character) {
            json.addProperty(element, (char) obj);
        } else if (obj instanceof String) {
            json.addProperty(element, (String) obj);
        } else if (obj instanceof Number) {
            json.addProperty(element, (Number) obj);
        }

        this.write();
    }

    public void addString(String element, String value) {
        if (isReadOnly)
            return;
        json.addProperty(element, this.getString(element) + value);
        this.write();
    }

    public void addInt(String element, int value) {
        if (isReadOnly)
            return;
        json.addProperty(element, this.getInt(element) + value);
        this.write();
    }

    public void addFloat(String element, float value) {
        if (isReadOnly)
            return;
        json.addProperty(element, this.getFloat(element) + value);
        this.write();
    }

    public void addDouble(String element, double value) {
        if (isReadOnly)
            return;
        json.addProperty(element, this.getDouble(element) + value);
        this.write();
    }

    public void mult(String element, Object obj) {
        if (isReadOnly)
            return;
        if (obj instanceof Integer) {
            json.addProperty(element, this.getInt(element) * (int) obj);
        } else if (obj instanceof Float) {
            json.addProperty(element, this.getFloat(element) * (float) obj);
        } else if (obj instanceof Double) {
            json.addProperty(element, this.getDouble(element) * (double) obj);
        }

        this.write();
    }

    public void div(String element, Object obj) {
        if (isReadOnly)
            return;
        if (obj instanceof Integer) {
            json.addProperty(element, this.getInt(element) / (int) obj);
        } else if (obj instanceof Float) {
            json.addProperty(element, this.getFloat(element) / (float) obj);
        } else if (obj instanceof Double) {
            json.addProperty(element, this.getDouble(element) / (double) obj);
        }

        this.write();
    }

    public void remove(String element) {
        if (isReadOnly)
            return;
        this.json.remove(element);
        this.write();
    }

    public void write() {
        if (isReadOnly)
            return;
        try {
            FileUtils.writeStringToFile(this.file, gson.toJson(json), Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.write();
            this.reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAndDelete() {
        try {
            this.write();
            this.reader.close();
            FileUtils.forceDelete(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String element) {
        return this.json.get(element).getAsBoolean();
    }

    public String getString(String element) {
        return this.json.get(element).getAsString();
    }

    public Number getNumber(String element) {
        return this.json.get(element).getAsNumber();
    }

    public int getInt(String element) {
        return this.json.get(element).getAsInt();
    }

    public double getDouble(String element) {
        return this.json.get(element).getAsDouble();
    }

    public float getFloat(String element) {
        return this.json.get(element).getAsFloat();
    }

    public char getChar(String element) {
        return this.json.get(element).getAsCharacter();
    }

    public JsonObject getJsonObject(String element) {
        return this.json.get(element).getAsJsonObject();
    }

    public JsonArray getJsonArray(String element) {
        return this.json.get(element).getAsJsonArray();
    }

    public Object[] get(String key) {
        //TODO: implement
        return null;
    }
}
