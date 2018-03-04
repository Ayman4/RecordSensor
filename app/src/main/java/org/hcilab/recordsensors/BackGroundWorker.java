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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ayman on 3/3/2018.
 */

public class BackGroundWorker extends AsyncTask<String,Void,Integer> {

    Context context;
    TextView countFinished;
    ProgressBar progressBar;

    BackGroundWorker(Context ctx){context=ctx;}
    @Override
    protected Integer doInBackground(String... params) {
        String SaveRecordURL="http://hciegypt.com/main/record/Record.php";
        try {
            String TripID=params[0];
            String Long=params[1];
            String Lat=params[2];
            String GestureId=params[3];
            String AccX=params[4];
            String AccY=params[5];
            String AccZ=params[6];


            URL url=new URL(SaveRecordURL);
            HttpURLConnection con= (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            OutputStream out=con.getOutputStream();
            BufferedWriter bf=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
            String Post_data= URLEncoder.encode("TripId","UTF-8")+"="+URLEncoder.encode(TripID,"UTF-8")+"&"
                    +URLEncoder.encode("Long","UTF-8")+"="+URLEncoder.encode(Long,"UTF-8")+"&"
                    +URLEncoder.encode("Lat","UTF-8")+"="+URLEncoder.encode(Lat,"UTF-8")+"&"
                    +URLEncoder.encode("GestureId","UTF-8")+"="+URLEncoder.encode(GestureId,"UTF-8")+"&"
                    +URLEncoder.encode("AccX","UTF-8")+"="+URLEncoder.encode(AccX,"UTF-8")+"&"
                    +URLEncoder.encode("AccY","UTF-8")+"="+URLEncoder.encode(AccY,"UTF-8")+"&"
                    +URLEncoder.encode("AccZ","UTF-8")+"="+URLEncoder.encode(AccZ,"UTF-8")+"&"

                    ;
            bf.write(Post_data);
            bf.flush();
            bf.close();
            out.close();

            String R="";

            InputStream is=con.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is,"iso-8859-1"));

            String line="";
            while ((line=br.readLine())!=null)
            {
                R+=line;

            }
            br.close();
            is.close();

            Log.i("Message from PHP",R);

            con.disconnect();


            return 1;




        } catch (Exception e) {
            e.printStackTrace();
        }


    return 0;
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
