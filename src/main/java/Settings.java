import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

public class Settings {
    public boolean isAlwaysOnTop;
    public double secondsToUpdate;
    public Point frameLocation;
    public HashMap<String, String> ipRangeLabels = new HashMap<String, String>();

    public Settings(boolean isAlwaysOnTop, int secondsToUpdate, Point frameLocation) {
        this.isAlwaysOnTop = isAlwaysOnTop;
        this.secondsToUpdate = secondsToUpdate;
        this.frameLocation = frameLocation;
    }

    public String getIPRangeLabel(IPRange ipRange) {
        for (Entry<String, String> entry : ipRangeLabels.entrySet()) {
            if (entry.getKey().equals(ipRange.toString())) {
                return entry.getValue();
            }
        }

        return ipRange.toString();
    }

    public void addIPRangeLabel(IPRange ipRange, String label) {
        if (ipRange == null || label == null) {
            return;
        }

        ipRangeLabels.put(ipRange.toString(), label);
    }

    public void setMissing() {
        if (ipRangeLabels == null) {
            ipRangeLabels = new HashMap<String, String>();
        }

        if (frameLocation == null) {
            frameLocation = new Point(0, 0);
        }
    }

    public static Settings load() {
        try (Reader reader = new FileReader(getLocation())) {
            Gson gson = new GsonBuilder().serializeNulls().create();

            final Type TYPE = new TypeToken<Settings>() {
            }.getType();

            return gson.fromJson(reader, TYPE);
        } catch (JsonSyntaxException e) {
            System.out.println("Error: Can't read file \"" + getLocation() + "\".");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Can't find file \"" + getLocation() + "\".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save() {
        try (Writer writer = new FileWriter(getLocation())) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.out.println("Error: Can't write file \"" + getLocation() + "\".");
            return false;
        }

        return true;
    }

    public static String getLocation() {
        return (new File("").equals(new File(File.separator)) ? System.getProperty("user.home")
                : new File("").getAbsolutePath()) + File.separator + "Usage_Checker.json";
    }
}
