

package game.Typing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;


public class CustomKeyboard extends View {
    //Variables declaration and intialization
    TextView tv1;
    EditText et1;
    int count_trakar=1,count_nukta=1,count_rafar=1;
    float touchdX=0,touchdY=0,touchdX1=0,touchdY1=0,distance=0,distance1=0;
    AudioManager am;
    int COUNT_DOWN_TIME=1000;
    final int longpressTimeDownBegin = 1000;
    float touchMovementX, touchMovementY;
    boolean touch_flag = false, multi_touch_flag = false, action_down_flag = false, multi_touch = false,tri_touch=false,navigator_flag=false;
    float x, y, x1, y1, touchDownX, touchDownY;
    boolean single_touch = false;
    Timer longPressTimer= new Timer();
    Timer longPressArray[][] = new Timer[11][7];
    boolean PROXIMITY_CHECK=false;
    float mOuterRadius;
    int radius;
    int arc;
    float PITCH = 1f;
    int THRESHOLD_DIST = 50,THRESHOLD_DIST_LAST_ROW = 60;
    Activity activity;
    boolean action_up = false;
    private static boolean isChakraVisible;
    float mInnerRadius = (float)180;
    AccessibilityManager mAccessibilityManager;
    Paint paint_arc;
    private static final int SIZE = 800;
    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    float width, height;
    private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW};
    private Paint textPaint;
    private int keyCode;
    CustomKeyboard obj;
    int l,k;
    public String[] vowels = new String[]{"\u094D", "\u093E", "\u093F", "\u0940", "\u0941", "\u0942", "\u0947", "\u0948", "\u094B", "\u094C"};
    public String[] vowels_speak = new String[]{"विराम", "आ", "\u0907","\u0908","\u0909","\u090A","\u090F","\u0910","\u0913","\u0914"};
    //    , "\u094B", "\u094C"
    public String[] vowels_aah = new String[]{"\u0905","\u0906","\u0907","\u0908","\u0909","\u090A","\u090F","\u0910","\u0913","\u0914"};
    public String[] vowes_uuh = new String[]{"\u0960","\u0944","","\u093D","\u0946","\u094A","\u0945","\u0972","\u0949","\u0911"};
    boolean vowelBoolean = false;
    public String keyCodelabel = "";
    private TextToSpeech tts, tts1;
    public PopupWindow popUp;
    boolean flag_pointerdown = false;
    EditText et;
    Keyboard.Key k1;
    boolean flag2[] = new boolean[3];

    //Text to Speeches intialisation
    TextToSpeech.OnInitListener onInit = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                final Locale loc = new Locale("hin", "IND");
                int result = tts.setLanguage(loc);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {

//                    speakOut(keyCodelabel);
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }
    };
    //Text to Speech
    TextToSpeech.OnInitListener  onInit1 = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                final Locale loc = new Locale("hin", "IND");
                int result1 = tts.setLanguage(loc);
                if (result1 == TextToSpeech.LANG_MISSING_DATA
                        || result1 == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {

//                    speakOut(keyCodelabel);
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }
    };


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
//        final String service = mHostActivity.getPackageName() + "/" + AccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("TAG", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("TAG", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            return true;
        } else {
            return false;
        }
    }
    public void speakOut_pitch(String keyCodelabel) {
//        if (!isAccessibilitySettingsOn(mHostActivity.getApplicationContext())) {
        tts1.setPitch((float) 0.8);
        tts1.speak(keyCodelabel, TextToSpeech.QUEUE_FLUSH, null);
//        }
    }
    public void speakOut(String keyCodelabel) {
//        if (!isAccessibilitySettingsOn(mHostActivity.getApplicationContext())) {
        tts.setPitch(1);
        tts.speak(keyCodelabel, TextToSpeech.QUEUE_FLUSH, null);
//        }
    }
    public void speakOut(String keyCodelabel,float pitch) {
//        if(!isAccessibilitySettingsOn(mHostActivity.getApplicationContext())){
        if(pitch == 0.8){
            tts.setPitch((float) 0.8);
        }else{
            tts.setPitch(pitch);
        }
        tts.speak(keyCodelabel, TextToSpeech.QUEUE_FLUSH, null);}
//    }



    public void setTouchDownPoint(float x1, float y1) {
        touchDownX = x1;
        touchDownY = y1;
    }


    private void initView() {

        paint_arc = new Paint();
        paint_arc.setColor(Color.BLUE);
        paint_arc.setStrokeWidth(1);
        paint_arc.setStrokeWidth(1);
        paint_arc.setStyle(Paint.Style.STROKE);


        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (multi_touch == false)
            return;
        DisplayMetrics metrics = new DisplayMetrics();
        mHostActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.heightPixels;
        height = metrics.widthPixels;
        // draw all pointers
        mOuterRadius = (float) (0.35 * Math.min(width, height));
        final RectF bound = new RectF();
        final RectF boundOut;

        Log.d("pointer_down_co_ondraw", "(" + touchDownX + "," + touchDownY + ")");

        bound.set(touchDownX - mOuterRadius, touchDownY, touchDownX + mOuterRadius, touchDownY + 2 * mOuterRadius);
//        boundOut = new RectF(mOuterRadius-3,mOuterRadius-3,(3*mOuterRadius)+3, (3*mOuterRadius)+3);
        final RectF bound1 = new RectF();

        Paint mInnerPaint = new Paint();
        mInnerPaint.setColor(Color.BLACK);
        mInnerPaint.setAntiAlias(true);

        float centerX = bound.centerX();
        float centerY = bound.centerY();
        Paint mArcDividerPaint = new Paint();
        mArcDividerPaint.setColor(Color.rgb(200, 200, 200));
        mArcDividerPaint.setAntiAlias(true);

        Paint mArcDividerPaint1 = new Paint();
        mArcDividerPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.STROKE);

        Paint mTransparentPaint = new Paint();//changes
        mTransparentPaint.setColor(Color.TRANSPARENT);//changes
        mTransparentPaint.setAntiAlias(true);//changes

        Paint mArcPaint = new Paint();
        mArcPaint.setColor(Color.rgb(54, 89, 80));//changes
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        Paint mArcPrevPaint = new Paint();
        mArcPrevPaint.setColor(Color.BLACK);//changes
//        mArcPrevPaint.setColor(Color.BLACK);//changes
        mArcPrevPaint.setAntiAlias(true);
        mArcPrevPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));

        Paint mArcPrevPaint1 = new Paint();
        mArcPrevPaint1.setColor(Color.rgb(105, 105, 105));//changes
        mArcPrevPaint1.setAntiAlias(true);

        Paint transparentPaint = new Paint();
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        //outer circle
        Log.d("Code", "I am Executing Pabba!");
        canvas.drawCircle(touchDownX, touchDownY + mOuterRadius, mOuterRadius, mArcDividerPaint);
        canvas.drawCircle(touchDownX, touchDownY + mOuterRadius, mOuterRadius, mTransparentPaint);
        canvas.drawCircle(touchDownX, touchDownY + mOuterRadius, mInnerRadius, mInnerPaint);
//
//
        Paint arcPaint, arcPaint1;
        Float anglePerArc = (float) (360.0 / 10);
        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();


        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        String text = editable.toString();
        int start = edittext.getSelectionStart();
//        if( touchMovementX - mOuterRadius > 87.5 || touchMovementY - mOuterRadius> 87.5){

        for (int i = 0; i < 10; i++) {
            if (radius > mInnerRadius) {
                arcPaint = mArcPaint;
//            arcPaint1 = mArcPrevPaint1;
                if (i == arc) {

                    arcPaint = mArcPrevPaint;
                    String speak_text="";
                    if (arc == 10) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);

                            myVib.vibrate(100);

                        }
                    } else if (arc == 1) {
                        if (flag1[arc] == 0) {


                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);
                            myVib.vibrate(100);

                        }
                    } else if (arc == 2) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 3) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_speak[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);
                            myVib.vibrate(100);
                        }

                    } else if (arc == 4) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_speak[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 5) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text =  vowels_speak[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 6) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 7) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 8) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 9) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel + vowels[arc];
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text,PITCH);
                            myVib.vibrate(100);
                        }
                    } else if (arc == 0) {
                        if (flag1[arc] == 0) {
                            if(keyCodelabel.equals("अ")){
                                speak_text = vowels_aah[arc];
                            }else if(keyCodelabel.equals("ृ")){
                                speak_text = vowes_uuh[arc];
                            }else if(keyCodelabel.equals("\u093E")){
                                speak_text = vowels_speak[arc];
                            }else{
                                speak_text = keyCodelabel+"विराम";
                            }
                            for (int i1 = 0; i1 < 10; i1++) {
                                if (i1 == arc) {
                                    flag1[i1] = 1;
                                } else {
                                    flag1[i1] = 0;
                                }
                            }
                            speakOut(speak_text);
                            myVib.vibrate(100);
                        }
                    }

                    Log.d("Code", "I am Executing Pabba!");
                    canvas.drawArc(bound, getMidAngle(i) - anglePerArc / 2, anglePerArc - 1, true, arcPaint);
                }
            }else if(radius < mInnerRadius){
                for(int i1=0 ; i1< 10 ; i1 ++){
                    flag1[i1] = 0;
                }
            }
