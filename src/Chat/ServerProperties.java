package Chat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerProperties {
	 private static final Properties props = new Properties();

	    public static String getPath() {
	        return System.getProperty("path", "") + "setting.ini";
	    }

	    public static void loadProperties() {
	        try {
	            InputStream in = new FileInputStream(getPath());
	            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
	            props.load(bf);
	            bf.close();
	        } catch (IOException ex) {
	            System.out.println("Ū��\"" + getPath() + "\"�ɮץ��� " + ex);
	        }
	    }

	    public static void saveProperties() {
	    	File outputFile = new File(getPath());
	        if (outputFile.exists()) {
	            outputFile.delete();
	        }
	        ArrayList<String> setting = new ArrayList();
	        ArrayList<String> job = new ArrayList();
	        Map<String, ArrayList<String>> world = new HashMap();
	        Map<String, ArrayList<String>> tespia = new HashMap();

	        for (Map.Entry i : props.entrySet()) {
	            String info = i.getKey() + " = " + i.getValue() + "\r\n";
	            if (((String) i.getKey()).contains("World")) {
	                int worldId = Integer.parseInt(((String) i.getKey()).substring(((String) i.getKey()).lastIndexOf('d') + 1));
	            } else if (((String) i.getKey()).contains("Worldt")) {
	                int worldId = Integer.parseInt(((String) i.getKey()).substring(((String) i.getKey()).lastIndexOf('t') + 1));
	            } else if (((String) i.getKey()).contains("Job")) {
	                job.add(info);
	            } else {
	                setting.add(info);
	            }
	        }
	    }

	    public static void setProperty(String prop, String newInf) {
	        props.setProperty(prop, newInf);
	    }

	    public static void setProperty(String prop, boolean newInf) {
	        props.setProperty(prop, String.valueOf(newInf));
	    }

	    public static void setProperty(String prop, byte newInf) {
	        props.setProperty(prop, String.valueOf(newInf));
	    }

	    public static void setProperty(String prop, short newInf) {
	        props.setProperty(prop, String.valueOf(newInf));
	    }

	    public static void setProperty(String prop, int newInf) {
	        props.setProperty(prop, String.valueOf(newInf));
	    }

	    public static void setProperty(String prop, long newInf) {
	        props.setProperty(prop, String.valueOf(newInf));
	    }

	    public static void removeProperty(String prop) {
	        props.remove(prop);
	    }

	    public static String getProperty(String s, String def) {
	        return props.getProperty(s, def);
	    }

	    public static boolean getProperty(String s, boolean def) {
	        return getProperty(s, def ? "true" : "false").equalsIgnoreCase("true");
	    }

	    public static byte getProperty(String s, byte def) {
	        String property = props.getProperty(s);
	        if (property != null) {
	            return Byte.parseByte(property);
	        }
	        return def;
	    }

	    public static short getProperty(String s, short def) {
	        String property = props.getProperty(s);
	        if (property != null) {
	            return Short.parseShort(property);
	        }
	        return def;
	    }

	    public static int getProperty(String s, int def) {
	        String property = props.getProperty(s);
	        if (property != null) {
	            return Integer.parseInt(property);
	        }
	        return def;
	    }

	    public static long getProperty(String s, long def) {
	        String property = props.getProperty(s);
	        if (property != null) {
	            return Long.parseLong(property);
	        }
	        return def;
	    }

	    static {
	        loadProperties();
	    }
}
