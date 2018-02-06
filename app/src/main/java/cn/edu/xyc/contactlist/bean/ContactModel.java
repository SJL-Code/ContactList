package cn.edu.xyc.contactlist.bean;

/**
 * @author sjl
 */
public class ContactModel {

    /**
     * 联系人
     */
    private String name;

    /**
     * 显示拼音的首字母
     */
    private String letters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
