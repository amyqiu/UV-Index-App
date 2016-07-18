package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amy on 2016-07-17.
 */
public class LocationAdapter extends ArrayAdapter<Location> implements Filterable {
    private Context ctx;
    private List<Location> locationList = new ArrayList<Location>();

    public LocationAdapter(Context ctx, List<Location> locationList) {
        super(ctx, R.layout.activity_settings, locationList);
        this.locationList = locationList;
        this.ctx = ctx;
    }

    @Override
    public Location getItem(int position) {
        if (locationList != null)
            return locationList.get(position);

        return null;
    }

    @Override
    public int getCount() {
        if (locationList != null)
            return locationList.size();

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inf.inflate(R.layout.activity_settings, parent, false);

        }

        TextView tv = (TextView) result.findViewById(R.id.txtLocationName);
        tv.setText(locationList.get(position).getLocationName() + "," + locationList.get(position).getProvince());

        return result;
    }

    @Override
    public long getItemId(int position) {
        if (locationList != null)
            return locationList.get(position).hashCode();

        return 0;
    }
    @Override
    public Filter getFilter() {

        Filter cityFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() < 2)
                    return results;
                UVIndexForecast temp = new UVIndexForecast();
                List <Location> locationResultList = temp.getLocationResultList();
                results.values = locationResultList;
                results.count = locationResultList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                locationList = (List) results.values;
                notifyDataSetChanged();
            }
        };

        return cityFilter;
    }
}
