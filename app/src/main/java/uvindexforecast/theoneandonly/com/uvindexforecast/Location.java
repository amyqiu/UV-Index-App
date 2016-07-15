package uvindexforecast.theoneandonly.com.uvindexforecast;

/**
 * Created by Amy on 2016-07-14.
 */
public class Location {
    private String location_name;
    private String province;
    private String code;

    public Location (String location_name, String province, String code)
    {
        this.code = code;
        this.location_name = location_name;
        this.province = province;
    }

    @Override
    public String toString() {
        return location_name + "," + province;
    }
}


