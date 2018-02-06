package cn.edu.xyc.contactlist;

import java.util.Comparator;

import cn.edu.xyc.contactlist.bean.ContactModel;

/**
 * @author sjl
 */
public class PinyinComparator implements Comparator<ContactModel> {
    @Override
    public int compare(ContactModel o1, ContactModel o2) {
        if (o1.getLetters().equals("@") || o2.getLetters().equals("#")) {
            return -1;
        } else if (o1.getLetters().equals("#") || o2.getLetters().equals("@")) {
            return 1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }
}
