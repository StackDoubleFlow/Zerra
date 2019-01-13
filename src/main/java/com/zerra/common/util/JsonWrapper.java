package com.zerra.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class JsonWrapper
{

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// means that file is null
	boolean isReadOnly = false;
	boolean isFileBased = true;
	private File file;
	private JsonObject mainJson;
	private Reader reader;

	public JsonWrapper(FileInputStream fileInputStream)
	{
		reader = new InputStreamReader(fileInputStream);
		JsonReader jsonReader = gson.newJsonReader(reader);
		mainJson = gson.fromJson(jsonReader, JsonObject.class);
		isReadOnly = true;
		isFileBased = false;
	}

	public JsonWrapper(File file)
	{
		try
		{
			FileUtils.touch(file);
			this.file = file;
			FileUtils.touch(this.file);
			this.reader = new FileReader(this.file);
			this.mainJson = gson.fromJson(reader, JsonObject.class);
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(file.getPath());
		}
	}

	public JsonWrapper(String string, boolean isFileBased)
	{
		if (isFileBased)
		{
			try
			{
				this.file = new File(string);
				FileUtils.touch(file);
				this.reader = new FileReader(file);
				this.mainJson = gson.fromJson(reader, JsonObject.class);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			this.mainJson = gson.fromJson(string, JsonObject.class);
			this.isFileBased = false;
		}
	}

	public String fetch()
	{
		return fetch(true);
	}

	public String fetch(boolean format)
	{
		GsonBuilder builder = new GsonBuilder();

		if (format)
		{
			return builder.setPrettyPrinting().create().toJson(this.mainJson);
		}

		return builder.create().toJson(this.mainJson);
	}

	public JsonObject getObjectFromPath(String path)
	{
		String[] objs = path.split("/");

		JsonObject obj = this.mainJson;

		for (int i = 0; i < objs.length; i++)
		{
			obj = obj.getAsJsonObject(objs[i]);
		}

		return obj;
	}

	public void putForObject(String path, String element, Object value)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;

		JsonObject obj = this.getObjectFromPath(path);

		if (value instanceof Boolean)
		{
			obj.addProperty(element, (boolean) value);
		} else if (value instanceof Character)
		{
			obj.addProperty(element, (char) value);
		} else if (value instanceof String)
		{
			obj.addProperty(element, (String) value);
		} else if (value instanceof Number)
		{
			obj.addProperty(element, (Number) value);
		}

		this.write();
	}

	public JsonObject getJson()
	{
		return this.mainJson;
	}

	public File getFile()
	{
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
	public void put(String element, Object obj)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;

		if (obj instanceof Boolean)
		{
			mainJson.addProperty(element, (boolean) obj);
		} else if (obj instanceof Character)
		{
			mainJson.addProperty(element, (char) obj);
		} else if (obj instanceof String)
		{
			mainJson.addProperty(element, (String) obj);
		} else if (obj instanceof Number)
		{
			mainJson.addProperty(element, (Number) obj);
		}

		this.write();
	}

	public void addString(String element, String value)
	{
		if (isReadOnly | !this.getJson().has(element))
			return;
		mainJson.addProperty(element, this.getString(element) + value);
		this.write();
	}

	public void addInt(String element, int value)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		mainJson.addProperty(element, this.getInt(element) + value);
		this.write();
	}

	public void addFloat(String element, float value)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		mainJson.addProperty(element, this.getFloat(element) + value);
		this.write();
	}

	public void addDouble(String element, double value)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		mainJson.addProperty(element, this.getDouble(element) + value);
		this.write();
	}

	public void mult(String element, Object obj)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		if (obj instanceof Integer)
		{
			mainJson.addProperty(element, this.getInt(element) * (int) obj);
		} else if (obj instanceof Float)
		{
			mainJson.addProperty(element, this.getFloat(element) * (float) obj);
		} else if (obj instanceof Double)
		{
			mainJson.addProperty(element, this.getDouble(element) * (double) obj);
		}

		this.write();
	}

	public void div(String element, Object obj)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		if (obj instanceof Integer)
		{
			mainJson.addProperty(element, this.getInt(element) / (int) obj);
		} else if (obj instanceof Float)
		{
			mainJson.addProperty(element, this.getFloat(element) / (float) obj);
		} else if (obj instanceof Double)
		{
			mainJson.addProperty(element, this.getDouble(element) / (double) obj);
		}

		this.write();
	}

	public void remove(String element)
	{
		if (isReadOnly || !this.getJson().has(element))
			return;
		this.mainJson.remove(element);
		this.write();
	}

	public void write()
	{
		if (isReadOnly || !isFileBased)
			return;
		try
		{
			FileUtils.writeStringToFile(this.file, gson.toJson(mainJson), Charset.defaultCharset());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		if (isReadOnly || !isFileBased)
			return;

		try
		{
			this.write();
			this.reader.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void closeAndDelete()
	{
		if (isReadOnly || !isFileBased)
			return;

		try
		{
			this.close();
			FileUtils.forceDelete(this.file);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		try
		{
			if (this.reader.ready())
				this.reader.close();
		} catch (IOException e)
		{
			// Do nothing. The reader is closed, based on the io exception. There's nothing
			// more for us to do here.
		}
	}

	public boolean getBoolean(String element)
	{
		return this.mainJson.get(element).getAsBoolean();
	}

	public String getString(String element)
	{
		return this.mainJson.get(element).getAsString();
	}

	public Number getNumber(String element)
	{
		return this.mainJson.get(element).getAsNumber();
	}

	public int getInt(String element)
	{
		return this.mainJson.get(element).getAsInt();
	}

	public double getDouble(String element)
	{
		return this.mainJson.get(element).getAsDouble();
	}

	public float getFloat(String element)
	{
		return this.mainJson.get(element).getAsFloat();
	}

	public char getChar(String element)
	{
		return this.mainJson.get(element).getAsCharacter();
	}

	public JsonObject getJsonObject(String element)
	{
		return this.mainJson.get(element).getAsJsonObject();
	}

	public JsonArray getJsonArray(String element)
	{
		return this.mainJson.get(element).getAsJsonArray();
	}

	public Object[] get(String key)
	{
		// TODO: implement
		return null;
	}
}
