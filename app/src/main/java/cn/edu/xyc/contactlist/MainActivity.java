package cn.edu.xyc.contactlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.xyc.contactlist.adapter.ContactListAdapter;
import cn.edu.xyc.contactlist.bean.ContactModel;
import cn.edu.xyc.contactlist.util.PinyinUtils;
import cn.edu.xyc.contactlist.widget.ClearEditText;
import cn.edu.xyc.contactlist.widget.WaveSideBar;

/**
 * @author sjl
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ClearEditText mSearchClearEditText;

    private RecyclerView mContactListRecyclerView;

    private WaveSideBar mSideBar;

    private List<ContactModel> mContactList;

    private ContactListAdapter mAdapter;

    private PinyinComparator mPinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mSearchClearEditText = (ClearEditText) findViewById(R.id.searchClearEditText);
        mContactListRecyclerView = (RecyclerView) findViewById(R.id.contactListRecyclerView);
        mSideBar = (WaveSideBar) findViewById(R.id.waveSideBar);
    }

    private void initData() {
        mContactList = filledData(getResources().getStringArray(R.array.data));
        mPinyinComparator = new PinyinComparator();
        // 根据a-z进行排序源数据
        Collections.sort(mContactList, mPinyinComparator);

        Log.e(TAG, "根据a-z进行排序源数据: " + new Gson().toJson(mContactList));
    }

    private void initEvent() {
        final LinearLayoutManager mManager = new LinearLayoutManager(this);
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactListRecyclerView.setLayoutManager(mManager);
        mAdapter = new ContactListAdapter(this, mContactList);
        mContactListRecyclerView.setAdapter(mAdapter);

        // 设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                Log.e(TAG, "选中的字母：" + letter);
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForCharValue(letter.charAt(0));
                if (position != -1) {
                    mManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });

        mSearchClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private List<ContactModel> filledData(String[] data) {
        List<ContactModel> mContactList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            ContactModel contactModel = new ContactModel();
            contactModel.setName(data[i]);

            // 汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(data[i]);
            // 获取拼音的首字母并且将该字母转大写
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式,判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                contactModel.setLetters(sortString.toUpperCase());
            } else {
                contactModel.setLetters("#");
            }
            mContactList.add(contactModel);
        }
        return mContactList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<ContactModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mContactList;
        } else {
            filterDateList.clear();
            for (ContactModel contactModel : mContactList) {
                String name = contactModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(contactModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, mPinyinComparator);
        mAdapter.updateList(filterDateList);
    }
}
