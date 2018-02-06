package cn.edu.xyc.contactlist.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class FilletButton extends Button {

    /**
     * 画文本的画笔
     */
    private Paint mTextPaint;

    /**
     * 文本的内容,默认为"文"字
     */
    private String mText = "文";

    /**
     * 文本的大小,默认25
     */
    private int mTextSize = 25;

    /**
     * 文本的颜色,默认白色
     */
    private String mTextColor = "#FFFFFF";

    /**
     * 画圆的画笔
     */
    private Paint mCirclePaint;

    /**
     * 圆的颜色,默认粉色
     */
    private String mCircleColor = "#FF4081";

    public FilletButton(Context context) {
        super(context);
    }

    public FilletButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    private void initPaint(Context context) {
        // 设置画文本的画笔
        mTextPaint = new Paint();
        // 防锯齿
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        // Style.STROKE：空心,Style.FILL：实心
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.parseColor(mTextColor));
        mTextPaint.setStrokeWidth(20);

        // 设置画圆的画笔
        mCirclePaint = new Paint();
        // 防锯齿
        mCirclePaint.setAntiAlias(true);
        // Style.STROKE：空心,Style.FILL：实心
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.parseColor(mCircleColor));
        mCirclePaint.setStrokeWidth(20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 圆环默认的大小
        int mDefaultSize = 100;

        // 分别获取测量模式 和 测量大小
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        // 如果是精确度模式,就按xml中定义的来
        // 如果是最大值模式,就按我们定义的来
        if (widthMode == View.MeasureSpec.AT_MOST && heightMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultSize, mDefaultSize);
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultSize, heightSize);
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mDefaultSize);
        } else {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getWidth();
        int mHeight = getHeight();
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mCirclePaint);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 为基线到字体上边框的距离
        float top = fontMetrics.top;
        // 为基线到字体下边框的距离
        float bottom = fontMetrics.bottom;
        // 基线中间点的y轴计算公式
        int baseLineY = (int) (mWidth / 2 - top / 2 - bottom / 2);
        canvas.drawText(mText, mWidth / 2, baseLineY, mTextPaint);
    }

    public void setText(String mText) {
        this.mText = mText;
        postInvalidate();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        postInvalidate();
    }

    public void setTextColor(String mTextColor) {
        this.mTextColor = mTextColor;
        postInvalidate();
    }

    public void setCircleColor(String mCircleColor) {
        this.mCircleColor = mCircleColor;
        postInvalidate();
    }
}
