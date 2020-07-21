package com.example.myapplication2;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class data_to_api extends AsyncTask<Void,Void,Void> {
    String imageurl;
    String line="";
   // Context main1;
    String val;
    byte[] b;


public interface get_response{
    void get_string(String img_url);
}
    data_to_api( Context main, Mat sub)
    {
        //main1=main;
         b=new byte[sub.cols()*sub.rows()*sub.channels()];
        sub.get(0,0,b);
         this.imageurl= new String(Base64.encode(b,Base64.DEFAULT));
         Log.d("Main",String.valueOf(sub.rows())+" "+String.valueOf(sub.cols())+" "+String.valueOf(sub.channels()));
        //this.imageurl=imageurl;

        //Log.d("Main",imageurl);
    }

    get_response gr=null;
    @Override
    protected Void doInBackground(Void... voids) {
        //Log.d("Main","url connect start");

            URL url = null; //in the real code, there is an ip and a port
            try {
                url = new URL("http://192.168.1.207:5000/predict");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground url connectyion: ", e);
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                //Log.d("Main","url connect");

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground conn connection: ", e);
            }
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground request post: ", e);
            }
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try {
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground connect: ", e);
            }
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("image",imageurl);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground json put: ", e);
            }
            DataOutputStream os = null;
            try {
                os = new DataOutputStream(conn.getOutputStream());
                //Log.d("Main","data send");
                //Log.d("Main",jsonParam.toString());
                os.writeBytes((jsonParam.toString()));
                os.flush();
                os.close();



            } catch (IOException e) {
                Log.e("Main","doinbackground output stream",e);

                e.printStackTrace();
            }
            InputStream ip = null;
            try {
                ip = conn.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Main", "doInBackground input stream: ", e);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(ip));
            String l = "";
            while (l != null) {
                try {
                    l = br.readLine();
                    line = line + l;
                } catch (IOException e) {
                    Log.e("Main", "doInBackground line reading: ", e);
                    e.printStackTrace();
                }
                try {
                    Log.d("Main", line);
                    JSONObject ja = new JSONObject(line);
                    val=(ja.get("str_image")).toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Main", "doInBackground json reading: ", e);
                }

            }




        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.d("Main","output from api "+val);

        Log.d("Main","output from api "+line);
        MainActivity.t.setText(line.substring(14,15));
        this.imageurl=null;
        this.b=null;
        System.gc();
        //gr.get_string(this.val);

    }


}
