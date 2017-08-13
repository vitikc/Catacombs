package me.vitikc.catacombs.regions;

import com.sk89q.worldedit.Vector;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("CCRegion")
public class CCRegion implements ConfigurationSerializable {

    private String name;
    private Vector minimum;
    private Vector maximum;

    public CCRegion(String name,Vector min, Vector max){
        this.name = name;
        this.minimum = min;
        this.maximum = max;
    }

    public CCRegion(String name, double x1, double y1, double z1, double x2, double y2, double z2){
        this.name = name;
        this.minimum = new Vector(x1,y1,z1);
        this.maximum = new Vector(x2,y2,z2);
    }

    public String getName() {
        return name;
    }

    public Vector getMaximum() {
        return maximum;
    }

    public Vector getMinimum() {
        return minimum;
    }

    public void setMaximum(Vector maximum) {
        this.maximum = maximum;
    }

    public void setMaximum(double x, double y, double z){
        this.maximum = new Vector(x,y,z);
    }

    public void setMinimum(Vector minimum) {
        this.minimum = minimum;
    }

    public void setMinimum(double x, double y, double z){
        this.minimum = new Vector(x,y,z);
    }

    public boolean contains(Vector vector){
        double x = vector.getBlockX();
        double y = vector.getBlockY();
        double z = vector.getBlockZ();
        return x >= (double)this.minimum.getBlockX() && x < (double)(this.maximum.getBlockX() + 1) &&
                y >= (double)this.minimum.getBlockY() && y < (double)(this.maximum.getBlockY() + 1) &&
                z >= (double)this.minimum.getBlockZ() && z < (double)(this.maximum.getBlockZ() + 1);
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("name", name);
        result.put("minx", minimum.getBlockX());
        result.put("miny", minimum.getBlockY());
        result.put("minz", minimum.getBlockZ());
        result.put("maxx", maximum.getBlockX());
        result.put("maxy", maximum.getBlockY());
        result.put("maxz", maximum.getBlockZ());

        return result;
    }

    public static CCRegion deserialize(Map<String, Object> map){
        String name = "";
        double minx = 0;
        double miny = 0;
        double minz = 0;
        double maxx = 0;
        double maxy = 0;
        double maxz = 0;

        if(map.containsKey("name")){
            name = (String) map.get("name");
        }
        if(map.containsKey("minx")){
            minx = (int) map.get("minx");
        }
        if(map.containsKey("miny")){
            miny = (int) map.get("miny");
        }
        if(map.containsKey("minz")){
            minz = (int) map.get("minz");
        }
        if(map.containsKey("maxx")){
            maxx = (int) map.get("maxx");
        }
        if(map.containsKey("maxy")){
            maxy = (int) map.get("maxy");
        }
        if(map.containsKey("maxz")){
            maxz = (int) map.get("maxz");
        }
        return new CCRegion(name, minx, miny, minz, maxx, maxy, maxz);
    }
}
