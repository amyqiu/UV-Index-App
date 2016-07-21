package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Amy on 2016-07-14.
 */
public class HandleCSV {
    InputStream inputStream;
    List<String[]> resultList;

    public HandleCSV(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public void read(List <Location> locationList){
        resultList = new ArrayList<String[]>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
                Location city = new Location(row[0], row[1], row[2]);
                locationList.add(city);
                Log.d("City", city.toString());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        //return resultList;
    }


}
