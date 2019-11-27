package ai;


public class Transposition2 {
    private int tableSize;
    private Entry2[] entries;

    public Transposition2(final int tableSize) {
        this.tableSize = tableSize;
        this.entries = new Entry2[tableSize];
    }

    public boolean lookup(final int hashValue, boolean myTurn) {
        /**
         * Tìm kiếm các giá trị của 1 phần tử trong bảng băm*/
        final int tableSize = entries.length;
        Entry2 entry = entries[hashValue % tableSize];
        if(entry != null)
            if(entry.getMyTurn() == myTurn)
                return entry.getHashValue() == hashValue;
        return false;
    }

    public int getBestValue(final int hashValue) {
        /**
         * Trả về giá trị lượng giá tại 1 trạng thái của map*/
        Entry2 entry = entries[hashValue % tableSize];
        return entry.getBestValue();
    }

    public int getLowerBound(final int hashValue) {
        Entry2 entry = entries[hashValue % tableSize];
        return entry.getLowerBound();
    }

    public int getUpperBound(final int hashValue) {
        Entry2 entry = entries[hashValue % tableSize];
        return entry.getUpperBound();
    }

    public void store(final Entry2 entry) {
        /**
         * lưu 1 giá trị vào bảng băm*/
        entries[entry.getHashValue() % tableSize] = entry;
    }
}