//
//        }


        }
        drawLetters(canvas);
    }


    public String getText() {
        if (arc < 0) {
            return "k";
        }
        return getTextForArc(arc);
    }

    public String getTextForArc(int region) {
//        Log.d("Gettextforarc",keyCodelabel);
        String str;
        if(keyCodelabel.equals("\u0905")){
            str = vowels_aah[region];
        }else if(keyCodelabel.equals("ृ")){
            str = vowes_uuh[region];
        }else if(keyCodelabel.equals("")){
            str = vowels_aah[region];
        }else{
            str = keyCodelabel + vowels[region];
        }
        Log.d("String", Integer.toString(keyCode));
        return str;
    }

    private void drawLetters(Canvas canvas) {
        float offsetY = 0;
        //textBounds = new Rect();
        Rect textBounds = new Rect();
        Paint mInnerTextPaint = new Paint();
        mInnerTextPaint.setColor(Color.BLACK);
        mInnerTextPaint.setAntiAlias(true);
        mInnerTextPaint.setTextAlign(Paint.Align.CENTER);

        mInnerTextPaint.getTextBounds(getText(), 0, getText().length(), textBounds);
        offsetY = (textBounds.bottom - textBounds.top) / 2;

        mInnerTextPaint.setTextSize(50);
//        canvas.drawText(getText(), touchDownX, touchDownY+ mOuterRadius, mInnerTextPaint);

        float offsetX = 0;

        Paint mArcTextPaint = new Paint();
        mArcTextPaint.setColor(Color.BLACK);
        mArcTextPaint.setAntiAlias(true);
        mArcTextPaint.setTextAlign(Paint.Align.CENTER);
        float mArcTextRadius;

        for (int i = 0; i < 10; i++) {
            //PointF textPos = getArcTextPoint(i);
            PointF textPos = new PointF();
            //Rect textBounds = new Rect();
            String text = getTextForArc(i);
            mArcTextPaint.getTextBounds(text, 0, text.length(), textBounds);

            mArcTextRadius = (float) (0.9 * mOuterRadius);
            offsetY = (textBounds.bottom - textBounds.top) / 2;
            float angleRad = (float) Math.toRadians(getMidAngle(i));
            textPos.x = touchDownX + (float) (mArcTextRadius * Math.cos(angleRad)) + offsetX;
            textPos.y = touchDownY + (float) (mArcTextRadius * Math.sin(angleRad)) + offsetY;
            mArcTextPaint.setTextSize(50);
            canvas.drawText(getTextForArc(i), textPos.x, textPos.y + mOuterRadius, mArcTextPaint);


        }

    }

    public float getMidAngle(int region) {
        float anglePerArc = (float) (360.0 / 10);
        float offset = -90;
        float midAngle = region * anglePerArc + offset;
        return midAngle;
    }

    public void setKeyCode(int keyCodeClicked) {
        this.keyCode = keyCodeClicked;
    }

    /**
     * A link to the KeyboardView that is used to render this CustomKeyboard.
     */
    Vibrator myVib;
    int count;
    KeyEvent ke;
    private KeyboardView mKeyboardView;
    /**
     * A link to the activity that hosts the {@link #mKeyboardView}.
     */
    String keyUniCode;
    boolean long_click_flag=false;
    private Activity mHostActivity;
    static int flag_num = 0;
    static int flag_trakar = 0;
    static int flag_rafar = 0;
    public int keyCodeClicked;
    public RelativeLayout relativelayout;

    int flag[][] = new int[11][7];

    int flag1[] = new int[10];

    boolean action_downflag2[] = {false,false,false};
    int P_CHECK_FLAGS[][] =  new int[11][7];
    boolean PREV_COORDINATE=false;
    private void handleMove(int x, int y) {
        touchMovementX = (int) x - touchDownX;
        touchMovementY = (int) y - touchDownY;

        if (y == 0 && touchMovementX < mOuterRadius
                && touchMovementY < mOuterRadius) {
            float outerRadius = (float) (1.2 * mOuterRadius);
            touchMovementY = -(int) Math.sqrt(outerRadius * outerRadius
                    - touchMovementX * touchMovementX);
        }
        radius = (int) Math.sqrt((touchMovementX * touchMovementX)
                + (touchMovementY * touchMovementY));

        float theta = (float) Math.toDegrees(Math.atan2(touchMovementY,
                touchMovementX));

        if (radius > mInnerRadius) {

            arc = findArc(theta);
            setArc(arc);
        } else {
            desetArc();
        }

    }

    public void setArc(int region) {
        if (region != arc) {
            arc = region;
            invalidate();
        }
    }

    public void desetArc() {
        arc = -1;
        invalidate();
    }

    private int findArc(float theta) {
        int nArcs = 10;
        float offset = (float) -(90.0 + 360.0 / (2 * nArcs));
        float relAngle = theta - offset;
        if (relAngle < 0) {
            relAngle = 360 + relAngle;
        }
        int region = (int) (relAngle * nArcs / 360);
        return region;
    }
    //    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
