package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 , data_to_api.get_response {
    JavaCameraView jv;
    Rect rect=new Rect(100,100,280,280);;
    Mat a1,a2,sub,hsv_mask,sub_hsv,sub_yrc,yrc_mask;
    public static TextView t;
    Button b;


    static{

    }
    BaseLoaderCallback bs=new BaseLoaderCallback(MainActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case BaseLoaderCallback.SUCCESS:
                {
                    jv.enableView();

                    break;
                }
                default:
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         jv=findViewById(R.id.Jview);
         t=(TextView)findViewById(R.id.output);

        jv.setVisibility(SurfaceView.VISIBLE);

        jv.setCvCameraViewListener(MainActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        a1=new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        a1.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        a1=inputFrame.rgba();
        a2=a1.t();
        Core.flip(a1.t(),a2,1);
        Imgproc.resize(a2,a2,a1.size());



        sub = new Mat();
        hsv_mask=new Mat();
        sub_hsv=new Mat();
        sub_yrc=new Mat();
        yrc_mask=new Mat();


        Imgproc.rectangle(a2,rect.tl(),rect.br(),new Scalar(255,0,0),2,8,0);
        sub=a2.submat(rect);
        Imgproc.cvtColor(sub,sub,Imgproc.COLOR_RGBA2RGB);

        Imgproc.cvtColor(sub,sub_hsv,Imgproc.COLOR_RGB2HSV);
        Core.inRange(sub_hsv,new Scalar(0,15,0),new Scalar(17,170,255),hsv_mask);
        //Imgproc.morphologyEx(hsv_mask,hsv_mask,Imgproc.MORPH_OPEN,kernel);
        Imgproc.cvtColor(sub,sub_yrc,Imgproc.COLOR_RGB2YCrCb);
        Core.inRange(sub_yrc,new Scalar(0, 135, 85),new Scalar (255,180,135),yrc_mask);
        //Imgproc.morphologyEx(yrc_mask,yrc_mask,Imgproc.MORPH_OPEN,kernel);
        Core.bitwise_and(yrc_mask,hsv_mask,sub);




        new java.util.Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public  void run(){
                        //byte b[]=new byte[sub.cols()*sub.rows()*sub.channels()];
                        //sub.get(0,0,b);
                        //Log.d("Main",String.valueOf(a2.toString()));
                        Log.d("Main",String.valueOf(sub.rows())+" "+String.valueOf(sub.cols())+" "+String.valueOf(sub.channels()));

                        //String url_image= new String(Base64.encode(b,Base64.DEFAULT));
                        data_to_api a=new data_to_api(MainActivity.this,sub);
                        a.execute();


                        Log.d("Main","insde execute async");
                    }
                },5000
        );
         //url_image=null;
        //b=null;

        return a2;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(jv!=null)
        {
            jv.disableView();

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(jv!=null)
        {
            jv.disableView();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug())
        {

            bs.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,bs);

        }

    }

    @Override
    public void get_string(String img_url) {


    }
}
