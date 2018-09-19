package com.example.pulis.pulistitlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.pulis.pulistitlebar.Utils.Utils;

import java.util.ArrayList;


public class PulisTilteBar extends ViewGroup{
    private Context context;
    private EditText editText;
    /*绘制titleBar颜色
    radials半径
    eventX,eventY绘制坐标
    */
    private Paint[] paints;
    private Paint textPaint;
    private int[] radials;
    private float[] eventX;
    private float[] eventY;
    //是否绘制圆
    private boolean isDraw;
    //tHeight=titleBar默认初始化高度
    //tBackground=titleBar默认初始化颜色
    private int tHeight;
    private int tBackground;
    //titleBar Item index默认为0
    private static int index;
    //变化颜色之前的index
    private static int nowIndex;
    //titleBar绘制颜色
    private static int[] titleColors={
            Color.rgb(119,43,154),Color.rgb(154,43,72)
            ,Color.rgb(199,138,47),Color.rgb(92,142,65)
            ,Color.rgb(63,83,100)
    };
    private static String[] itemText={
            "页面1","页面2","页面3","页面4","页面5"
    };
    //item 默认5
    private int itemCount;
    private int oldIndex;
    private float shapeWitch;
    private float shapeHeight;
    //应该绘制颜色的个数
    private ArrayList count;
    //初始化
    private AnimationDrawable animationDrawable;
    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    for (int i=0;i<count.size();i++){
                        int i1= (int) count.get(i);
                        //为数组中每一个 radial 增加半径
                        radials[i1]+=Utils.dip2px(context,10);
                        //如果半径超出屏幕则将其从数组中移除并且设置title颜色
                        if (radials[i1]>=getWidth()+Utils.dip2px(context,10)){
                            count.remove(i);
                            setBackgroundColor(titleColors[i1]);
                            radials[i1]=0;
                        }
                    }
                    invalidate();
                    removeMessages(1);
                    sendEmptyMessage(1);
                    break;
                case 2:
                    //计算小白条应该移动的距离
                    float dx=(index*shapeWitch)-(oldIndex*shapeWitch);
                    moveX+=dx/20;
                    //限制moveX不超出dx
                    //如果相等那么移除消息
                    if (Math.abs(moveX)>=Math.abs(dx)){
                        moveX=0;
                        oldIndex=index;

                        removeMessages(1);
                        break;
                    }
                    invalidate();
                    removeMessages(2);
                    sendEmptyMessage(2);
                    break;
            }
        }
    };
    private float moveX = 0;
    private float textWidth;
    private float textHeight;
    private OnItemSelectChangeListener oISCListener;
    private EditTextOnFocusChangeListener eTOFCListener;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private void intiView(){
        itemCount=5;
        paints=new Paint[itemCount];
        radials=new int[itemCount];
        eventY=new float[itemCount];
        eventX=new float[itemCount];
        titleColors[0]=tBackground;
        editText=new EditText(context);
        //editText.setCursorVisible(false);
        editText.setWidth(Utils.dip2px(context,250));
        editText.setBackground(null);
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        editText.setSingleLine();
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i==KeyEvent.KEYCODE_ENTER){//修改回车键功能

                }
                return false;
            }
        });
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    InputMethodManager imm = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.clearFocus();
                    //editText.setFocusable(false);
                    //完成自己的事件


                }
                return false;
            }
        });

        linearLayout=new LinearLayout(context);
        linearLayout.setFocusable(true);//设置默认获得焦点
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setBackgroundResource(R.drawable.search_bar);
        imageView =new ImageView(context);
        imageView.setBackgroundResource(R.drawable.search_1);
        imageView.setLayoutParams(new LayoutParams(Utils.dip2px(context,35),Utils.dip2px(context,35)));
        linearLayout.setPadding(Utils.dip2px(context,10),0,0,0);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(imageView);
        linearLayout.addView(editText);
        addView(linearLayout);
        //设置drawable图像