//        public void onLongPress(MotionEvent e) {
////            if(flag[1][0] == 1){
////                speakOut("kamal");
////            }else if(flag[1][1] == 1){
////                speakOut("खुशी");
////            }
//
//            Log.d("long","longclick");
//        }
//    });
//
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
//
    public OnTouchListener mTouchlistener;

    {
        mTouchlistener = new OnTouchListener() {



            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int pointerIndex = event.getActionIndex();
//            int liveregion = v.getAccessibilityLiveRegion();

                //total height of keyonboard = 1545 & width of a row = 1080
                //height of row = 15.45 ,  width of a first row keys = 180 &&  width of next row keys = 216
//            Log.d("height of keyboard",Integer.toString(mKeyboardView.getHeight()));
//            Log.d("width of keyboard",Integer.toString(mKeyboardView.getWidth()));

                Log.d("(Y)", Integer.toString((int) event.getRawY()));

                // get pointer ID
                int pointerId = event.getPointerId(pointerIndex);

                // get masked (not specific to a pointer) action
                int maskedAction = event.getActionMasked();

//            Log.d("Multitouch","intialised");
                if (event.getPointerCount() > 2)
                    multi_touch = false;
                switch (maskedAction) {

                    case MotionEvent.ACTION_DOWN:
                        action_down_flag = true;
                        action_up  = false;
//                    Log.d("touch", "single touch");
                        x = (int) event.getX();
                        y = (int) event.getY();
                        int curkey = keyCodeClicked;
                        Display display = mHostActivity.getWindowManager().getDefaultDisplay();
                        Point size1 = new Point();
                        display.getSize(size1);
                        width = mKeyboardView.getWidth();
                        height = mKeyboardView.getHeight();
//                    Log.d("width,height",width+","+height);

                        Log.d("keycodeclicked", Integer.toString(curkey));

                        myVib = (Vibrator) mHostActivity.getSystemService(Context.VIBRATOR_SERVICE);
                        am = (AudioManager) mHostActivity.getSystemService(Context.AUDIO_SERVICE);
                       if(y <2*height/9){
                           if(x>5*width/6 && x < width){
                              key_touch(0,6,"delete",50);
                           }
                       }
                        Log.d("height",String.valueOf(height));
                        if (y < height/9 ) {
                            if (x < width/6) {
                                if (flag[1][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0915\u094D\u0930");
                                        keyCodelabel = "\u0915\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "\u0930\u094D\u0915";
                                        speakOut("\u0930\u094D\u0915");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0915\u093C";
                                        speakOut("\u0915"+"nukta");


                                    } else {
                                        speakOut("\u0915");
                                        keyCodelabel = "\u0915";


                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.d("proximity","Check is true for 1 0");
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[1][0]==1){
                                                speakOut("काम");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 1 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    myVib.vibrate(500);

                                    Log.d("keycodelabel", keyCodelabel);
                                    Log.d("key", "1");
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[1][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0916\u094D\u0930");
                                        keyCodelabel = "\u0916\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "\u0930\u094D\u0916";
                                        speakOut("\u0930\u094D\u0916");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0916\u093C";
                                        speakOut("\u0916"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0916";
                                        Log.d("keycodelabel", keyCodelabel);
                                        speakOut("\u0916");


                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK= true;
                                            Log.d("proximity","Check is true for 1 1");
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[1][1]==1){
                                                speakOut("खुशी");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("timer", "aftertimer");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 1 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    myVib.vibrate(50);
                                    Log.d("key", "2");
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[1][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0917\u094D\u0930");
                                        keyCodelabel = "\u0917\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "\u0930\u094D\u0917";
                                        speakOut("\u0930\u094D\u0917");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0917\u093C";
                                        speakOut("\u0917"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0917";
                                        speakOut("\u0917");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[1][2]==1){
                                                speakOut("गंगा");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("timer","aftertimer");

                                    Log.d("key", "3");
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 1 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[1][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0918\u094D\u0930");
                                        keyCodelabel = "\u0918\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "\u0930\u094D\u0918";
                                        speakOut("र्\u0918");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "घ";
                                        speakOut("घ");


                                    } else {
                                        keyCodelabel = "\u0918";
                                        speakOut("\u0918");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[1][3]==1){
                                                speakOut("घंटा");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    Log.d("timer", "aftertimer");

//     keyCodelabel = "\u0918";
//                                speakOut("\u0918");
                                    Log.d("key", "4");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 1 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[1][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0919\u094D\u0930");
                                        keyCodelabel = "\u0919\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0919";
                                        speakOut("र्\u0919");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ङ";
                                        speakOut("ङ");

                                    } else {
                                        keyCodelabel = "\u0919";
                                        speakOut("\u0919");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    Log.d("timer","aftertimer");

//                                keyCodelabel = "\u0919";
//                                speakOut("\u0919");
                                    myVib.vibrate(500);

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 1 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");

                                }
                            } else if (x > 5 * width/6 && x < 6 * width/6) {
//                                speakOut("delete");
//                                CountDownTimer timer = new CountDownTimer(5000 /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {
//
//                                    public void onTick(long millisUntilFinished) {
//                                        PROXIMITY_CHECK=true;
//
//                                    }
//
//                                    public void onFinish() {
//                                        //Done timer time out.
//                                        Log.i("Countdown Timer:","TimeOut");
//                                        long_backspace();
//                                    }
//                                }.start();
//
//                                //Intialising the flag value of this column to 1 and rest of the keys to 0
//                                for (int i = 0; i < 11; i++) {
//                                    for (int j = 0; j < 7; j++) {
//                                        if (i == 0 && j == 6) {
//                                            flag[i][j] = 1;
//                                        } else {
//                                            flag[i][j] = 0;
//                                        }
//                                    }
//                                }
//                                myVib.vibrate(50);
                            }

                        } else if (y >  height/9 && y < 2 * height/9) {
                            if (x < width/6) {
                                if (flag[2][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091A\u094D\u0930");
                                        keyCodelabel = "\u091A\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091A";
                                        speakOut("र्\u091A");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "च";
                                        speakOut("च");

                                    } else {
                                        keyCodelabel = "\u091A";
                                        speakOut("\u091A");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[2][0]==1){
                                                PROXIMITY_CHECK=true;
                                                speakOut("चम्मच");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();


//                                keyCodelabel = "\u091A";
//                                speakOut(keyCodelabel);


                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 2 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[2][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091B\u094D\u0930");
                                        keyCodelabel = "\u091B\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091B";
                                        speakOut("र्\u091B");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "छ";
                                        speakOut("छ");

                                    } else {
                                        keyCodelabel = "\u091B";
                                        speakOut("\u091B");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[2][1]==1){
                                                speakOut("छतरी");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();


//                                keyCodelabel = "\u091B";
//                                speakOut("\u091B");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 2 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[2][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091C\u094D\u0930");
                                        keyCodelabel = "\u091C\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091C";
                                        speakOut("र्\u091C");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u091C़";
                                        speakOut("\u091C"+"nukta");


                                    } else {
                                        keyCodelabel = "\u091C";
                                        speakOut("\u091C");

                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[2][2]==1){
                                                speakOut("ज़मीन ");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u091C";
//                                speakOut("\u091C");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 2 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[2][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091D\u094D\u0930");
                                        keyCodelabel = "\u091D\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091D";
                                        speakOut("र्\u091D");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "झ";
                                        speakOut("झ");

                                    } else {
                                        keyCodelabel = "\u091D";
                                        speakOut("\u091D");

                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[2][3]==1){
                                                speakOut("झन्डा");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
// /                                keyCodelabel = "\u091D";
//                                speakOut("\u091D");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 2 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[2][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091E\u094D\u0930");
                                        keyCodelabel = "\u091E\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091E";
                                        speakOut("र्\u091E");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ञ";
                                        speakOut("ञ");

                                    } else {
                                        keyCodelabel = "\u091E";
                                        speakOut("\u091E");

                                    }
//                                keyCodelabel = "\u091E";
//                                speakOut("\u091E");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 2 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }

                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
//                                speakOut("delete");
//                                CountDownTimer timer = new CountDownTimer(5000 /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {
//
//                                    public void onTick(long millisUntilFinished) {
//                                        PROXIMITY_CHECK=true;
//
//                                    }
//
//                                    public void onFinish() {
//                                        //Done timer time out.
//                                        Log.i("Countdown Timer:","TimeOut");
//                                        long_backspace();
//                                    }
//                                }.start();
//
//                                //Intialising the flag value of this column to 1 and rest of the keys to 0
//                                for (int i = 0; i < 11; i++) {
//                                    for (int j = 0; j < 7; j++) {
//                                        if (i == 0 && j == 6) {
//                                            flag[i][j] = 1;
//                                        } else {
//                                            flag[i][j] = 0;
//                                        }
//                                    }
//                                }
//                                myVib.vibrate(50);

                            }
                        } else if (y > 2 * height/9 && y < 3 * height/9) {
                            if (x < width/6) {
                                if (flag[3][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091F\u094D\u0930");
                                        keyCodelabel = "\u091F\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091F";
                                        speakOut("र्\u091F");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ट";
                                        speakOut("ट");


                                    } else {
                                        keyCodelabel = "\u091F";
                                        speakOut("\u091F");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[3][0]==1){
                                                speakOut("टमाटर");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u091F";
//                                speakOut("\u091F");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 3 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[3][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0920\u094D\u0930");
                                        keyCodelabel = "\u0920\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0920";
                                        speakOut("र्\u0920");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ठ";
                                        speakOut("ठ");

                                    } else {
                                        keyCodelabel = "\u0920";
                                        speakOut("\u0920");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[3][1]==1){
                                                speakOut("ठप्पा");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u0920";
//                                speakOut("\u0920");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 3 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[3][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0921\u094D\u0930");
                                        keyCodelabel = "\u0921\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0921";
                                        speakOut("र्\u0921");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0921़";
                                        speakOut("\u0921"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0921";
                                        speakOut("\u0921");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[3][2]==1){
                                                speakOut("डमरू");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u0921";
//                                speakOut("\u0921");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 3 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[3][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0922\u094D\u0930");
                                        keyCodelabel = "\u0922\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0922";
                                        speakOut("र्\u0922");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0922़";
                                        speakOut("\u0922"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0922";
                                        speakOut("\u0922");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[3][3]==1){
                                                speakOut("ढोलक");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u0922";
//                                speakOut("\u0922");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 3 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[3][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0923\u094D\u0930");
                                        keyCodelabel = "\u0923\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0923";
                                        speakOut("र्\u0923");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ण";
                                        speakOut("ण");

                                    } else {
                                        keyCodelabel = "\u0923";
                                        speakOut("\u0923");
                                    }
//                                keyCodelabel = "\u0923";
//                                speakOut("\u0923");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 3 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("ं");
                                keyCodelabel = "\u0902";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 0) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }
                        } else if (y > 3 * height/9 && y < 4 * height/9) {
                            if (x < width/6) {
                                if (flag[4][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0924\u094D\u0930");
                                        keyCodelabel = "\u0924\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0924";
                                        speakOut("र्\u0924");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "त";
                                        speakOut("त");


                                    } else {
                                        keyCodelabel = "\u0924";
                                        speakOut("\u0924");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[4][0]==1){
                                                speakOut("तरबूज़");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
//                                keyCodelabel = "\u0924";
//                                speakOut("\u0924");

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 4 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[4][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0925\u094D\u0930");
                                        keyCodelabel = "\u0925\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0925";
                                        speakOut("र्\u0925");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "थ";
                                        speakOut("थ");


                                    } else {
                                        keyCodelabel = "\u0925";
                                        speakOut("\u0925");


                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[4][1]==1){
                                                speakOut("थरमस");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 4 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[4][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0926\u094D\u0930");
                                        keyCodelabel = "\u0926\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0926";
                                        speakOut("र्\u0926");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "द";
                                        speakOut("द");

                                    } else {
                                        keyCodelabel = "\u0926";
                                        speakOut("\u0926");

                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[4][2]==1){
                                                speakOut("दवात");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

//                                keyCodelabel = "\u0926";

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 4 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
//                                speakOut("\u0926");
                                    myVib.vibrate(500);
                                    Log.d("key", "3");
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[4][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0927\u094D\u0930");
                                        keyCodelabel = "\u0927\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0927";
                                        speakOut("र्\u0927");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ध";
                                        speakOut("ध");

                                    } else {
                                        keyCodelabel = "\u0927";
                                        speakOut("\u0927");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[4][3]==1){
                                                speakOut("धनुष");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 4 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[4][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0928\u094D\u0930");
                                        keyCodelabel = "\u0928\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0928";
                                        speakOut("र्\u0928");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0928़";
                                        speakOut("\u0928"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0928";
                                        speakOut("\u0928");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[4][4]==1){
                                                speakOut("नल");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 4 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("ः");
                                keyCodelabel = "\u0903";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 1) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }
                        } else if (y > 4 * height/9 && y < 5 * height/9) {
                            if (x < width/6) {
                                if (flag[5][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092A\u094D\u0930");
                                        keyCodelabel = "\u092A\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092A";
                                        speakOut("र्\u092A");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "प";
                                        speakOut("प");

                                    } else {
                                        keyCodelabel = "\u092A";
                                        speakOut("\u092A");


                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[5][0]==1){
                                                speakOut("पतंग");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 5 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[5][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092B\u094D\u0930");
                                        keyCodelabel = "\u092B\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092B";
                                        speakOut("र्\u092B");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u092B़";
                                        speakOut("\u092B"+"nukta");


                                    } else {
                                        keyCodelabel = "\u092B";
                                        speakOut("\u092B");


                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[5][1]==1){
                                                speakOut("फल");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 5 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[5][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092C\u094D\u0930");
                                        keyCodelabel = "\u092C\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092C";
                                        speakOut("र्\u092C");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ब";
                                        speakOut("ब");

                                    } else {
                                        keyCodelabel = "\u092C";
                                        speakOut("\u092C");


                                    } CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[5][2]==1){
                                                speakOut("बतख");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 5 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[5][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092D\u094D\u0930");
                                        keyCodelabel = "\u092D\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092D";
                                        speakOut("र्\u092D");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "भ";
                                        speakOut("भ");
                                    } else {
                                        keyCodelabel = "\u092D";
                                        speakOut("\u092D");

                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[5][3]==1){
                                                speakOut("भालू");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 5 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[5][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092E\u094D\u0930");
                                        keyCodelabel = "\u092E\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092E";
                                        speakOut("र्\u092E");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "म";
                                        speakOut("म");


                                    } else {
                                        keyCodelabel = "\u092E";
                                        speakOut("\u092E");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[5][4]==1){
                                                speakOut("मछली");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 5 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("ँ");
                                keyCodelabel = "\u0901";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 2) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }

                        } else if (y > 5 * height/9 && y < 6 * height/9) {
                            if (x < width/6) {
                                if (flag[6][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u092F\u094D\u0930");
                                        keyCodelabel = "\u092F\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u092F";
                                        speakOut("र्\u092F");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u092F़";
                                        speakOut("\u092F"+"nukta");


                                    } else {
                                        keyCodelabel = "\u092F";
                                        speakOut("\u092F");
                                    }


                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 6 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[6][0]==1){
                                                speakOut("यज्ञ");
                                            }

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[6][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0930\u094D\u0930");
                                        keyCodelabel = "\u0930\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0930";
                                        speakOut("र्\u0930");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0930़";
                                        speakOut("\u0930"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0930";
                                        speakOut("\u0930");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[6][1]==1) {
                                                speakOut("रस्सी");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 6 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[6][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0932\u094D\u0930");
                                        keyCodelabel = "\u0932\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0932";
                                        speakOut("र्\u0932");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "\u0932़";
                                        speakOut("\u0932"+"nukta");


                                    } else {
                                        keyCodelabel = "\u0932";
                                        speakOut("\u0932");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[6][2]==1) {
                                                speakOut("लडका");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 6 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[6][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0935\u094D\u0930");
                                        keyCodelabel = "\u0935\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0935";
                                        speakOut("र्\u0935");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "व";
                                        speakOut("व");

                                    } else {
                                        keyCodelabel = "\u0935";
                                        speakOut("\u0935");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[6][3]==1){
                                                speakOut("वन");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 6 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[6][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0936\u094D\u0930");
                                        keyCodelabel = "\u0936\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0936";
                                        speakOut("र्\u0936");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "श";
                                        speakOut("श");


                                    } else {
                                        keyCodelabel = "\u0936";
                                        speakOut("\u0936");


                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[6][4]==1){
                                                speakOut("शहर");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 6 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("Nukta");
                                keyCodelabel="";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 3) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }
                        } else if (y > 6 * height/9 && y < 7 * height/9) {
                            if (x < width/6) {
                                if (flag[7][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0937\u094D\u0930");
                                        keyCodelabel = "\u0937\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0937";
                                        speakOut("र्\u0937");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ष";
                                        speakOut("ष");


                                    } else {
                                        keyCodelabel = "\u0937";
                                        speakOut("\u0937");
                                    }
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[7][0] == 1) {
                                                speakOut("षद्कोण");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();


                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 7 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(50);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[7][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0938\u094D\u0930");
                                        keyCodelabel = "\u0938\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0938";
                                        speakOut("र्\u0938");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "स";
                                        speakOut("स");


                                    } else {
                                        keyCodelabel = "\u0938";
                                        speakOut("\u0938");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[7][1] == 1){
                                            speakOut("सेब");}
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 7 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }

                                    }
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[7][2] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0939\u094D\u0930");
                                        keyCodelabel = "\u0939\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0939";
                                        speakOut("र्\u0939");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ह";
                                        speakOut("ह");

                                    } else {
                                        keyCodelabel = "\u0939";
                                        speakOut("\u0939");
                                    }


                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 7 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[7][2] == 1) {
                                                speakOut("हरिन");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[7][3] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0924\u094D\u0930\u094D\u0930");
                                        keyCodelabel = "\u0924\u094D\u0930\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0924\u094D\u0930";
                                        speakOut("र्\u0924\u094D\u0930");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "त्र";
                                        speakOut("त्र");

                                    } else {
                                        keyCodelabel = "\u0924\u094D\u0930";
                                        speakOut("\u0924\u094D\u0930");
                                    }


                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 7 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[7][3] == 1){
                                            speakOut("त्रिशुल");}
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[7][4] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0915\u094D\u0937\u094D\u0930");
                                        keyCodelabel = "\u0915\u094D\u0937\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0915\u094D\u0937";
                                        speakOut("र्\u0915\u094D\u0937");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "क्ष";
                                        speakOut("क्ष");


                                    } else {
                                        keyCodelabel = "\u0915\u094D\u0937";
                                        speakOut("\u0915\u094D\u0937");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[7][4]==1){
                                                speakOut("क्षत्रिय");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 7 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");
                                    myVib.vibrate(50);
                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("Trakaar");
                                keyCodelabel ="";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 4) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }
                        } else if (y > 7 * height/9 && y < 8 * height/9) {
                            if (x < width/6) {
                                if (flag[8][0] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u091C\u094D\u091E\u094D\u0930");
                                        keyCodelabel = "\u091C\u094D\u091E\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u091C\u094D\u091E";
                                        speakOut("र्\u091C\u094D\u091E");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "ज्ञ";
                                        speakOut("ज्ञ");


                                    } else {
                                        keyCodelabel = "\u091C\u094D\u091E";
                                        speakOut("\u091C\u094D\u091E");
                                    }

                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK=true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[8][0]==1){
                                                speakOut("ज्ञान");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 8 && j == 0) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "1");
                                    myVib.vibrate(500);
                                }
                            } else if (x > width/6 && x < 2 * width/6) {
                                if (flag[8][1] == 0) {
                                    if (flag2[1]) {
                                        speakOut("\u0936\u094D\u0930\u094D\u0930");
                                        keyCodelabel = "\u0936\u094D\u0930\u094D\u0930";


                                    } else if (flag2[2]) {
                                        keyCodelabel = "र्\u0930\u094D\u0936";
                                        speakOut("र्\u0930\u094D\u0936");


                                    } else if (flag2[0]) {
                                        keyCodelabel = "श्र";
                                        speakOut("श्र");

                                    } else {
                                        speakOut("\u0936\u094D\u0930");
                                        keyCodelabel = "\u0936\u094D\u0930";
                                    }

                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 8 && j == 1) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            if(flag[8][1] == 1){
                                                 speakOut("श्रम");
                                            }
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    Log.d("key", "2");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 2 * width/6 && x < 3 * width/6) {
                                if (flag[8][2] == 0) {
                                    speakOut("स्वर वर्ण");
                                    keyCodelabel = "\u093E";
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 8 && j == 2) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "3");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 3 * width/6 && x < 4 * width/6) {
                                if (flag[8][3] == 0) {

                                    speakOut("Ru");
                                    keyCodelabel = "\u0943";  CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.

                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 8 && j == 3) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "4");
                                    myVib.vibrate(50);
                                }
                            } else if (x > 4 * width/6 && x < 5 * width/6) {
                                if (flag[8][4] == 0) {
                                    speakOut("अ");
                                    keyCodelabel = "\u0905";
                                    CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                        public void onTick(long millisUntilFinished) {
                                            PROXIMITY_CHECK =true;
                                            Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            //Done timer time out.
                                            speakOut("आम");
                                            Log.i("Countdown Timer:","TimeOut");
                                        }
                                    }.start();
                                    myVib.vibrate(500);
                                    //Intialising the flag value of this column to 1 and rest of the keys to 0
                                    for (int i = 0; i < 11; i++) {
                                        for (int j = 0; j < 7; j++) {
                                            if (i == 8 && j == 4) {
                                                flag[i][j] = 1;
                                            } else {
                                                flag[i][j] = 0;
                                            }
                                        }
                                    }
                                    Log.d("key", "5");

                                }
                            }else if (x > 5 * width/6 && x < 6 * width/6) {
                                speakOut("Rafaar");
                                keyCodelabel ="";
                                CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                    public void onTick(long millisUntilFinished) {
                                        PROXIMITY_CHECK=true;
                                    }

                                    public void onFinish() {
                                        //Done timer time out.
                                        Log.i("Countdown Timer:","TimeOut");
                                    }
                                }.start();

                                //Intialising the flag value of this column to 1 and rest of the keys to 0
                                for (int i = 0; i < 11; i++) {
                                    for (int j = 0; j < 7; j++) {
                                        if (i == 0 && j == 5) {
                                            flag[i][j] = 1;
                                        } else {
                                            flag[i][j] = 0;
                                        }
                                    }
                                }
                                myVib.vibrate(50);
                            }
                        } else if (y > 8 * height/9 && y < 9 * height/9) {
                            if(x < width/6){
                                keyCodelabel = "";
                                key_touch(9,3,"Left",50);
                            }else if(x > width/6 && x< 2*width/6){
                                keyCodelabel = "";
                                key_touch(9,4,"Right",50);
                            }
                            if (x >2* width/6 && x < 5 * width/6) {
                                key_touch(9,0,"Space",50);
                            }
                            else if (x > 5 * width/6 && x < width){
                                key_touch(9,1,"Enter",50);
                            }
                        }


                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:

                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 7; j++) {
                                if (flag[i][j] == 1) {
                                    l = i;
                                    k = j;
                                }
                            }
                        }
                        if(event.getPointerCount()==3){
                            desetArc();
                            tri_touch = true;
                            multi_touch=false;
                            navigator_flag = false;
                            setTouchDownPoint(event.getX(pointerIndex), event.getY(pointerIndex));
                            keyCodelabel="";
                        }else if(event.getPointerCount()==2 && l<9 && l>0){
                            multi_touch = true;
                            navigator_flag = false;
                            tri_touch=false;
                            PointF f = new PointF();
                            f.x = event.getX(pointerIndex);
                            f.y = event.getY(pointerIndex);
                            Log.d("pointer_down_co", "(" + f.x + "," + f.y + ")");
                            setTouchDownPoint(f.x, f.y);
                            float touchMovementX = x - touchDownX;
                            float touchMovementY = y - touchDownY;

                            float theta = (float) Math.toDegrees(Math.atan2(touchMovementY,
                                    touchMovementX));
                            Log.d("theta value", Integer.toString((int) theta));
                            arc = findArc(theta);
                            myVib.vibrate(50);
                        }else if(event.getPointerCount() == 2 && l==9 && k==3){
                            Log.d("navigate", "left");
                            //split tap for left arrow
                            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
                            EditText edittext = (EditText) focusCurrent;
                            String s1 = edittext.getText().toString();
                            Editable editable = edittext.getText();
                            int start = edittext.getSelectionStart();
                            int selectionEnd = edittext.getSelectionEnd();
                            if (selectionEnd >= 0) {
                                // gives you the substring from start to the current cursor
                                // position
                                s1 = s1.substring(0, selectionEnd);
//                            speakOut_pitch(text);
                            }
                            String delimiter = " ";
                            int x = s1.lastIndexOf(delimiter);
                            Log.d("x",x+"");
                            if(x!=-1){
                                edittext.setSelection(x);
                                speakOut(getLastword(), (float) 0.8);
                            }else{
                                edittext.setSelection(0);
                                speakOut("Starting of the Text", (float) 0.8);
                            }


                        }else if(event.getPointerCount() == 2 && l==9 && k==4){
                            //split tap for right arrow
                            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
                            EditText edittext = (EditText) focusCurrent;
                            String s1 = edittext.getText().toString();
                            Editable editable = edittext.getText();
                            int start = edittext.getSelectionStart();
                            int selectionEnd = edittext.getSelectionEnd();
                            if (selectionEnd >= 0) {
                                // gives you the substring from the current cursor to end
                                // position
                                s1 = s1.substring(selectionEnd);
//                            speakOut_pitch(text);
                            }
                            String delimiter = " ";
                            int x = s1.indexOf(" ");
                            Log.d("x",x+"");
                            if(x!=-1){
                                edittext.setSelection(x+1);
                                speakOut(getLastword(), (float) 0.8);
                            }else{
                                edittext.setSelection(0);
                                speakOut("Ending of the Text", (float) 0.8);
                            }


                            Log.d("navigate","right");

                        }
                        Log.d("check_view_added", "View added");
//                    Log.d("touch", "ACTION_POINTER_DOWN");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        action_up =false;
                        boolean leftflag=false,rightflag=false;
                        if(tri_touch == true){
                            for (int i = 0; i < event.getPointerCount(); ++i) {
                                pointerIndex = i;
                                pointerId = event.getPointerId(pointerIndex);
                                Log.d("pointer id", Integer.toString(pointerId));

                                if (pointerId == 2) {
                                    PointF f1 = new PointF();
                                    f1.x = event.getX(pointerIndex);
                                    f1.y = event.getY(pointerIndex);
                                    if (touchDownX - f1.x > 170) {
//                                        Toast.makeText(mHostActivity,"left",Toast.LENGTH_SHORT).show();

                                        for (i = 0; i < 11; i++) {
                                            for (int j = 0; j < 7; j++) {
                                                if (i == 9 && j == 5) {
                                                    flag[i][j] = 1;
                                                } else {
                                                    flag[i][j] = 0;
                                                }
                                            }
                                        }
                                        Log.d("Direction","left");
                                    }
                                    // left to right swipe
                                    else if(f1.x - touchDownX >100) {
//                                        Toast.makeText(mHostActivity,"right",Toast.LENGTH_SHORT).show();

                                        for (i = 0; i < 11; i++) {
                                            for (int j = 0; j < 7; j++) {
                                                if (i == 9 && j == 0) {
                                                    flag[i][j] = 1;
                                                } else {
                                                    flag[i][j] = 0;
                                                }
                                            }
                                        }
                                        Log.d("Direction","right");
                                    }else if(f1.x-touchDownX<100 || touchDownX-f1.x<100 ){
                                        for (i = 0; i < 11; i++) {
                                            for (int j = 0; j < 7; j++) {
                                                if (i == 1 && j == 6) {
                                                    flag[i][j] = 1;
                                                } else {
                                                    flag[i][j] = 0;
                                                }
                                            }
                                        }
                                        Log.d("Direction","touch");
                                    }
                                    Log.d("x,y", f1.x + "," + f1.y);
                                }

                            }


                        }else if (multi_touch == true) {
                            Log.d("multitouch", "helo");
                            for (int i = 0; i < event.getPointerCount(); ++i) {
                                pointerIndex = i;
                                pointerId = event.getPointerId(pointerIndex);
                                Log.d("pointer id", Integer.toString(pointerId));

                                if (pointerId == 1) {
                                    PointF f1 = new PointF();
                                    f1.x = event.getX(pointerIndex);
                                    f1.y = event.getY(pointerIndex);

                                    handleMove((int) f1.x, (int) f1.y);

//                                MultiTouch mt1 = new MultiTouch(mHostActivity, null, null,f1.x,f1.y,"\u0916",mKeyboardView);
//                                mHostActivity.addContentView(mt1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                                    Log.d("x,y", f1.x + "," + f1.y);
                                }

                            }
                        } else {
                            // a pointer was moved
                            Log.d("touch", "movingfinger");

                            x = (int) event.getX();
                            y = (int) event.getY();
                            Display display1 = mHostActivity.getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display1.getSize(size);
                            width = mKeyboardView.getWidth();
                            height = mKeyboardView.getHeight();
                            Log.d("MovementX,Y", x + "," + y);
//                    Log.d("width,height",width+","+height);

//                    Log.d("keycodeclicked", Integer.toString(curkey));

                            myVib = (Vibrator) mHostActivity.getSystemService(Context.VIBRATOR_SERVICE);
                            am = (AudioManager) mHostActivity.getSystemService(Context.AUDIO_SERVICE);
                            Log.d("height",String.valueOf(y));

                            //leftswipe for reading the word
                            if(y<0){
                                    if(x < width/3){
                                        if(flag[10][0]==0){
                                            tv1 = (TextView) mHostActivity.findViewById(R.id.textView1);
                                            String text = tv1.getText().toString();
                                            speakOut(text);
                                            Log.d("swipe", text);

                                            for (int i = 0; i < 11; i++) {
                                            for (int j = 0; j < 7; j++) {
                                                if (i == 10 && j == 0) {
                                                    flag[i][j] = 1;
                                                } else {
                                                    flag[i][j] = 0;
                                                }
                                            }
                                        }}
                                    }else if(x > 2*width/3 && x < width){
                                        et1 = (EditText) mHostActivity.findViewById(R.id.editText1);
                                        speakOut(et1.getText().toString());
                                        Log.d("swipe","b");
                                        if(flag[10][1]==0){
                                        for (int i = 0; i < 11; i++) {
                                            for (int j = 0; j < 7; j++) {
                                                if (i == 10 && j == 1) {
                                                    flag[i][j] = 1;
                                                } else {
                                                    flag[i][j] = 0;
                                                }
                                            }
                                        }}
                                    }

                                }


                            //delete
                            if(y < 2*height/9 && y > 0 ){
                                if(x > 5 *width/6 && x <width){

                                    int r, s;
                                    r = 0;
                                    s = 6;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
//                                        Log.d("proximitydistance", String.valueOf(distancef));
                                    } else {
                                        if (flag[0][6] == 0) {
                                            speakOut("delete");
                                            keyCodelabel = "";
                                            CountDownTimer timer = new CountDownTimer(4000 /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
//                                                    long_backspace();
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                    if(action_up){
                                                    long_backspace();}
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 6) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            }
                            if (y < height/9 && y > 0) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 1;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {

                                        if (flag[1][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0915\u094D\u0930");
                                                keyCodelabel = "\u0915\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "\u0930\u094D\u0915";
                                                speakOut("\u0930\u094D\u0915");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0915\u093C";
                                                speakOut("\u0915" + "nukta");


                                            } else {
                                                speakOut("\u0915");
                                                keyCodelabel = "\u0915";


                                            }
                                            touchdX1 = x;
                                            touchdY1 = y;
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {

                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[1][0] == 1) {
                                                        speakOut("काम");
                                                    }
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 1 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(500);

                                            Log.d("keycodelabel", keyCodelabel);
                                            Log.d("key", "1");
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 1;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {

                                        if (flag[1][1] == 0) {

                                            if (flag2[1]) {
                                                speakOut("\u0916\u094D\u0930");
                                                keyCodelabel = "\u0916\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "\u0930\u094D\u0916";
                                                speakOut("\u0930\u094D\u0916");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0916\u093C";
                                                speakOut("\u0916" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0916";
                                                Log.d("keycodelabel", keyCodelabel);
                                                speakOut("\u0916");


                                            }
                                            touchdX1 = x;
                                            touchdY1 = y;
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
//                                                PROXIMITY_CHECK=true;
                                                    Log.i("Countdown Timer: ", String.valueOf(PROXIMITY_CHECK));
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[1][1] == 1) {
                                                        speakOut("खुशी");
                                                        PROXIMITY_CHECK = true;
                                                        Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    }
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            Log.d("timer", "aftertimer");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 1 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                            Log.d("key", "2");
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 1;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[1][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0917\u094D\u0930");
                                                keyCodelabel = "\u0917\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "\u0930\u094D\u0917";
                                                speakOut("\u0930\u094D\u0917");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0917\u093C";
                                                speakOut("\u0917" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0917";
                                                speakOut("\u0917");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[1][2] == 1) {
                                                        speakOut("गंगा");
                                                        PROXIMITY_CHECK = true;
                                                        Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            Log.d("timer", "aftertimer");

                                            Log.d("key", "3");
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 1 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 1;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[1][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0918\u094D\u0930");
                                                keyCodelabel = "\u0918\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "\u0930\u094D\u0918";
                                                speakOut("र्\u0918");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "घ";
                                                speakOut("घ");


                                            } else {
                                                keyCodelabel = "\u0918";
                                                speakOut("\u0918");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[1][3] == 1) {
                                                        speakOut("घंटा");
                                                        PROXIMITY_CHECK = true;
                                                        Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            Log.d("timer", "aftertimer");

//     keyCodelabel = "\u0918";
//                                speakOut("\u0918");
                                            Log.d("key", "4");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 1 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 1;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[1][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0919\u094D\u0930");
                                                keyCodelabel = "\u0919\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0919";
                                                speakOut("र्\u0919");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "";
                                                speakOut("ङ");

                                            } else {
                                                keyCodelabel = "\u0919";
                                                speakOut("\u0919");

                                            }
                                            Log.d("timer", "aftertimer");
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[1][4] == 1) {
                                                        PROXIMITY_CHECK = true;
                                                        Log.i("Proximity: ", String.valueOf(PROXIMITY_CHECK));
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u0919";
//                                speakOut("\u0919");
                                            myVib.vibrate(500);

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 1 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");

                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){

                                }
                            } else if (y > height/9 && y < 2 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 2;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[2][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091A\u094D\u0930");
                                                keyCodelabel = "\u091A\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091A";
                                                speakOut("र्\u091A");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "च";
                                                speakOut("च");

                                            } else {
                                                keyCodelabel = "\u091A";
                                                speakOut("\u091A");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[2][0] == 1) {
                                                        speakOut("चम्मच");
                                                        PROXIMITY_CHECK = true;
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();


//                                keyCodelabel = "\u091A";
//                                speakOut(keyCodelabel);


                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 2 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 2;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[2][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091B\u094D\u0930");
                                                keyCodelabel = "\u091B\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091B";
                                                speakOut("र्\u091B");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "छ";
                                                speakOut("छ");

                                            } else {
                                                keyCodelabel = "\u091B";
                                                speakOut("छ");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[2][1] == 1) {
                                                        speakOut("छतरी");
                                                        PROXIMITY_CHECK = true;
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();


//                                keyCodelabel = "\u091B";
//                                speakOut("\u091B");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 2 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 2;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[2][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091C\u094D\u0930");
                                                keyCodelabel = "\u091C\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091C";
                                                speakOut("र्\u091C");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u091C़";
                                                speakOut("\u091C" + "nukta");


                                            } else {
                                                keyCodelabel = "\u091C";
                                                speakOut("\u091C");

                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[2][2] == 1) {
                                                        speakOut("ज़मीन ");
                                                        PROXIMITY_CHECK = true;
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u091C";
//                                speakOut("\u091C");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 2 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 2;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[2][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091D\u094D\u0930");
                                                keyCodelabel = "\u091D\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091D";
                                                speakOut("र्\u091D");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "झ";
                                                speakOut("झ");

                                            } else {
                                                keyCodelabel = "\u091D";
                                                speakOut("\u091D");

                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[2][3] == 1) {
                                                        speakOut("झन्डा");
                                                        PROXIMITY_CHECK = true;
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
// /                                keyCodelabel = "\u091D";
//                                speakOut("\u091D");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 2 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 2;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[2][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091E\u094D\u0930");
                                                keyCodelabel = "\u091E\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091E";
                                                speakOut("र्\u091E");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ञ";
                                                speakOut("ञ");

                                            } else {
                                                keyCodelabel = "\u091E";
                                                speakOut("\u091E");

                                            }
//                                keyCodelabel = "\u091E";
//                                speakOut("\u091E");


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[2][4] == 1) {
//                                                    speakOut("झन्डा");
                                                        PROXIMITY_CHECK = true;
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 2 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x < 5*width/6 && x > width  ){

                                }
                            } else if (y > 2 * height/9 && y < 3 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 3;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[3][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091F\u094D\u0930");
                                                keyCodelabel = "\u091F\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091F";
                                                speakOut("र्\u091F");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ट";
                                                speakOut("ट");


                                            } else {
                                                keyCodelabel = "\u091F";
                                                speakOut("\u091F");
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[3][0] == 1) {
                                                        speakOut("टमाटर");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u091F";
//                                speakOut("\u091F");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 3 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 3;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[3][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0920\u094D\u0930");
                                                keyCodelabel = "\u0920\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0920";
                                                speakOut("र्\u0920");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ठ";
                                                speakOut("ठ");

                                            } else {
                                                keyCodelabel = "\u0920";
                                                speakOut("\u0920");
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[3][1] == 1) {
                                                        speakOut("ठप्पा");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u0920";
//                                speakOut("\u0920");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 3 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 3;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[3][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0921\u094D\u0930");
                                                keyCodelabel = "\u0921\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0921";
                                                speakOut("र्\u0921");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0921़";
                                                speakOut("\u0921" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0921";
                                                speakOut("\u0921");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[3][2] == 1) {
                                                        speakOut("डमरू");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u0921";
//                                speakOut("\u0921");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 3 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 3;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[3][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0922\u094D\u0930");
                                                keyCodelabel = "\u0922\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0922";
                                                speakOut("र्\u0922");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0922़";
                                                speakOut("\u0922" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0922";
                                                speakOut("\u0922");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[3][3] == 1) {
                                                        speakOut("ढोलक");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u0922";
//                                speakOut("\u0922");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 3 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 3;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[3][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0923\u094D\u0930");
                                                keyCodelabel = "\u0923\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0923";
                                                speakOut("र्\u0923");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ण";
                                                speakOut("ण");

                                            } else {
                                                keyCodelabel = "\u0923";
                                                speakOut("\u0923");
                                            }
//                                keyCodelabel = "\u0923";
//                                speakOut("\u0923");

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.

                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 3 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 6;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][0] == 0) {
                                            speakOut("ं");
                                            keyCodelabel = "\u0902";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 3 * height/9 && y < 4 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 4;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[4][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0924\u094D\u0930");
                                                keyCodelabel = "\u0924\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0924";
                                                speakOut("र्\u0924");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "त";
                                                speakOut("त");


                                            } else {
                                                keyCodelabel = "\u0924";
                                                speakOut("\u0924");
                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[4][0] == 1) {
                                                        speakOut("तरबूज़");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
//                                keyCodelabel = "\u0924";
//                                speakOut("\u0924");

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 4 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 4;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[4][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0925\u094D\u0930");
                                                keyCodelabel = "\u0925\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0925";
                                                speakOut("र्\u0925");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "थ";
                                                speakOut("थ");


                                            } else {
                                                keyCodelabel = "\u0925";
                                                speakOut("\u0925");


                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[4][1] == 1) {
                                                        speakOut("थरमस");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 4 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 4;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[4][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0926\u094D\u0930");
                                                keyCodelabel = "\u0926\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0926";
                                                speakOut("र्\u0926");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "द";
                                                speakOut("द");

                                            } else {
                                                keyCodelabel = "\u0926";
                                                speakOut("\u0926");

                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[4][2] == 1) {
                                                        speakOut("दवात");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

//                                keyCodelabel = "\u0926";

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 4 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
//                                speakOut("\u0926");
                                            myVib.vibrate(500);
                                            Log.d("key", "3");
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 4;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[4][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0927\u094D\u0930");
                                                keyCodelabel = "\u0927\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0927";
                                                speakOut("र्\u0927");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ध";
                                                speakOut("ध");

                                            } else {
                                                keyCodelabel = "\u0927";
                                                speakOut("\u0927");
                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[4][3] == 1) {
                                                        speakOut("धनुष");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 4 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 4;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[4][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0928\u094D\u0930");
                                                keyCodelabel = "\u0928\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0928";
                                                speakOut("र्\u0928");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0928़";
                                                speakOut("\u0928" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0928";
                                                speakOut("\u0928");
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[4][4] == 1) {
                                                        speakOut("नल");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 4 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][1] == 0) {
                                            speakOut("ः");
                                            keyCodelabel="\u0903";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 4 * height/9 && y < 5 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 5;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[5][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092A\u094D\u0930");
                                                keyCodelabel = "\u092A\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092A";
                                                speakOut("र्\u092A");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "प";
                                                speakOut("प");

                                            } else {
                                                keyCodelabel = "\u092A";
                                                speakOut("\u092A");


                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[5][0] == 1) {
                                                        speakOut("पतंग");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 5 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 5;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[5][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092B\u094D\u0930");
                                                keyCodelabel = "\u092B\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092B";
                                                speakOut("र्\u092B");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u092B़";
                                                speakOut("\u092B" + "nukta");


                                            } else {
                                                keyCodelabel = "\u092B";
                                                speakOut("\u092B");


                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[5][1] == 1) {
                                                        speakOut("फल");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 5 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 5;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[5][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092C\u094D\u0930");
                                                keyCodelabel = "\u092C\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092C";
                                                speakOut("र्\u092C");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ब";
                                                speakOut("ब");

                                            } else {
                                                keyCodelabel = "\u092C";
                                                speakOut("\u092C");


                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[5][2] == 1) {
                                                        speakOut("बतख");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 5 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 5;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[5][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092D\u094D\u0930");
                                                keyCodelabel = "\u092D\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092D";
                                                speakOut("र्\u092D");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "भ";
                                                speakOut("भ");
                                            } else {
                                                keyCodelabel = "\u092D";
                                                speakOut("\u092D");

                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[5][3] == 1) {
                                                        speakOut("भालू");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 5 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 5;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[5][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092E\u094D\u0930");
                                                keyCodelabel = "\u092E\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092E";
                                                speakOut("र्\u092E");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "म";
                                                speakOut("म");


                                            } else {
                                                keyCodelabel = "\u092E";
                                                speakOut("\u092E");
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[5][4] == 1) {
                                                        speakOut("मछली");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 5 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][2] == 0) {
                                            speakOut("ँ");
                                            keyCodelabel = "\u0901";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 5 * height/9 && y < 6 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 6;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[6][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u092F\u094D\u0930");
                                                keyCodelabel = "\u092F\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u092F";
                                                speakOut("र्\u092F");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u092F़";
                                                speakOut("\u092F" + "nukta");


                                            } else {
                                                keyCodelabel = "\u092F";
                                                speakOut("\u092F");
                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[6][0] == 1) {
                                                        speakOut("यज्ञ");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 6 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 6;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[6][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0930\u094D\u0930");
                                                keyCodelabel = "\u0930\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0930";
                                                speakOut("र्\u0930");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0930़";
                                                speakOut("\u0930" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0930";
                                                speakOut("\u0930");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[6][1] == 1) {
                                                        speakOut("रस्सी");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 6 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 6;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[6][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0932\u094D\u0930");
                                                keyCodelabel = "\u0932\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0932";
                                                speakOut("र्\u0932");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "\u0932़";
                                                speakOut("\u0932" + "nukta");


                                            } else {
                                                keyCodelabel = "\u0932";
                                                speakOut("\u0932");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[6][2] == 1) {
                                                        speakOut("लडका");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 6 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 6;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[6][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0935\u094D\u0930");
                                                keyCodelabel = "\u0935\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0935";
                                                speakOut("र्\u0935");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "व";
                                                speakOut("व");

                                            } else {
                                                keyCodelabel = "\u0935";
                                                speakOut("\u0935");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[6][3] == 1) {
                                                        speakOut("वन");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 6 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 6;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[6][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0936\u094D\u0930");
                                                keyCodelabel = "\u0936\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0936";
                                                speakOut("र्\u0936");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "श";
                                                speakOut("श");


                                            } else {
                                                keyCodelabel = "\u0936";
                                                speakOut("\u0936");


                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK = true;
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[6][4] == 1) {
                                                        speakOut("शहर");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 6 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][3] == 0) {
                                            speakOut("Nukta");
                                            keyCodelabel="";

                                            vowelBoolean = true;
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 6 * height/9 && y < 7 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 7;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[7][0] == 0) {


                                            if (flag2[1]) {
                                                speakOut("\u0937\u094D\u0930");
                                                keyCodelabel = "\u0937\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0937";
                                                speakOut("र्\u0937");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ष";
                                                speakOut("ष");


                                            } else {
                                                keyCodelabel = "\u0937";
                                                speakOut("\u0937");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[7][0] == 1) {
                                                        speakOut("षद्कोण");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 7 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 7;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[7][1] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0938\u094D\u0930");
                                                keyCodelabel = "\u0938\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0938";
                                                speakOut("र्\u0938");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "स";
                                                speakOut("स");


                                            } else {
                                                keyCodelabel = "\u0938";
                                                speakOut("\u0938");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[7][1] == 1) {
                                                        speakOut("सेब");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 7 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }

                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 7;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[7][2] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0939\u094D\u0930");
                                                keyCodelabel = "\u0939\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0939";
                                                speakOut("र्\u0939");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ह";
                                                speakOut("ह");

                                            } else {
                                                keyCodelabel = "\u0939";
                                                speakOut("\u0939");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[7][2] == 1) {
                                                        speakOut("हरिन");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 7 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 7;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[7][3] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0924\u094D\u0930\u094D\u0930");
                                                keyCodelabel = "\u0924\u094D\u0930\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0924\u094D\u0930";
                                                speakOut("र्\u0924\u094D\u0930");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "";
                                                speakOut(".");

                                            } else {
                                                keyCodelabel = "\u0924\u094D\u0930";
                                                speakOut("\u0924\u094D\u0930");
                                            }


                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[7][3] == 1) {
                                                        speakOut("त्रिशुल");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 7 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 7;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[7][4] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u0915\u094D\u0937\u094D\u0930");
                                                keyCodelabel = "\u0915\u094D\u0937\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0915\u094D\u0937";
                                                speakOut("र्\u0915\u094D\u0937");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "क्ष";
                                                speakOut("क्ष");


                                            } else {
                                                keyCodelabel = "\u0915\u094D\u0937";
                                                speakOut("\u0915\u094D\u0937");
                                            }

                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[7][4] == 1) {
                                                        speakOut("क्षत्रिय");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 7 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");
                                            myVib.vibrate(50);
                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][4] == 0) {
                                            speakOut("trakaar");
                                            keyCodelabel ="";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 7 * height/9 && y < 8 * height/9) {
                                if (x < width/6) {
                                    int r, s;
                                    r = 8;
                                    s = 0;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[8][0] == 0) {
                                            if (flag2[1]) {
                                                speakOut("\u091C\u094D\u091E\u094D\u0930");
                                                keyCodelabel = "\u091C\u094D\u091E\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u091C\u094D\u091E";
                                                speakOut("र्\u091C\u094D\u091E");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "ज्ञ";
                                                speakOut("ज्ञ");


                                            } else {
                                                keyCodelabel = "\u091C\u094D\u091E";
                                                speakOut("\u091C\u094D\u091E");
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[8][0] == 1) {
                                                        speakOut("ज्ञान");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 8 && j == 0) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "1");
                                            myVib.vibrate(500);
                                        }
                                    }
                                } else if (x > width/6 && x < 2 * width/6) {
                                    int r, s;
                                    r = 8;
                                    s = 1;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[8][1] == 0) {

                                            if (flag2[1]) {
                                                speakOut("\u0936\u094D\u0930\u094D\u0930");
                                                keyCodelabel = "\u0936\u094D\u0930\u094D\u0930";


                                            } else if (flag2[2]) {
                                                keyCodelabel = "र्\u0930\u094D\u0936";
                                                speakOut("र्\u0930\u094D\u0936");


                                            } else if (flag2[0]) {
                                                keyCodelabel = "";
                                                speakOut(".");

                                            } else {
                                                speakOut("\u0936\u094D\u0930");
                                                keyCodelabel = "\u0936\u094D\u0930";
                                            }
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[8][1] == 1) {
                                                        speakOut("श्रम");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 8 && j == 1) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "2");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 2 * width/6 && x < 3 * width/6) {
                                    int r, s;
                                    r = 8;
                                    s = 2;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[8][2] == 0) {
                                            speakOut("स्वर वर्ण");
                                            keyCodelabel = "\u093E";

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 8 && j == 2) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "3");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 3 * width/6 && x < 4 * width/6) {
                                    int r, s;
                                    r = 8;
                                    s = 3;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[8][3] == 0) {

                                            speakOut("Ru");
                                            keyCodelabel = "\u0943";
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 8 && j == 3) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "4");
                                            myVib.vibrate(50);
                                        }
                                    }
                                } else if (x > 4 * width/6 && x < 5 * width/6) {
                                    int r, s;
                                    r = 8;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[8][4] == 0) {
                                            speakOut("अ");
                                            keyCodelabel = "\u0905";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                                                    PROXIMITY_CHECK = true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    if (flag[8][4] == 1) {
                                                        speakOut("आम");
                                                    }
                                                    Log.i("Countdown Timer:", "TimeOut");
                                                }
                                            }.start();
                                            myVib.vibrate(500);
                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 8 && j == 4) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            Log.d("key", "5");

                                        }
                                    }
                                }else if( x > 5*width/6 && x < width  ){
                                    int r, s;
                                    r = 0;
                                    s = 4;
                                    if (PROXIMITY_CHECK) {
                                        if (P_CHECK_FLAGS[r][s] == 0) {

                                            //check any other p_check_flags are intialised to 1
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (P_CHECK_FLAGS[i][j] == 1) {
                                                        PREV_COORDINATE = true;
                                                    }
                                                }
                                            }

                                            //if there is no other flags intilaise then set a new starting point
                                            if (PREV_COORDINATE == false) {
                                                for (int i = 0; i < 11; i++) {
                                                    for (int j = 0; j < 7; j++) {
                                                        if (i == r && j == s) {
                                                            P_CHECK_FLAGS[i][j] = 1;
                                                        }
                                                    }
                                                }
                                                touchdX = x;
                                                touchdY = y;
                                            }


                                        }

                                        //differnce b/w two points
                                        touchdX1 = x - touchdX;
                                        touchdY1 = y - touchdY;
                                        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

                                        if (distance > THRESHOLD_DIST) {
                                            PROXIMITY_CHECK = false;
                                            //setting all the flags to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    P_CHECK_FLAGS[i][j] = 0;
                                                }
                                            }
                                            //setting prev cordinate to false
                                            PREV_COORDINATE = false;
                                        }
                                        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
                                        Log.d("proximitydistance", String.valueOf(distance));
                                    } else {
                                        if (flag[0][5] == 0) {
                                            speakOut("Rafaar");
                                            keyCodelabel ="";
                                            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                                                public void onTick(long millisUntilFinished) {
                                                    PROXIMITY_CHECK=true;
                                                }

                                                public void onFinish() {
                                                    //Done timer time out.
                                                    Log.i("Countdown Timer:","TimeOut");
                                                }
                                            }.start();

                                            //Intialising the flag value of this column to 1 and rest of the keys to 0
                                            for (int i = 0; i < 11; i++) {
                                                for (int j = 0; j < 7; j++) {
                                                    if (i == 0 && j == 5) {
                                                        flag[i][j] = 1;
                                                    } else {
                                                        flag[i][j] = 0;
                                                    }
                                                }
                                            }
                                            myVib.vibrate(50);
                                        }
                                    }
                                }
                            } else if (y > 8* height/9 && y < 9 * height/9) {
                                if(x < width/6){
                                    int r, s,vibrate_time;
                                    String label;
                                    r = 9;
                                    s = 3;
                                    vibrate_time = 50;
                                    label ="Left";
                                    keyCodelabel ="";
                                    if (PROXIMITY_CHECK) {
                                        proximity_check(r,s);
                                    } else {
                                        key_touch(r,s,label,vibrate_time);
                                    }
                                }else if(x > width/6 && x < 2*width/6){
                                    int r, s,vibrate_time;
                                    String label;
                                    r = 9;
                                    s = 4;
                                    vibrate_time = 50;
                                    label ="Right";
                                    keyCodelabel ="";
                                    if (PROXIMITY_CHECK) {
                                        proximity_check(r,s);
                                    } else {
                                        key_touch(r,s,label,vibrate_time);
                                    }
                                }
                                else if (x > 2 * width/6 && x < 5*width/6 ) {
                                    int r, s,vibrate_time;
                                    String label;
                                    r = 9;
                                    s = 0;
                                    vibrate_time = 50;
                                    label ="Space";

                                    if (PROXIMITY_CHECK) {
                                        proximity_check(r,s);
                                    } else {
                                        key_touch(r,s,label,vibrate_time);
                                    }
                                }
                                else if (x > 5 * width/6 && x <  width) {
                                    int r, s,vibrate_time;
                                    String label;
                                    r = 9;
                                    s = 1;
                                    vibrate_time = 50;
                                    label ="Enter";

                                    if (PROXIMITY_CHECK) {
                                        proximity_check(r,s);
                                    } else {
                                        key_touch(r,s,label,vibrate_time);
                                    }
                                }
                            }

                        }



                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d("move","action_up");
                        action_up =true;

                        //detect flag
                        l = 0;
                        k = 0;
                        for (int i = 0; i < 11; i++) {
                            for (int j = 0; j < 7; j++) {
                                if (flag[i][j] == 1) {
                                    l = i;
                                    k = j;
                                }
                            }
                        }
                        float a=0,b=0;
                        a= event.getX();
                        b= event.getY();
                        if(l==10 && k == 0){

                        }
                        if(l==10 && k == 1){

                        }
                        if (l == 0 && k == 3) {
                            count_nukta += 1;//count_nukta == 2
                            if (count_nukta > 2) {
                                flag2[2] = false;
                                flag2[1] = false;
                                flag2[0] = false;
                                count_nukta = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));

                            } else {
                                count_trakar = 1;
                                count_rafar = 1;
                                flag2[2] = false;
                                flag2[1] = false;
                                flag2[0] = true;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.nukta));
                            }

                        }
                        if (l == 0 && k == 4) {

                            count_trakar += 1;
                            if (count_trakar > 2) {
                                flag2[2] = false;
                                flag2[1] = false;
                                flag2[0] = false;
                                count_trakar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));

                            } else {
                                count_nukta = 1;
                                count_rafar = 1;
                                flag2[2] = false;
                                flag2[1] = true;
                                flag2[0] = false;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.trakar));
                            }
                        }

                        if (l == 0 && k == 5) {
                            count_rafar += 1;

                            if (count_rafar > 2) {
                                flag2[2] = false;
                                flag2[1] = false;
                                flag2[0] = false;
                                count_rafar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));

                            } else {
                                count_trakar = 1;
                                count_nukta = 1;
                                flag2[2] = true;
                                flag2[1] = false;
                                flag2[0] = false;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.rafar));

                            }
                        }


