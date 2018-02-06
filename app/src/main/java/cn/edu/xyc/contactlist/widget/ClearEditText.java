package cn.edu.xyc.contactlist.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import cn.edu.xyc.contactlist.R;

/**
 * 自定义自带清除功能的EditText
 */
public class ClearEditText extends EditText {

    /**
     * ClearEditText左边的图片资源
     */
    protected Drawable leftImage;

    /**
     * ClearEditText右边的图片资源
     */
    protected Drawable rightImage;

    public ClearEditText(Context context) {
        super(context);

        init(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    /**
     * 初始化
     */
    public void init(Context context, AttributeSet attrs) {

        // 先对自定义的属性进行处理.只有当前控件是在布局文件中使用的时候，attrs才不是null
        if (attrs != null) {
            // 获取由自定义属性组成的数组
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditTextStyle);

            // 获取设置在ClearEditText左边的图片资源
            leftImage = typedArray.getDrawable(R.styleable.ClearEditTextStyle_leftImage);

            // 获取设置在ClearEditText右边的图片资源
            rightImage = typedArray.getDrawable(R.styleable.ClearEditTextStyle_rightImage);

            // 回收释放
            typedArray.recycle();
        }

        setDrawable();

        /**
         * 对EditText文本状态监听
         */
        this.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

        /**
         * 对EditText焦点状态监听
         */
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDrawable();
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(leftImage, null, leftImage, null);
                }
            }
        });
    }

    /**
     * 给EditText设置带有删除功能图片
     */
    public void setDrawable() {
        if (this.length() < 1) {
            // 此方法意思是在EditText添加图片 参数： left - 左边图片id top - 顶部图片id right - 右边图片id bottom - 底部图片id
            this.setCompoundDrawablesWithIntrinsicBounds(leftImage, null, leftImage, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(leftImage, null, rightImage, null);
        }
    }

    /**
     * 设置删除事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rightImage != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();

            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;

            if (rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}
