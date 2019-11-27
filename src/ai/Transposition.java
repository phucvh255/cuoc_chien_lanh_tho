package ai;


public class Transposition {
    private int tableSize;
    private Entry[] entries;

    public Transposition(final int tableSize) {
        this.tableSize = tableSize;
        this.entries = new Entry[tableSize];
    }

    public boolean lookup(final int hashValue, boolean myTurn) {
        /**
         * Tìm kiếm các giá trị của 1 phần tử trong bảng băm*/
        final int tableSize = entries.length;
        Entry entry = entries[hashValue % tableSize];
        if(entry != null)
            if(entry.getMyTurn() == myTurn)
                return entry.getHashValue() == hashValue;
        return false;
    }

    public int getBestValue(final int hashValue) {
        /**
         * Trả về giá trị lượng giá tại 1 trạng thái của map*/
        Entry entry = entries[hashValue % tableSize];
        return entry.getBestValue();
    }

    public void store(final Entry entry) {
        /**
         * lưu 1 giá trị vào bảng băm*/
        entries[entry.getHashValue() % tableSize] = entry;
    }
}
