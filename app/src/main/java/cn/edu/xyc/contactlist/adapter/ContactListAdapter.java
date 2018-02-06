package cn.edu.xyc.contactlist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.xyc.contactlist.R;
import cn.edu.xyc.contactlist.bean.ContactModel;
import cn.edu.xyc.contactlist.widget.FilletButton;

/**
 * @author sjl
 */
public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContactModel> mContactList;
    private LayoutInflater mInflater;

    public ContactListAdapter(Context mContext, List<ContactModel> mContactList) {
        this.mContext = mContext;
        this.mContactList = mContactList;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * @param mContactList
     */
    public void updateList(List<ContactModel> mContactList) {
        this.mContactList = mContactList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = mInflater.inflate(R.layout.contact_list_item_layout, parent, false);
        viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        int charValue = getCharValueForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置,则认为是第一次出现
        if (position == getPositionForCharValue(charValue)) {
            myViewHolder.mLettersFilletButton.setVisibility(View.VISIBLE);
            myViewHolder.mLettersFilletButton.setText(this.mContactList.get(position).getLetters());
        } else {
            myViewHolder.mLettersFilletButton.setVisibility(View.GONE);
        }

        myViewHolder.mIconImageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.icon));
        myViewHolder.mNameTextView.setText(this.mContactList.get(position).getName());
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        FilletButton mLettersFilletButton;
        ImageView mIconImageView;
        TextView mNameTextView;

        private MyViewHolder(View itemView) {
            super(itemView);

            mLettersFilletButton = itemView.findViewById(R.id.lettersFilletButton);
            mIconImageView = itemView.findViewById(R.id.iconImageView);
            mNameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    private int getCharValueForPosition(int position) {
        return mContactList.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForCharValue(int charValue) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortString = mContactList.get(i).getLetters();
            char firstChar = sortString.toUpperCase().charAt(0);
            if (firstChar == charValue) {
                return i;
            }
        }
        return -1;
    }
}
