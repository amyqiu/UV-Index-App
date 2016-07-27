package uvindexforecast.theoneandonly.com.uvindexforecast;

import java.util.ArrayList;

/**
 * Created by Amy on 2016-07-14.
 */
public class Location {
    private String location_name;
    private String province;
    private String code;
    private ArrayList<Location> locationResultList;

    public Location()
    {
        this.code = "s0000585";
        this.location_name = "Markham";
        this.province = "ON";
    }

    public Location (String code, String location_name, String province)
    {
        this.code = code;
        this.location_name = location_name;
        this.province = province;
    }

    @Override
    public String toString() {
        return location_name + ", " + province;
    }

    public String getLocationName(){
        return location_name;
    }

    public String getProvince(){
        return province;
    }

    public String getCode(){
        return code;
    }

    public void setLocation_name(String location_name)
    {
        this.location_name = location_name;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public void setLocationList(ArrayList<Location> locationResultList)
    {
        this.locationResultList = locationResultList;
    }

    public ArrayList<Location> getLocationResultList(){return locationResultList; }
}