//                        else if(l==2 && k==6){
//                            Log.d("navigation","right");
//                        }else if(l==3 && k==6){
//                            Log.d("navigation","touch and leave");
//                        }
//                        if (l == 0 && k > 2) {
//                            keyCodelabel = "";
//                        }

                        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
                        EditText edittext = (EditText) focusCurrent;
                        String s = edittext.getText().toString();
                        Editable editable = edittext.getText();
                        int start = edittext.getSelectionStart();
                        MediaPlayer mp = MediaPlayer.create(mHostActivity, R.raw.thrash);
                        //three finger left swipe
                        if(l==9 && k==5){
                            String new_string,delimiter = " ";
//                            EditText et1 = (EditText) mHostActivity.findViewById(R.id.editText1);
                            log_chars('丐');
                            int lastindex ;
                            if(edittext.getSelectionEnd()!=0){
                              Log.d("selectionEnd",Integer.toString(edittext.getSelectionEnd()));
                                String part_1 = s.substring(0,edittext.getSelectionEnd());
                                String part_2 = s.substring(edittext.getSelectionEnd());
                                Log.d("parts", part_1 + "," + part_2);
                                int len = part_1.length();
                                Log.d("length", Integer.toString(len));
                                String c = part_1.charAt(len-1)+"";
                                Log.d("lastchar",c);
                                if(c.equals(" ")){
                                    part_1 = s.substring(0,edittext.getSelectionEnd()-1);
                                    Log.d("part1",part_1);
                                }

                                lastindex = part_1.lastIndexOf(delimiter);
                                if(lastindex!=-1) {
                                    new_string = part_1.substring(0, lastindex);
                                    Log.d("new_string",new_string);
                                    editable.clear();
                                    editable.append(new_string + " " + part_2);
                                    edittext.setSelection(new_string.length() + 1);
                                    Log.d("parts", part_1 + "," + part_2);
                                    Log.d("lastindex", String.valueOf(lastindex));
                                    mp.start();
                                }else{
                                    editable.clear();
                                    editable.append(part_2);
                                    edittext.setSelection(0);
                                    Log.d("parts", part_2);
                                    Log.d("lastindex", String.valueOf(lastindex));
                                    mp.start();
                                }

                            }
//                            if(s.length() != 0){
//                            if(lastindex!=-1){
//                            new_string = s.substring(0,lastindex);
//                          editable.clear();
//                                mp.start();
//                                editable.append(new_string);
//                            }else{
//                                editable.clear();
//                                mp.start();
//                            }
//
//                        }
                }
                        if(l==1 && k==6){
                            speakOut(s);
                            log_chars('丑');
                        }
                        if (l == 9 && k == 0) {                     //space
                            keyCodelabel = "";
                            int selectionEnd = edittext.getSelectionEnd();
                            String text = edittext.getText().toString();
                            if (selectionEnd >= 0) {
                                // gives you the substring from start to the current cursor
                                // position
                                text = text.substring(0, selectionEnd);
//                            speakOut_pitch(text);
                            }
                            String delimiter = " ";
                            int lastDelimiterPosition = text.lastIndexOf(delimiter);
                            String lastWord = lastDelimiterPosition == -1 ? text :
                                    text.substring(lastDelimiterPosition + delimiter.length());
                            speakOut(lastWord, (float) 0.8);
                            text = edittext.getText().toString();
                            StringBuffer buffer = new StringBuffer(text);
                            int cursor_position= edittext.getSelectionStart();
                            buffer.insert(cursor_position, " ");
                            edittext.setText(buffer.toString());
                            edittext.setSelection(cursor_position+1);


                        } else if (l == 9 && k == 3) {
                            /**code for clear option
                             * keyCodelabel = "";
                             *edittext.setText("");
//                             */
                            int selectionEnd = edittext.getSelectionEnd();
                            if (selectionEnd != 0) edittext.setSelection(selectionEnd - 1);
                            speakOut(getLastword());

                        } else if (l == 9 && k == 4) {
                            int selectionEnd = edittext.getSelectionEnd();
                            if(selectionEnd != s.length() )edittext.setSelection(selectionEnd+1);
                            speakOut(getLastword());
                        }
                        if (l == 0 && k == 6) {
                            Log.d("length of edit text", Integer.toString(s.length()));
                            String str = edittext.getText().toString();
                            String str1,str2;
                            int pos=0;
                            if (str.length() != 0) {

//                            mp.setVolume(2,3);
                                Log.d("selection", String.valueOf(edittext.getSelectionStart())+","+String.valueOf(str.length()));

                                if(edittext.getSelectionStart() == str.length() &&  edittext.getSelectionStart()!=0){
                                    char lastchar = str.charAt(edittext.getSelectionStart()-1);
                                    speakOut(String.valueOf(lastchar) + " deleted", 0.8f);

                                    str = str.substring(0,str.length()-1);
                                    pos = edittext.getSelectionStart()-1;
                                    edittext.setText(str);
                                    edittext.setSelection(pos);

                                }else if(edittext.getSelectionStart() < str.length() &&  edittext.getSelectionStart()!=0){
                                    char lastchar = str.charAt(edittext.getSelectionStart()-1);
                                    speakOut(String.valueOf(lastchar)+" deleted", 0.8f);
                                    mp.start();
                                    str1 = str.substring(0,(edittext.getSelectionStart()-1));
                                    str2 = str.substring((edittext.getSelectionStart()));
                                    str = str1+str2;

                                    pos = edittext.getSelectionStart()-1;
                                    Log.d("selection1", String.valueOf(edittext.getSelectionStart()));
                                    if(edittext.getSelectionStart()!=0){
                                        edittext.setText(str);
                                        edittext.setSelection(pos);}
                                }


                            }

                        }
                        if(l==10 && k==6){

                        }
                        if (tri_touch==false && multi_touch == false && action_down_flag && y>0) {     //condition for lift to type model

                            speakOut_pitch(keyCodelabel);
                            action_down_flag = false;
                            editable.insert(start, keyCodelabel);
//                            keyCodelabel = "";
//                            int selectionEnd = edittext.getSelectionEnd();
//                            String text = edittext.getText().toString();
//                            if (selectionEnd >= 0) {
//                                // gives you the substring from start to the current cursor
//                                // position
//                                text = text.substring(0, selectionEnd);
////                            speakOut_pitch(text);
//                            }
//                            String delimiter = " ";
//                            int lastDelimiterPosition = text.lastIndexOf(delimiter);
//                            String lastWord = lastDelimiterPosition == -1 ? text :
//                                    text.substring(lastDelimiterPosition + delimiter.length());
//                            speakOut_pitch(lastWord);

                            if (l > 0 && l < 10 && flag2[0]) {
                                flag2[0] = false;
                                count_nukta = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            } else if (l > 0 && l < 10 && flag2[1]) {
                                flag2[1] = false;
                                count_trakar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            } else if (l > 0 && l < 10 && flag2[2]) {
                                flag2[2] = false;
                                count_rafar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            }
                            action_down_flag = false;

                        } else if (multi_touch == true && keyCodelabel != null && radius > mInnerRadius) {

                            int new_start = edittext.getSelectionStart();
                            if (keyCodelabel.equals("\u0905")) {
                                editable.insert(new_start, vowels_aah[arc]);
                                speakOut_pitch(vowels_aah[arc]);
//                                keyCodelabel = "";
//                                int selectionEnd = edittext.getSelectionEnd();
//                                String text = edittext.getText().toString();
//                                if (selectionEnd >= 0) {
//                                    // gives you the substring from start to the current cursor
//                                    // position
//                                    text = text.substring(0, selectionEnd);
////                            speakOut_pitch(text);
//                                }
//                                String delimiter = " ";
//                                int lastDelimiterPosition = text.lastIndexOf(delimiter);
//                                String lastWord = lastDelimiterPosition == -1 ? text :
//                                        text.substring(lastDelimiterPosition + delimiter.length());
//                                speakOut_pitch(lastWord);

                            } else if (keyCodelabel.equals("ृ")) {
                                editable.insert(new_start, vowes_uuh[arc]);
                                speakOut_pitch(vowes_uuh[arc]);
//                                keyCodelabel = "";
//                                int selectionEnd = edittext.getSelectionEnd();
//                                String text = edittext.getText().toString();
//                                if (selectionEnd >= 0) {
//                                    // gives you the substring from start to the current cursor
//                                    // position
//                                    text = text.substring(0, selectionEnd);
////                            speakOut_pitch(text);
//                                }
//                                String delimiter = " ";
//                                int lastDelimiterPosition = text.lastIndexOf(delimiter);
//                                String lastWord = lastDelimiterPosition == -1 ? text :
//                                        text.substring(lastDelimiterPosition + delimiter.length());
//                                speakOut_pitch(lastWord);

                            } else if (keyCodelabel.equals("ा")) {
                                editable.insert(new_start, vowels[arc]);
                                speakOut_pitch(vowels[arc]);
//                                keyCodelabel = "";
//                                int selectionEnd = edittext.getSelectionEnd();
//                                String text = edittext.getText().toString();
//                                if (selectionEnd >= 0) {
//                                    // gives you the substring from start to the current cursor
//                                    // position
//                                    text = text.substring(0, selectionEnd);
////                            speakOut_pitch(text);
//                                }
//                                String delimiter = " ";
//                                int lastDelimiterPosition = text.lastIndexOf(delimiter);
//                                String lastWord = lastDelimiterPosition == -1 ? text :
//                                        text.substring(lastDelimiterPosition + delimiter.length());
//                                speakOut_pitch(lastWord);

                            } else {

                                editable.insert(new_start, keyCodelabel + vowels[arc]);
                                speakOut_pitch(keyCodelabel + vowels[arc]);
//                                keyCodelabel = "";
//                                int selectionEnd = edittext.getSelectionEnd();
//                                String text = edittext.getText().toString();
//                                if (selectionEnd >= 0) {
//                                    // gives you the substring from start to the current cursor
//                                    // position
//                                    text = text.substring(0, selectionEnd);
////                            speakOut_pitch(text);
//                                }
//                                String delimiter = " ";
//                                int lastDelimiterPosition = text.lastIndexOf(delimiter);
//                                String lastWord = lastDelimiterPosition == -1 ? text :
//                                        text.substring(lastDelimiterPosition + delimiter.length());
//                                speakOut_pitch(lastWord);

                            }
                            if (l > 0 && l < 10 && flag2[0]) {
                                flag2[0] = false;
                                count_nukta = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            } else if (l > 0 && l < 10 && flag2[1]) {
                                flag2[1] = false;
                                count_trakar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            } else if (l > 0 && l < 10 && flag2[2]) {
                                flag2[2] = false;
                                count_rafar = 1;
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                            }

                        }



                        //setting touch flags to default values

                        for (int i = 0; i < 11; i++) {
                            for (int j = 0; j < 7; j++) {

                                flag[i][j] = 0;

                            }
                        }
                        keyCodelabel = "";
                        multi_touch = false;
                        tri_touch =false;
                        navigator_flag = false;
                        action_down_flag= false;
                        PROXIMITY_CHECK = false;
                        PREV_COORDINATE =false;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        View focusCurrent1 = mHostActivity.getWindow().getCurrentFocus();
                        EditText edittext1 = (EditText) focusCurrent1;
                        String s1 = edittext1.getText().toString();
                        Editable editable1 = edittext1.getText();
                        int start1 = edittext1.getSelectionStart();

                        if (event.getPointerId(pointerIndex) != 0 && radius > mInnerRadius && tri_touch == false && multi_touch && navigator_flag==false) {
                            editable1.insert(start1, keyCodelabel + vowels[arc]);
                            speakOut_pitch(keyCodelabel + vowels[arc]);
                            if (flag2[0] || flag2[1] || flag2[2]) {
                                mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexkbd));
                                flag2[0] = false;
                                flag2[1] = false;
                                flag2[2] = false;

                            }
                        }
                        if (event.getPointerId(pointerIndex) != 0 && tri_touch == false && multi_touch == false && navigator_flag) {
                        }
                        break;

                }
                if (event.getPointerCount() < 2 && pointerId == 0){
                    multi_touch = false;
                    navigator_flag=false;
                }
                invalidate();
                return true;
            }
            private void long_backspace() {
                if(action_up == false){
                View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
                EditText edittext = (EditText) focusCurrent;
                edittext.setText("");
                MediaPlayer mp = MediaPlayer.create(mHostActivity, R.raw.thrash);
                mp.start();}
            }


        };
    }




    /**
     * The key (code) handler.
     */
    public OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
        public final static int CodePrev = 55000;
        public final static int CodeAllLeft = 55001;
        public final static int CodeLeft = 55002;
        public final static int CodeRight = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext = 55005;
        public final static int CodeClear = 55006;
        public int keyCodeClicked;



        @Override
        public void onKey(final int primaryCode, int[] keyCodes) {
//            Log.d("Count",Integer.toString(count));

            Log.d("On", "onkey");
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class) return;

            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            String text = editable.toString();
            int start = edittext.getSelectionStart();
            // Apply the key to the edittext
            if (primaryCode == CodeLeft) {
                Log.d("On", "codeleft");
                int selectionEnd = edittext.getSelectionEnd();
                if(selectionEnd != 0) edittext.setSelection(selectionEnd-1);
//                setContentDescription("leftarrow");
            } else if (primaryCode == CodeRight) {
                Log.d("On", "coderight");
                int selectionEnd = edittext.getSelectionEnd();
                if(selectionEnd != text.length() )edittext.setSelection(selectionEnd+1);
            } else if (primaryCode == CodeAllLeft) {
                Log.d("On", "codeallleft");
                edittext.setSelection(0);
            } else if (primaryCode == CodeAllRight) {
                Log.d("On", "codeallright");
                edittext.setSelection(edittext.length());
            } else if (primaryCode == CodeDelete) {
                String str = edittext.getText().toString();
                String str1,str2;
                int pos=0;
                if (str.length() != 0) {

//                            mp.setVolume(2,3);
                    Log.d("selection", String.valueOf(edittext.getSelectionStart())+","+String.valueOf(str.length()));

                    if(edittext.getSelectionStart() == str.length() &&  edittext.getSelectionStart()!=0){
                        char lastchar = str.charAt(edittext.getSelectionStart()-1);
                        speakOut(String.valueOf(lastchar), 0.8f);
                        MediaPlayer mp = MediaPlayer.create(mHostActivity, R.raw.thrash);
                        mp.start();
                        str = str.substring(0,str.length()-1);
                        pos = edittext.getSelectionStart()-1;
                        edittext.setText(str);
                        edittext.setSelection(pos);

                    }else if(edittext.getSelectionStart() < str.length() &&  edittext.getSelectionStart()!=0){
                        char lastchar = str.charAt(edittext.getSelectionStart()-1);
                        speakOut(String.valueOf(lastchar), 0.8f);
                        MediaPlayer mp = MediaPlayer.create(mHostActivity, R.raw.thrash);
                        mp.start();
                        str1 = str.substring(0,(edittext.getSelectionStart()-1));
                        str2 = str.substring((edittext.getSelectionStart()));
                        str = str1+str2;

                        pos = edittext.getSelectionStart()-1;
                        Log.d("selection1", String.valueOf(edittext.getSelectionStart()));
                        if(edittext.getSelectionStart()!=0){
                            edittext.setText(str);
                            edittext.setSelection(pos);}
                    }


                }} else if (primaryCode == (23664)) {
                //for trakar
                if (flag_trakar == 0) {
                    new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.trakar);
                    flag_trakar = 1;
                } else {
                    new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.hexkbd);
                    flag_trakar = 0;
                }
            } else if (primaryCode == (23665)) {
                //for rafar
                if (flag_rafar == 0) {
                    new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.rafar);
                    flag_rafar = 1;
                    flag_rafar = 1;
                } else {
                    new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.hexkbd);
                    flag_rafar = 0;
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }

        }

        @Override
        public void onPress(int keyCode) {
            keyCodeClicked = keyCode;
//            Log.d("On","onpress");
            //setiing the keycode currently clicked
//            mt.setKeyCode(keyCode);

        }


        @Override
        public void onRelease(int primaryCode) {
//            Log.d("On","onrelease");
        }

        @Override
        public void onText(CharSequence text) {
//            Log.d("On","onText");
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class) return;

            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            if (editable != null && start > 0) editable.delete(start - 1, start);

        }

        @Override
        public void swipeRight() {
            int arr[] = new int[32];
            onKey(32, arr);

        }


        @Override
        public void swipeUp() {
            if (flag_num == 0) {
//                new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.numpad);
                flag_num = 1;
            } else {
                new CustomKeyboard(mHostActivity, null, R.id.keyboardview, R.xml.hexkbd);
                flag_num = 0;
            }
        }
    };


    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(int)}.
     *
     * @param host     The hosting activity.
     * @param viewid   The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public CustomKeyboard(Activity host, AttributeSet attrs, int viewid, int layoutid) {
        super(host, attrs);

        mHostActivity = host;
//        mainActivityObj = new MainActivity();

        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);