//        drawable= getResources().getDrawable(R.drawable.search_animation1);
//        //设置图像大小
//        drawable.setBounds(50, 0, 150, 120);
//        //为editText 设置drawableRight图像
//        editText.setCompoundDrawables(drawable,null,null,null);
//        editText.setBackgroundResource(R.drawable.search_bar);
//        //设置editText 焦点监听器
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    imageView.setBackgroundResource(R.drawable.search_animation1);
                    animationDrawable= (AnimationDrawable) imageView.getBackground();
                    animationDrawable.start();

                }else {
                    imageView.setBackgroundResource(R.drawable.search_animation2);
                    animationDrawable= (AnimationDrawable) imageView.getBackground();
                    animationDrawable.start();
                }
                if (eTOFCListener!=null){
                    eTOFCListener.onFocusChange(view,b);
                }
            }
        });
        //添加子视图
        //this.addView(editText);
        //设置小白条高度
        shapeHeight=Utils.dip2px(context,2);
        isDraw=false;
        oldIndex=0;
        itemCount=itemText.length;
        count=new ArrayList();
        textPaint=new Paint();
        textPaint.setTextSize(Utils.dip2px(context,20));
        //设置粗体字
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        for (int i=0;i<itemCount;i++){
            radials[i]=0;
            paints[i]=new Paint();
            //设置抗锯齿
            paints[i].setAntiAlias(true);
            paints[i].setColor(titleColors[i]);
            ////绘制圆形并且填充
            paints[i].setStyle(Paint.Style.FILL_AND_STROKE);
        }
        index=0;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTitleColor(canvas);
        drawTitleText(canvas);
        drawShape(canvas);
        //初始化文字
    }

    private void drawShape(Canvas canvas) {
        if (oldIndex!=index) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(2);
                }
            });
            Paint paint = new Paint();
            //设置画笔填充
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            float startX = oldIndex * shapeWitch+moveX;
            float startY = textHeight - shapeHeight;
            float endX = (oldIndex + 1) * shapeWitch+moveX;
            float endxY = textHeight;
            canvas.drawRect(startX, startY, endX, endxY, paint);
        }else {
            Paint paint = new Paint();
            //设置画笔填充
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            float startX = index * shapeWitch;
            float startY = textHeight - shapeHeight;
            float endX = (index + 1) * shapeWitch;
            float endxY = textHeight;
            canvas.drawRect(startX, startY, endX, endxY, paint);
        }

    }

    private void drawTitleText(Canvas canvas){
            for(int i=0;i<itemCount;i++){
                textPaint.setColor(Color.GRAY);
                if (i==index){
                    textPaint.setColor(Color.WHITE);
                }
                String word = itemText[i];
                Rect bounds = new Rect();
                textPaint.getTextBounds(word,0,word.length(),bounds);
                //计算每个文字的宽和高
                int wordWidth = bounds.width();
                int wordHeight = bounds.height();
                float wordX = i*textWidth+(textWidth/2-wordWidth/2);
                float wordY = textHeight-wordHeight/2-Utils.dip2px(context,5);
                eventX[i]=wordX+wordWidth/2;
                eventY[i]=wordY;
                canvas.drawText(word,wordX,wordY,textPaint);
        }
    }
    //绘制圆形
    private void drawTitleColor(Canvas canvas){
        if (isDraw){
            //只绘制数组中对应index的圈圈颜色变化
            if (count!=null&&count.size()>0) {
                for (int i = 0; i < count.size(); i++) {
                    float X = eventX[(int) count.get(i)];
                    float Y = eventY[(int) count.get(i)];
                    Paint paint = paints[(int) count.get(i)];
                    int r = radials[(int) count.get(i)];
                    if (r>0) {
                        canvas.drawCircle(X, Y, r, paint);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }else {
            //默认初始化颜色
            setBackgroundColor(tBackground);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float X=event.getX();
                float Y=event.getY();
                if (Y>textHeight-Utils.dip2px(context,40)) {
                    index = (int) (X / textWidth);
                }
                if (index!=nowIndex){
                    count.add(index);
                    isDraw=true;
                    invalidate();
                    nowIndex=index;
                    if (oISCListener!=null) {
                        oISCListener.selectItem(index);
                    }
                }
                break;
        }
        return true;
    }

    public PulisTilteBar(Context context) {
        this(context,null);
    }
    public PulisTilteBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PulisTilteBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        setValue(context,attrs);
        intiView();
    }

    @Override
    protected void onLayout(boolean bl, int l, int t, int r, int b) {
            View et=getChildAt(0);
            et.layout(l+Utils.dip2px(context,10),b-Utils.dip2px(context,90),
                    r- Utils.dip2px(context,10),b-Utils.dip2px(context,40));
    }

    private void setValue(Context context,AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.PulisTilteBar);
        tBackground=typedArray.getColor(R.styleable.PulisTilteBar_tBackground,Color.BLUE);
        tHeight= (int) typedArray.getDimension(R.styleable.PulisTilteBar_tHeight,Utils.dip2px(context,130));
        typedArray.recycle();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,tHeight);
        setMeasuredDimension(widthMeasureSpec,tHeight);
        textHeight=getMeasuredHeight();
        textWidth=getMeasuredWidth()/itemCount;
        shapeWitch=textWidth;

    }

    public EditText getEditText() {
        return editText;
    }

    public int gettHeight() {
        return tHeight;
    }

    public void settHeight(int tHeight) {
        this.tHeight = tHeight;
    }

    public int gettBackground() {
        return tBackground;
    }

    public void settBackground(int tBackground) {
        this.tBackground = tBackground;
    }

    public  int[] getTitleColors() {
        return titleColors;


    }

    public  void setTitleColors(int[] titleColors) {
        this.titleColors = titleColors;
        for(int i=0;i<itemCount;i++){
            paints[i].setColor(titleColors[i]);
        }
    }

    public  String[] getItemText() {
        return itemText;
    }

    public  void setItemText(String[] itemText) {
        this.itemText = itemText;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    public void setOnItemSelectChangeListener(OnItemSelectChangeListener listener){
        this.oISCListener= listener;
    }
    public void setEditTextOnFocusChangeListener(EditTextOnFocusChangeListener listener){
        this.eTOFCListener= listener;
    }

    public interface OnItemSelectChangeListener{
        void selectItem(int index);
    }
    public interface EditTextOnFocusChangeListener{
        void onFocusChange(View view, boolean b);
    }
}
