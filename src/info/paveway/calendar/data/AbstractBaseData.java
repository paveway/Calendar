package info.paveway.calendar.data;

/**
 * データの抽象基底クラス
 *
 * @author masatsugu
 *
 */
public abstract class AbstractBaseData {

    /** ID */
    protected int mId;

    /**
     * コンストラクタ
     */
    public AbstractBaseData() {
        super();
    }

    /**
     * IDを設定する。
     *
     * @param id ID
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * IDを返却する。
     *
     * @return ID
     */
    public int getId() {
        return mId;
    }
}
