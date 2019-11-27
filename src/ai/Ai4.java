package ai;

import java.util.*;
import map.MapS;

public class Ai4
{
    private static final int DEPTH = 7;
    private static final int DEPTH_PATH = 12;
    private static final int MAXIMUM = 80000;
    private static final int MINIMUM = -80000;
    private int[] priceofspace; // lượng giá ô trống
    private int amountnode = 0; // đếm số node được duyệt qua
    private boolean crack; // biến kiểm tra xem map đã phân vùng chưa
    private boolean alphabeta;
    private Transposition2 transpositionTable; // bảng băm
    private int [][][] zobristTable; // mảng băm zobristTable
    private int [][] evaluateTable; // mảng nhân chập dùng để đánh giá trạng thái trò chơi
    private int [] parameter;
    private static final int VAT = 5;
    private static final int VS = 1;
    private static final int KN = 31;
    private static final int KE = 11;

    public Ai4() {
        this.priceofspace = new int[5];
        this.crack = false;
        this.alphabeta = true;
        transpositionTable = new Transposition2(200000);
        zobristTable = new int[15][15][5];
        // tạo một instance của lớp Random
        Random rd = new Random();
        // sinh ngẫu nhiên giá trị của bảng băm zobristTable
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                for(int k = 0; k < 5; k++) {
                    int temp = -1;
                    while (temp < 1)
                        temp = rd.nextInt();
                    zobristTable[i][j][k] = temp;
                }
            }
        }
        evaluateTable = new int [21][21];
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                evaluateTable[i][j] = i + 1 + j;
                evaluateTable[20 - i][j] = i + 1 + j;
                evaluateTable[20 - i][20 - j] = i + 1 + j;
                evaluateTable[i][20 - j] = i + 1 + j;
            }
        }
        parameter = new int [2];
        parameter[0] = 31;
        parameter[1] = 11;
    }

    public Ai4(int [] parameter) {
        this.priceofspace = new int[5];
        this.crack = false;
        this.alphabeta = true;
        transpositionTable = new Transposition2(200000);
        zobristTable = new int[15][15][5];
        // tạo một instance của lớp Random
        Random rd = new Random();
        // sinh ngẫu nhiên giá trị của bảng băm zobristTable
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                for(int k = 0; k < 5; k++) {
                    int temp = -1;
                    while (temp < 1)
                        temp = rd.nextInt();
                    zobristTable[i][j][k] = temp;
                }
            }
        }
        evaluateTable = new int [21][21];
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                evaluateTable[i][j] = i + 1 + j;
                evaluateTable[20 - i][j] = i + 1 + j;
                evaluateTable[20 - i][20 - j] = i + 1 + j;
                evaluateTable[i][20 - j] = i + 1 + j;
            }
        }
        parameter = new int[2];
        this.parameter[0] = parameter[0];
        this.parameter[1] = parameter[1];
    }

    public int findDirection(final MapS map, final int x, final int y, final int xe, final int ye) {
        /**
         *Input:
         * map: bản đồ hiện tại
         * x: tung độ xe đỏ hiện tại
         * y: hoành độ xe đỏ hiện tại
         * xe: tung độ xe xanh hiện tại
         * ye: hoành độ xe xanh hiện tại
         *Output: direction tương ứng với hướng cần di chuyển
         */
        if (!map.isSpace(x - 1, y) && !map.isSpace(x + 1, y) && !map.isSpace(x, y + 1) && !map.isSpace(x, y - 1)) {
            return -1;
        }
        if (this.crack) {
            return this.findPathV1(map, x, y);
        }
        // check xem bản đồ đã phân vùng chưa?
        if (!this.enemyInside(new MapS(map), xe, ye, x, y)) {
            this.crack = true;
            // nếu đã phân vùng thì gọi hàm findPathV1, dùng BFS để tìm đường
            return this.findPathV1(map, x, y);
        }
        // nếu chưa thì sử dụng alpha-beta để tìm đường
        final int t = this.minimax(map, x, y, xe, ye);
        // nếu alpha-beta không tìm được đường thì trả về đường đi là ô trống bất kì xung quanh
        if (t < 0) {
            if (map.isSpace(x - 1, y)) {
                return 1;
            }
            if (map.isSpace(x + 1, y)) {
                return 3;
            }
            if (map.isSpace(x, y - 1)) {
                return 2;
            }
            if (map.isSpace(x, y + 1)) {
                return 0;
            }
        }
        return t;
    }

    private int findPathV1(final MapS map, final int x, final int y) {
        /**
         * Input:
         * map: bản đồ hiện tại
         * x, y: tọa độ của xe hiện tại
         * Output: nước đi có điểm cao nhất
         * Sử dụng trong trường hợp bản đồ đã phân vùng
         * Đánh giá điểm cho 4 ô xung quanh ô hiện tại,
         * với mỗi ô còn trống, thực hiện thuật toán để
         * đánh giá điểm và tìm ra ô có điểm lớn nhất
         * -> nước đi tiếp theo*/
        int max = -1000;
        int dir = -1;
        if (map.isSpace(x - 1, y)) {
            final MapS m = new MapS(map);
            m.setRed(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 1;
            }
        }
        if (map.isSpace(x + 1, y)) {
            final MapS m = new MapS(map);
            m.setRed(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 3;
            }
        }
        if (map.isSpace(x, y - 1)) {
            final MapS m = new MapS(map);
            m.setRed(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, 12);
            if (temp > max) {
                max = temp;
                dir = 2;
            }
        }
        if (map.isSpace(x, y + 1)) {
            final MapS m = new MapS(map);
            m.setRed(x, y + 1);
            final int temp = this.searchPath(m, x, y + 1, 12);
            if (temp > max) {
                max = temp;
                dir = 0;
            }
        }
        return dir;
    }

    private int searchPath(final MapS map, final int x, final int y, final int depth) {
        /**
         * Input:
         * depth: độ sâu của cây tìm kiếm, ban đầu bằng 12
         * Output: điểm của node cần đáng giá
         * Đánh giá điểm cho từng node trong cây tìm kiếm
         * Bằng giải thuật tìm kiếm theo chiều rộng*/
        int t = -999;
        if (depth <= 0) {
            return this.numberofegdes(map, x, y);
        }
        if (map.isSpace(x - 1, y)) {
            final MapS m = new MapS(map);
            m.setRed(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x + 1, y)) {
            final MapS m = new MapS(map);
            m.setRed(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y - 1)) {
            final MapS m = new MapS(map);
            m.setRed(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y + 1)) {
            final MapS m = new MapS(map);
            m.setRed(x, y + 1);
            final int temp = this.searchPath(m, x, y + 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (t < -990) {
            return -depth;
        }
        return t;
    }

    private int countEdgesAStep(final MapS map, final ArrayList<Point> arp) {
        /**
         * Hàm lượng giá node
         * tính số lượng node có thể đi trong nước
         * đi tiếp theo từ trạng thái hiện tại*/
        int count = 0;
        final ArrayList<Point> temp = new ArrayList<Point>();
        while (!arp.isEmpty()) {
            final Point p = arp.remove(0);
            final int x = p.x;
            final int y = p.y;
            if (map.isSpaceAndNotTemp(x + 1, y)) {
                count += map.amountSpacesAround(x + 1, y);
                temp.add(new Point(x + 1, y));
                map.setTemp(x + 1, y);
            }
            if (map.isSpaceAndNotTemp(x - 1, y)) {
                count += map.amountSpacesAround(x - 1, y);
                temp.add(new Point(x - 1, y));
                map.setTemp(x - 1, y);
            }
            if (map.isSpaceAndNotTemp(x, y + 1)) {
                count += map.amountSpacesAround(x, y + 1);
                temp.add(new Point(x, y + 1));
                map.setTemp(x, y + 1);
            }
            if (map.isSpaceAndNotTemp(x, y - 1)) {
                count += map.amountSpacesAround(x, y - 1);
                temp.add(new Point(x, y - 1));
                map.setTemp(x, y - 1);
            }
        }
        arp.addAll(temp);
        return count;
    }

    private int luonggiacanh(final MapS map, final int x, final int y, final int xe, final int ye, final boolean myturn) {
        /**
         * Hàm lượng giá node ở độ sâu depth = 14
         * tính số lượng node có thể đi trong vòng
         * 2 nước đi tiếp theo từ trạng thái hiện tại*/
        int mysum = 0;
        int hissum = 0;
        final ArrayList<Point> red = new ArrayList<Point>();
        final ArrayList<Point> green = new ArrayList<Point>();
        red.add(new Point(x, y));
        green.add(new Point(xe, ye));
        if (myturn) {
            while (!red.isEmpty() || !green.isEmpty()) {
                mysum += this.countEdgesAStep(map, red);
                hissum += this.countEdgesAStep(map, green);
            }
        }
        else {
            while (!red.isEmpty() || !green.isEmpty()) {
                hissum += this.countEdgesAStep(map, green);
                mysum += this.countEdgesAStep(map, red);
            }
        }
//        if (myturn) {
//            mysum += this.countEdgesAStep(map, red);
//        }
//        while (!red.isEmpty() || !green.isEmpty()) {
//            hissum += this.countEdgesAStep(map, green);
//            mysum += this.countEdgesAStep(map, red);
//        }
        return mysum - hissum;
    }

    private int numberofegdes(final MapS map, final int x, final int y) {
        /**
         * Input:
         * Output: điểm của 1 node lá
         * Tính điểm của một node lá trong cây trò chơi
         * bằng cách tính số nước đi có thể từ node đó*/
        int max = 0;
        // mảng arp lưu các điểm đánh giả định
        final ArrayList<Point> arp = new ArrayList<Point>();
        arp.add(new Point(x, y));
        int xs = x;
        int ys = y;
        // nếu 1 ô đang trống và chưa được duyệt qua là nước đi giả định
        if (map.isSpaceAndNotTemp(xs + 1, ys)) {
            int t = 0;
            // đếm các ô trống xung quanh
            t += map.amountSpacesAround(xs + 1, ys);
            // đánh dấu ô đó là có thể đánh
            map.setTemp(xs + 1, ys);
            // thêm ô đó vào mảng arp
            arp.add(new Point(xs + 1, ys));
            while (!arp.isEmpty()) {
                // lấy 1 điểm từ mảng arp ra và chọn nó là nước đi tiếp theo
                // điểm ban đầu là phần tử đầu tiên trong mảng
                //-> sử dụng duyệt theo chiều rộng để tính điểm của node lá
                final Point p = arp.remove(0);
                xs = p.x;
                ys = p.y;
                // lặp lại các bước như trên để tính điểm cho đến khi không tìm được nước đi nữa
                if (map.isSpaceAndNotTemp(xs + 1, ys)) {
                    t += map.amountSpacesAround(xs + 1, ys);
                    map.setTemp(xs + 1, ys);
                    arp.add(new Point(xs + 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs - 1, ys)) {
                    t += map.amountSpacesAround(xs - 1, ys);
                    map.setTemp(xs - 1, ys);
                    arp.add(new Point(xs - 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs, ys + 1)) {
                    t += map.amountSpacesAround(xs, ys + 1);
                    map.setTemp(xs, ys + 1);
                    arp.add(new Point(xs, ys + 1));
                }
                if (map.isSpaceAndNotTemp(xs, ys - 1)) {
                    t += map.amountSpacesAround(xs, ys - 1);
                    map.setTemp(xs, ys - 1);
                    arp.add(new Point(xs, ys - 1));
                }
            }
            if (t > max) {
                max = t;
            }
        }
        if (map.isSpaceAndNotTemp(xs - 1, ys)) {
            int t = 0;
            t += map.amountSpacesAround(xs - 1, ys);
            map.setTemp(xs - 1, ys);
            arp.add(new Point(xs - 1, ys));
            while (!arp.isEmpty()) {
                final Point p = arp.remove(0);
                xs = p.x;
                ys = p.y;
                if (map.isSpaceAndNotTemp(xs + 1, ys)) {
                    t += map.amountSpacesAround(xs + 1, ys);
                    map.setTemp(xs + 1, ys);
                    arp.add(new Point(xs + 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs - 1, ys)) {
                    t += map.amountSpacesAround(xs - 1, ys);
                    map.setTemp(xs - 1, ys);
                    arp.add(new Point(xs - 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs, ys + 1)) {
                    t += map.amountSpacesAround(xs, ys + 1);
                    map.setTemp(xs, ys + 1);
                    arp.add(new Point(xs, ys + 1));
                }
                if (map.isSpaceAndNotTemp(xs, ys - 1)) {
                    t += map.amountSpacesAround(xs, ys - 1);
                    map.setTemp(xs, ys - 1);
                    arp.add(new Point(xs, ys - 1));
                }
            }
            if (t > max) {
                max = t;
            }
        }
        if (map.isSpaceAndNotTemp(xs, ys + 1)) {
            int t = 0;
            t += map.amountSpacesAround(xs, ys + 1);
            map.setTemp(xs, ys + 1);
            arp.add(new Point(xs, ys + 1));
            while (!arp.isEmpty()) {
                final Point p = arp.remove(0);
                xs = p.x;
                ys = p.y;
                if (map.isSpaceAndNotTemp(xs + 1, ys)) {
                    t += map.amountSpacesAround(xs + 1, ys);
                    map.setTemp(xs + 1, ys);
                    arp.add(new Point(xs + 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs - 1, ys)) {
                    t += map.amountSpacesAround(xs - 1, ys);
                    map.setTemp(xs - 1, ys);
                    arp.add(new Point(xs - 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs, ys + 1)) {
                    t += map.amountSpacesAround(xs, ys + 1);
                    map.setTemp(xs, ys + 1);
                    arp.add(new Point(xs, ys + 1));
                }
                if (map.isSpaceAndNotTemp(xs, ys - 1)) {
                    t += map.amountSpacesAround(xs, ys - 1);
                    map.setTemp(xs, ys - 1);
                    arp.add(new Point(xs, ys - 1));
                }
            }
            if (t > max) {
                max = t;
            }
        }
        if (map.isSpaceAndNotTemp(xs, ys - 1)) {
            int t = 0;
            t += map.amountSpacesAround(xs, ys - 1);
            map.setTemp(xs, ys - 1);
            arp.add(new Point(xs, ys - 1));
            while (!arp.isEmpty()) {
                final Point p = arp.remove(0);
                xs = p.x;
                ys = p.y;
                if (map.isSpaceAndNotTemp(xs + 1, ys)) {
                    t += map.amountSpacesAround(xs + 1, ys);
                    map.setTemp(xs + 1, ys);
                    arp.add(new Point(xs + 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs - 1, ys)) {
                    t += map.amountSpacesAround(xs - 1, ys);
                    map.setTemp(xs - 1, ys);
                    arp.add(new Point(xs - 1, ys));
                }
                if (map.isSpaceAndNotTemp(xs, ys + 1)) {
                    t += map.amountSpacesAround(xs, ys + 1);
                    map.setTemp(xs, ys + 1);
                    arp.add(new Point(xs, ys + 1));
                }
                if (map.isSpaceAndNotTemp(xs, ys - 1)) {
                    t += map.amountSpacesAround(xs, ys - 1);
                    map.setTemp(xs, ys - 1);
                    arp.add(new Point(xs, ys - 1));
                }
            }
            if (t > max) {
                max = t;
            }
        }
        return max;
    }

    private int minimax(final MapS map, final int x, final int y, final int xe, final int ye) {
        long startTime = System.currentTimeMillis();
        final int depth = 14;
        int direction = -1;
        int value = -80000;
        // chú ý cách chọn alpha và beta để thuật toán hiệu quả nhất
        final int alpha = -80000;
        final int beta = 80000;
        this.amountnode = 0;
        // Đối với mỗi hướng đi
        if (map.isSpace(x, y + 1)) {
            // giả lập 1 bản đồ mới từ bản đồ hiện tại
            final MapS mapTemp = new MapS(map);
            // Thực hiện nước đi trên bản đồ giả lập
            mapTemp.setRed(x, y + 1);
            final int temp = this.search(mapTemp, x, y + 1, xe, ye, depth - 1, alpha, beta, false);
            if (temp > value) {
                value = temp;
                direction = 0;
            }
        }
        if (map.isSpace(x, y - 1)) {
            final MapS mapTemp = new MapS(map);
            mapTemp.setRed(x, y - 1);
            final int temp = this.search(mapTemp, x, y - 1, xe, ye, depth - 1, alpha, beta, false);
            if (temp > value) {
                value = temp;
                direction = 2;
            }
        }
        if (map.isSpace(x + 1, y)) {
            final MapS mapTemp = new MapS(map);
            mapTemp.setRed(x + 1, y);
            final int temp = this.search(mapTemp, x + 1, y, xe, ye, depth - 1, alpha, beta, false);
            if (temp > value) {
                value = temp;
                direction = 3;
            }
        }
        if (map.isSpace(x - 1, y)) {
            final MapS mapTemp = new MapS(map);
            mapTemp.setRed(x - 1, y);
            final int temp = this.search(mapTemp, x - 1, y, xe, ye, depth - 1, alpha, beta, false);
            if (temp > value) {
                value = temp;
                direction = 1;
            }
        }
        long endTime = System.currentTimeMillis();
//        System.out.println("Do: " + this.amountnode);
//        System.out.println("Time: " + (endTime - startTime));
        return direction;
    }

    private int search(final MapS map, final int x, final int y, final int xe, final int ye, final int depth, int alpha, int beta, final boolean myTurn) {
        ++this.amountnode;
        int max = -80000;
        int min = 80000;
        if(myTurn) {
            // tính giá trị băm tương ứng với một trạng thái của bàn cờ
            int hashValue = this.computeHash(map, x, y, xe, ye);
            // check xem trạng thái này đã được lưu trong bảng băm chưa
            if(transpositionTable.lookup(hashValue, true)) {
                int lowerBound = transpositionTable.getLowerBound(hashValue);
                int upperBound = transpositionTable.getUpperBound(hashValue);
                if (lowerBound >= beta)
                    return lowerBound;
                if (upperBound <= alpha)
                    return upperBound;
                alpha = Math.max(alpha, lowerBound);
                beta = Math.min(beta, upperBound);
            }
                // nếu đã tồn tại thì trả về luôn giá trị lượng giá
                // mà không phải duyệt cây tìm kiếm
//                return transpositionTable.getBestValue(hashValue);
            if (this.enemyInside(map, xe, ye, x, y)) {
                int a = alpha;
                // nếu chưa đạt đến độ sâu được đặt ra ban đầu
                // tiếp tục thực hiện gọi minValue
                if (depth > 0) {
                    if (map.isSpace(x + 1, y) && a < beta) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setGreen(x + 1, y);
                        final int temp = this.search(maptemp, x + 1, y, xe, ye, depth - 1, a, beta, false);
                        if (temp > max) {
                            max = temp;
                        }
                        if (this.alphabeta) {
                            a = Math.max(a, max);
                        }
                    }
                    if (map.isSpace(x - 1, y) && a < beta) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setGreen(x - 1, y);
                        final int temp = this.search(maptemp, x - 1, y, xe, ye, depth - 1, a, beta, false);
                        if (temp > max) {
                            max = temp;
                        }
                        if (this.alphabeta) {
                            a = Math.max(a, max);
                        }
                    }
                    if (map.isSpace(x, y + 1) && a < beta) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setGreen(x, y + 1);
                        final int temp = this.search(maptemp, x, y + 1, xe, ye, depth - 1, a, beta, false);
                        if (temp > max) {
                            max = temp;
                        }
                        if (this.alphabeta) {
                            a = Math.max(a, max);
                        }
                    }
                    if (map.isSpace(x, y - 1) && a < beta) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setGreen(x, y - 1);
                        final int temp = this.search(maptemp, x, y - 1, xe, ye, depth - 1, a, beta, false);
                        if (temp > max) {
                            max = temp;
                        }
                        if (this.alphabeta) {
                            a = Math.max(a, max);
                        }
                    }
                    if (max <= alpha) {
                        int upperBound = max;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, max, hashValue, true, alpha, upperBound);
                        transpositionTable.store(entry);
                        return max;
                    }
                    if (max > alpha && max < beta) {
                        int upperBound = max;
                        int lowerBoner = max;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, max, hashValue, true, lowerBoner, upperBound);
                        transpositionTable.store(entry);
                        return max;
                    }
                    if (max >= beta) {
                        int lowerBound = max;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, max, hashValue, true, lowerBound, beta);
                        transpositionTable.store(entry);
                        return max;
                    }
                }
                // ngược lại gọi hàm lượng giá cho node
                // (tạm gọi là node lá nhưng khác node lá phía dưới
                else {
                    max = parameter[0] * this.luonggia(new MapS(map), x, y, xe, ye, true) + parameter[1] * this.luonggiacanh(new MapS(map), x, y, xe, ye, true);
                }
                if (max == -80000) {
                    max = -40000 - depth;
                }
                return max;
            }
            // nếu map giả định đã phân vùng
            // trả về kết quả lượng giá cho node lá
            final int t = 31 * this.leaf_utility(new MapS(map), x, y, xe, ye) + 11 * this.luonggiacanh(new MapS(map), x, y, xe, ye, true);
            if (t > 1 || t < -1) {
                return t * 5;
            }
            return t;
        }
        else {
            int hashValue = this.computeHash(map, x, y, xe, ye);
            if(transpositionTable.lookup(hashValue, false)) {
                int lowerBound = transpositionTable.getLowerBound(hashValue);
                int upperBound = transpositionTable.getUpperBound(hashValue);
                if (lowerBound >= beta)
                    return lowerBound;
                if (upperBound <= alpha)
                    return upperBound;
                alpha = Math.max(alpha, lowerBound);
                beta = Math.min(beta, upperBound);
            }
//                return transpositionTable.getBestValue(hashValue);
            if (this.enemyInside(map, xe, ye, x, y)) {
                int b = beta;
                // nếu chưa đạt đến độ sâu được đặt ra ban đầu
                // tiếp tục thực hiện gọi maxValue
                if (depth > 0) {
                    if (map.isSpace(xe + 1, ye) && b > alpha) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setRed(xe + 1, ye);
                        final int temp = this.search(maptemp, x, y, xe + 1, ye, depth - 1, alpha, b, true);
                        if (temp < min) {
                            min = temp;
                        }
                        if (this.alphabeta) {
                            b = Math.min(b, min);
                        }
                    }
                    if (map.isSpace(xe - 1, ye) && b > alpha) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setRed(xe - 1, ye);
                        final int temp = this.search(maptemp, x, y, xe - 1, ye, depth - 1, alpha, b, true);
                        if (temp < min) {
                            min = temp;
                        }
                        if (this.alphabeta) {
                            b = Math.min(b, min);
                        }
                    }
                    if (map.isSpace(xe, ye + 1) && b > alpha) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setRed(xe, ye + 1);
                        final int temp = this.search(maptemp, x, y, xe, ye + 1, depth - 1, alpha, b, true);
                        if (temp < min) {
                            min = temp;
                        }
                        if (this.alphabeta) {
                            b = Math.min(b, min);
                        }
                    }
                    if (map.isSpace(xe, ye - 1) && b > alpha) {
                        final MapS maptemp = new MapS(map);
                        maptemp.setRed(xe, ye - 1);
                        final int temp = this.search(maptemp, x, y, xe, ye - 1, depth - 1, alpha, b, true);
                        if (temp < min) {
                            min = temp;
                        }
                        if (this.alphabeta) {
                            b = Math.min(b, min);
                        }
                    }
                    if (min <= alpha) {
                        int upperBound = min;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, min, hashValue, false, alpha, upperBound);
                        transpositionTable.store(entry);
                        return min;
                    }
                    if (min > alpha && min < beta) {
                        int upperBound = min;
                        int lowerBoner = min;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, min, hashValue, false, lowerBoner, upperBound);
                        transpositionTable.store(entry);
                        return min;
                    }
                    if (min >= beta) {
                        int lowerBound = min;
                        Entry2 entry = new Entry2(map, x, y, xe, ye, depth, min, hashValue, false, lowerBound, beta);
                        transpositionTable.store(entry);
                        return min;
                    }
                }
                // ngược lại gọi hàm lượng giá cho node
                // (tạm gọi là node lá nhưng khác node lá phía dưới)
                else {
                    min =  parameter[0] * this.luonggia(new MapS(map), x, y, xe, ye, false) + parameter[1] * this.luonggiacanh(new MapS(map), x, y, xe, ye, false);
                }
                if (min == 80000) {
                    min = 40000 + depth;
                }
                return min;
            }
            // nếu map giả định đã phân vùng
            // trả về kết quả lượng giá cho node lá
            final int t =  31 * this.leaf_utility(new MapS(map), x, y, xe, ye) + 11 * this.luonggiacanh(new MapS(map), x, y, xe, ye, false);
            if (t > 1 || t < -1) {
                return t * 5;
            }
            return t;
        }
    }

    private int indexing(final int piece) {
        /**
         * trả về giá trị index của từng ô tương ứng với
         * giá trị mã hóa trong bảng băm zobrist*/
        if (piece == 1 || piece == 2 || piece == 3) // green or red or wall
            return 1;
        if (piece == 5)
            return 2;
        return 0;
    }

    private int computeHash(final MapS map, final int x, final int y, final int xe, final int ye) {
        /**
         * hàm băm zobrist. Giả sử map của game có kích
         * thước i*j, mỗi ô trong map có thể có n trạng
         * thái. Kĩ thuật băm zobrist tạo ra một ma trận
         * 3 chiều, kích thước i*j*n, với mỗi phần tử là
         * một số nguyên 32 bit nằm trong khoảng (1, 2^31 - 1)
         * Với mỗi phần tử của map, dùng hàm indexing trả
         * về vị trí của phần tử tương ứng trong mảng zobrist,
         * sau đó tiến hành XOR giá trị băm của ô này (ban đầu
         * được gán h = 0) với giá trị trong mảng zobrist
         * lặp các bước nêu trên với toàn bộ map sẽ ra được
         * giá trị băm của map hiện tại*/
        int h = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (!map.isSpace(i, j)) {
                    // dùng hàm indexing trả về vị trí của phần tử
                    // tương ứng trong mảng zobristTable
                    int piece = indexing(map.map[i * 15 + j]);
                    // vị trí của 2 xe hiện tại
                    if (i == x && j == y)
                        piece = 3;
                    if (i == xe && j == ye)
                        piece = 4;
                    // XOR bit
                    h ^= zobristTable[i][j][piece];
                }
            }
        }
        return h;
    }

    private int leaf_utility(final MapS m, final int x, final int y, final int xe, final int ye) {
        /**
         * Lượng giá cho node lá bằng cách đếm
         * số ô còn lại trong phân vùng của 2 đối thủ,
         * return số ô của red - số ô của green*/
        int redScore = 0;
        int greenScore = 0;
        final MapS redMap = new MapS(m);
        final MapS greenMap = new MapS(m);
        final Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(x, y));
        while (!queue.isEmpty()) {
            final Point temp = queue.remove();
            int xtemp = temp.x;
            int ytemp = temp.y;
            if(redMap.isSpace(xtemp + 1, ytemp)) {
                queue.add(new Point(xtemp + 1, ytemp));
                redMap.setMap(xtemp + 1, ytemp);
                redScore++;
            }
            if(redMap.isSpace(xtemp - 1, ytemp)) {
                queue.add(new Point(xtemp - 1, ytemp));
                redMap.setMap(xtemp - 1, ytemp);
                redScore++;
            }
            if(redMap.isSpace(xtemp, ytemp + 1)) {
                queue.add(new Point(xtemp, ytemp + 1));
                redMap.setMap(xtemp, ytemp + 1);
                redScore++;
            }
            if(redMap.isSpace(xtemp, ytemp - 1)) {
                queue.add(new Point(xtemp, ytemp - 1));
                redMap.setMap(xtemp, ytemp - 1);
                redScore++;
            }
        }
        queue.add(new Point(xe, ye));
        while (!queue.isEmpty()) {
            final Point temp = queue.remove();
            int xetemp = temp.x;
            int yetemp = temp.y;
            if(greenMap.isSpace(xetemp + 1, yetemp)) {
                queue.add(new Point(xetemp + 1, yetemp));
                greenMap.setMap(xetemp + 1, yetemp);
                greenScore++;
            }
            if(greenMap.isSpace(xetemp - 1, yetemp)) {
                queue.add(new Point(xetemp - 1, yetemp));
                greenMap.setMap(xetemp - 1, yetemp);
                greenScore++;
            }
            if(greenMap.isSpace(xetemp, yetemp + 1)) {
                queue.add(new Point(xetemp, yetemp + 1));
                greenMap.setMap(xetemp, yetemp + 1);
                greenScore++;
            }
            if(greenMap.isSpace(xetemp, yetemp - 1)) {
                queue.add(new Point(xetemp, yetemp - 1));
                greenMap.setMap(xetemp, yetemp - 1);
                greenScore++;
            }
        }
        return redScore - greenScore;
    }

    private int evaluate(final MapS map, final int x, final int y, final int xe, final int ye, final boolean myturn) {
        int [][] myEvaluateTable = create_evaluate_table(map, x, y);
        int [][] enemyEvaluateTable = create_evaluate_table(map, xe, ye);
        int sum = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                sum += myEvaluateTable[i][j] - enemyEvaluateTable[i][j];
            }
        }
        return sum;
    }

    private int [][] create_evaluate_table(final MapS map, final int x, final int y) {
        int [][] table = new int [29][29];
        for(int i = 0; i < 29; i++) {
            for(int j = 0; j < 29; j++) {
                table[i][j] = 0;
            }
        }
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                if(map.isSpace(i, j)) {
                    int m = 14 - (x - i);
                    int n = 14 - (y - j);
                    table[m][n] = evaluateTable[m][n];
                }
            }
        }
        return table;
    }

    private int luonggia(final MapS map, final int x, final int y, final int xe, final int ye, final boolean myturn) {
        /**
         * input: trạng thái bàn cờ, thứ tự lượt đi
         * output: tính số ô mà xe đỏ đi được - số ô mà xe xanh đi được
         * Hàm lượng giá trong trường hợp thuật toán chạy đến độ sâu
         * xác định nhưng map chưa phân vùng*/
        final ArrayList<Point> red = new ArrayList<Point>();
        final ArrayList<Point> green = new ArrayList<Point>();
        red.add(new Point(x, y));
        green.add(new Point(xe, ye));
        int sumred = 0;
        int sumgreen = 0;
        if (!this.crack) {
            if (myturn) {
                while (!red.isEmpty() || !green.isEmpty()) {
                    sumred += this.redGo(red, map);
                    sumgreen += this.greenGo(green, map);
                }
            } else {
                while (!red.isEmpty() || !green.isEmpty()) {
                    sumgreen += this.greenGo(green, map);
                    sumred += this.redGo(red, map);
                }
            }
        }
        return sumred - sumgreen;
    }

    private int redGo(final ArrayList<Point> red, final MapS map) {
        /**
         * Đếm và đánh dấu những ô xe xanh có thể đi từ vị trí hiện tại*/
        final ArrayList<Point> redtemp = new ArrayList<Point>();
        int sumred = 0;
        while (!red.isEmpty()) {
            final Point point = red.get(0);
            final int x = point.x;
            final int y = point.y;
            if (map.isSpace(x + 1, y)) {
                ++sumred;
                map.setRed(x + 1, y);
                redtemp.add(new Point(x + 1, y));
            }
            if (map.isSpace(x - 1, y)) {
                ++sumred;
                map.setRed(x - 1, y);
                redtemp.add(new Point(x - 1, y));
            }
            if (map.isSpace(x, y + 1)) {
                ++sumred;
                map.setRed(x, y + 1);
                redtemp.add(new Point(x, y + 1));
            }
            if (map.isSpace(x, y - 1)) {
                ++sumred;
                map.setRed(x, y - 1);
                redtemp.add(new Point(x, y - 1));
            }
            red.remove(0);
        }
        red.addAll(redtemp);
        return sumred;
    }

    private int greenGo(final ArrayList<Point> green, final MapS map) {
        /**
         * Đếm và đánh dấu những ô xe đỏ có thể đi từ vị trí hiện tại*/
        final ArrayList<Point> greentemp = new ArrayList<Point>();
        int sumgreen = 0;
        while (!green.isEmpty()) {
            final Point point = green.get(0);
            final int x = point.x;
            final int y = point.y;
            if (map.isSpace(x + 1, y)) {
                ++sumgreen;
                map.setGreen(x + 1, y);
                greentemp.add(new Point(x + 1, y));
            }
            if (map.isSpace(x - 1, y)) {
                ++sumgreen;
                map.setGreen(x - 1, y);
                greentemp.add(new Point(x - 1, y));
            }
            if (map.isSpace(x, y + 1)) {
                ++sumgreen;
                map.setGreen(x, y + 1);
                greentemp.add(new Point(x, y + 1));
            }
            if (map.isSpace(x, y - 1)) {
                ++sumgreen;
                map.setGreen(x, y - 1);
                greentemp.add(new Point(x, y - 1));
            }
            green.remove(0);
        }
        green.addAll(greentemp);
        return sumgreen;
    }

    private boolean enemyInside(final MapS m, final int xg, final int yg, final int xr, final int yr) {
        /**
         *Check xem đối thủ có ở vùng tìm kiếm hay không
         * (bản đồ đã phân vùng hay chưa)
         * Sử dụng tìm kiếm theo chiều rộng */
        final MapS map = new MapS(m);
        final Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(xr, yr));
        while (!queue.isEmpty()) {
            final Point element = queue.remove();
            final int x = element.x;
            final int y = element.y;
            if (map.isSpace(x + 1, y)) {
                map.setMap(x + 1, y);
                queue.add(new Point(x + 1, y));
            }
            if (map.isSpace(x - 1, y)) {
                map.setMap(x - 1, y);
                queue.add(new Point(x - 1, y));
            }
            if (map.isSpace(x, y - 1)) {
                map.setMap(x, y - 1);
                queue.add(new Point(x, y - 1));
            }
            if (map.isSpace(x, y + 1)) {
                map.setMap(x, y + 1);
                queue.add(new Point(x, y + 1));
            }
        }
        return map.isReachable(xg + 1, yg) || map.isReachable(xg - 1, yg) || map.isReachable(xg, yg + 1) || map.isReachable(xg, yg - 1);
    }
}