//        mKeyboardView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mKeyboardView.setLongClickable(true);
        mKeyboardView.isProximityCorrectionEnabled();
        mKeyboardView.setFocusable(true);
//        mKeyboardView.setOnLongClickListener(longlistener);
        mKeyboardView.setOnTouchListener(mTouchlistener);
        tts = new TextToSpeech(mHostActivity, onInit);
        tts1 = new TextToSpeech(mHostActivity, onInit1);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        //setting vowel flags to  false
        for(int i = 0 ;i<3;i++){flag2[i] = false;}

    }

    public void log_chars(char c){

//        //append & delete code for FT
             et1 = (EditText) mHostActivity.findViewById(R.id.editText1);
            et1.append(c+"");
            et1.getText().delete(et1.getText().length() - 1,
                    et1.getText().length());


    }



    /**
     * /**
     * Returns whether the CustomKeyboard is visible.
     */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * Make the CustomKeyboard invisible.
     */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext = (EditText) mHostActivity.findViewById(resid);

        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        final Button next = (Button) mHostActivity.findViewById(R.id.nextText);
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);// Restore input type
                next.setEnabled(false);
                return true; // Consume touch event
            }
        });

        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

    }

    public void key_touch(int r, int s, String label,int vibrate_time){
        if (flag[r][s] == 0) {
            speakOut(label);
            myVib.vibrate(vibrate_time);
            CountDownTimer timer = new CountDownTimer(COUNT_DOWN_TIME /*For how long should timer run*/, 500 /*time interval after which `onTick()` should be called*/) {

                public void onTick(long millisUntilFinished) {
                    PROXIMITY_CHECK =true;
                    Log.i("Countdown Timer: ", "seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    //Done timer time out
                    Log.i("Countdown Timer:","TimeOut");
                }
            }.start();
            //Intialising the flag value of this column to 1 and rest of the keys to 0
            assign_flag(r,s);
            Log.d("key", "1");

        }
    }
    public void assign_flag(int r,int s){
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == r && j == s) {
                    flag[i][j] = 1;
                } else {
                    flag[i][j] = 0;
                }
            }
        }
    }
    public void proximity_check(int r,int s){
        if (P_CHECK_FLAGS[r][s] == 0) {

            //check any other p_check_flags are intialised to 1
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 7; j++) {
                    if (P_CHECK_FLAGS[i][j] == 1) {
                        PREV_COORDINATE = true;
                    }
                }
            }

            //if there is no other flags intilaise then set a new starting point
            if (PREV_COORDINATE == false) {
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 7; j++) {
                        if (i == r && j == s) {
                            P_CHECK_FLAGS[i][j] = 1;
                        }
                    }
                }
                touchdX = x;
                touchdY = y;
            }


        }

        //differnce b/w two points
        touchdX1 = x - touchdX;
        touchdY1 = y - touchdY;
        distance = (float) Math.sqrt((touchdX1 * touchdX1) + (touchdY1 * touchdY1));

        if (distance > THRESHOLD_DIST_LAST_ROW) {
            PROXIMITY_CHECK = false;
            //setting all the flags to 0
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 7; j++) {
                    P_CHECK_FLAGS[i][j] = 0;
                }
            }
            //setting prev cordinate to false
            PREV_COORDINATE = false;
        }
        Log.d("proximity", "touchMovement X: " + String.valueOf(touchdX) + "touchMovementY:" + String.valueOf(touchdY));
        Log.d("proximitydistance", String.valueOf(distance));

    }
    public String getLastword(){

        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        EditText edittext = (EditText) focusCurrent;
        String s = edittext.getText().toString();
        int selectionEnd = edittext.getSelectionEnd();
        selectionEnd = edittext.getSelectionEnd();
        String text = edittext.getText().toString();
        if (selectionEnd >= 0) {
            // gives you the substring from start to the current cursor
            // position
            text = text.substring(0, selectionEnd);
//                            speakOut_pitch(text);
        }
        String delimiter = " ";
        int lastDelimiterPosition = text.lastIndexOf(delimiter);
        String lastWord = lastDelimiterPosition == -1 ? text :
                text.substring(lastDelimiterPosition + delimiter.length());
        return lastWord;
    }


}