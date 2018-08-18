package org.hcilab.recordsensors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.spec.ECField;
import java.util.ArrayList;

/**
 * Created by ayman on 3/3/2018.
 */

public class BackGroundWorker extends AsyncTask<ArrayList<PointACC>,Void,Integer> {

    Context context;
    TextView countFinished;
    ProgressBar progressBar;

    BackGroundWorker(Context ctx){context=ctx;}
    @Override
    protected Integer doInBackground(ArrayList<PointACC>... files) {

        String SaveRecordURL="http://www.hciegypt.com/main/record/Record.php";
        ArrayList<PointACC> params=files[0];
        String AllData="";
        int i=0;
        for (PointACC param:params) {
i++;
            AllData+= param.timeStamp+"#";
            AllData+= param.CurrecntLocationLong+"#";
            AllData+= param.CurrecntLocationAlt+"#";
            AllData+=param.GestureName+"#";
            AllData+= String.valueOf(param.x)+"#";
            AllData+= String.valueOf(param.y)+"#";
            AllData+= String.valueOf(param.z)+"#";
            AllData+=i+"#";
            AllData+= String.valueOf(param.AngleX)+"#";
            AllData+= String.valueOf(param.AngleY)+"#";
            AllData+="*";//end of line
            }
        try {
            URL url = new URL(SaveRecordURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String Post_data = URLEncoder.encode("AllData", "UTF-8") + "=" + URLEncoder.encode(AllData, "UTF-8");
            bf.write(Post_data);
            bf.flush();
            bf.close();
            out.close();


            String R = "";

            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));

            /*String line="";
            while ((line=br.readLine())!=null)
            {
                R+=line;

            }
*/
            br.close();
            is.close();

//         Log.i("Message from PHP", R);

            con.disconnect();

        }
        catch (Exception e){

            Log.i("Message from ayman", e.getMessage());

        }

        /*Log.i("Size",String.valueOf(params.size()));
        for (PointACC param:params) {
            try {
                String TripID = param.timeStamp;
                String Long = param.CurrecntLocationLong;
                String Lat = param.CurrecntLocationAlt;
                String GestureId = param.GestureName;
                String AccX = String.valueOf(param.x);
                String AccY = String.valueOf(param.y);
                String AccZ = String.valueOf(param.z);


                URL url = new URL(SaveRecordURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream out = con.getOutputStream();
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                String Post_data = URLEncoder.encode("TripId", "UTF-8") + "=" + URLEncoder.encode(TripID, "UTF-8") + "&"
                        + URLEncoder.encode("Long", "UTF-8") + "=" + URLEncoder.encode(Long, "UTF-8") + "&"
                        + URLEncoder.encode("Lat", "UTF-8") + "=" + URLEncoder.encode(Lat, "UTF-8") + "&"
                        + URLEncoder.encode("GestureId", "UTF-8") + "=" + URLEncoder.encode(GestureId, "UTF-8") + "&"
                        + URLEncoder.encode("AccX", "UTF-8") + "=" + URLEncoder.encode(AccX, "UTF-8") + "&"
                        + URLEncoder.encode("AccY", "UTF-8") + "=" + URLEncoder.encode(AccY, "UTF-8") + "&"
                        + URLEncoder.encode("AccZ", "UTF-8") + "=" + URLEncoder.encode(AccZ, "UTF-8") + "&";
                bf.write(Post_data);
                bf.flush();
                bf.close();
                out.close();

                String R = "";

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));

            String line="";
            while ((line=br.readLine())!=null)
            {
                R+=line;

            }

                br.close();
                is.close();

                Log.i("Message from PHP", R);

                con.disconnect();


                //return 1;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
*/
    return 1;
    }
    protected void onPostExecute(Integer result) {
        //progressBar.setVisibility(View.GONE);
        //txt.setText(result);
        //btn.setText("Restart");
        int finished=(Integer.parseInt((String) countFinished.getText())+1);
        countFinished.setText(""+finished);
        //double Per=((double)finished/FirstScreen.count)*100;
        //Log.i("Finished and Count", String.valueOf(finished) +" , "+ String.valueOf(FirstScreen.count) + " , " + Per );
        //progressBar.setProgress(Integer.parseInt(String.valueOf(());
    }

    public void setCountFinished(TextView countFinished) {
        this.countFinished = countFinished;


    }
}
