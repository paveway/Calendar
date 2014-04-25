package info.paveway.calendar;

/**
 * ページ番号リスナー
 *
 * @version 1.0 新規作成
 */
public interface PageNoListener {

    /**
     * 更新した時に呼び出される。
     *
     * @param pageNo ページ番号
     */
    void onUpdate(int pageNo);
}
