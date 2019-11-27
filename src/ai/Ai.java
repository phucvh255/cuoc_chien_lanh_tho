package ai;

import java.util.*;

import map.MapS;

public class Ai
{
    private static final int DEPTH = 7;
    private static final int DEPTH_PATH = 12;
    private static final int MAXIMUM = 80000;
    private static final int MINIMUM = -80000;
    int[] priceofspace; // lượng giá ô trống
    int amountnode = 0;
    boolean crack;
    private boolean alphabeta;
    private Transposition transpositionTable; // bảng băm
    private int [][][] zobristTable; // mảng băm zobristTable
    private static final int VAT = 5;
    private static final int VS = 1;
    private static final int KN = 31;
    private static final int KE = 11;

    public Ai() {
        this.priceofspace = new int[5];
        this.crack = false;
        this.alphabeta = true;
        transpositionTable = new Transposition(200000);
        zobristTable = new int[15][15][8];
        // tạo một instance của lớp Random
        Random rd = new Random();
        // sinh ngẫu nhiên giá trị của bảng băm zobristTable
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                for(int k = 0; k < 8; k++) {
                    int temp = -1;
                    while (temp < 1)
                        temp = rd.nextInt();
                    zobristTable[i][j][k] = temp;
                }
            }
        }
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
            m.setGreen(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 1;
            }
        }
        if (map.isSpace(x + 1, y)) {
            final MapS m = new MapS(map);
            m.setGreen(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 3;
            }
        }
        if (map.isSpace(x, y - 1)) {
            final MapS m = new MapS(map);
            m.setGreen(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, 12);
            if (temp > max) {
                max = temp;
                dir = 2;
            }
        }
        if (map.isSpace(x, y + 1)) {
            final MapS m = new MapS(map);
            m.setGreen(x, y + 1);
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
            m.setGreen(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x + 1, y)) {
            final MapS m = new MapS(map);
            m.setGreen(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y - 1)) {
            final MapS m = new MapS(map);
            m.setGreen(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y + 1)) {
            final MapS m = new MapS(map);
            m.setGreen(x, y + 1);
            final int temp = this.searchPath(m, x, y + 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (t < -990) {
            return -depth;
        }
        return t;
    }

    private int countEdgesAStep(final MapS map, final ArrayList<Point> arp) {
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
        int mysum = 0;
        int hissum = 0;
        final ArrayList<Point> me = new ArrayList<Point>();
        final ArrayList<Point> him = new ArrayList<Point>();
        me.add(new Point(x, y));
        him.add(new Point(xe, ye));
        if (myturn) {
            mysum += this.countEdgesAStep(map, me);
        }
        while (!me.isEmpty() || !him.isEmpty()) {
            hissum += this.countEdgesAStep(map, him);
            mysum += this.countEdgesAStep(map, me);
        }
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

    public int minimax(final MapS map, final int x, final int y, final int xe, final int ye) {
        final int depth = 14;
        int direction = -1;
        int value = -80000;
        // chú ý cách chọn alpha và beta để thuật toán hiệu quả nhất
        final int alpha = -80000;
        final int beta = 80000;
        this.amountnode = 0;
        if (map.isSpace(x, y + 1)) {
            // giả lập 1 bản đồ mới từ bản đồ hiện tại
            final MapS maptemp = new MapS(map);
            maptemp.setGreen(x, y + 1);
            final int temp = this.minValue(maptemp, x, y + 1, xe, ye, depth - 1, alpha, beta);
            if (temp > value) {
                value = temp;
                direction = 0;
            }
        }
        if (map.isSpace(x, y - 1)) {
            final MapS maptemp = new MapS(map);
            maptemp.setGreen(x, y - 1);
            final int temp = this.minValue(maptemp, x, y - 1, xe, ye, depth - 1, alpha, beta);
            if (temp > value) {
                value = temp;
                direction = 2;
            }
        }
        if (map.isSpace(x + 1, y)) {
            final MapS maptemp = new MapS(map);
            maptemp.setGreen(x + 1, y);
            final int temp = this.minValue(maptemp, x + 1, y, xe, ye, depth - 1, alpha, beta);
            if (temp > value) {
                value = temp;
                direction = 3;
            }
        }
        if (map.isSpace(x - 1, y)) {
            final MapS maptemp = new MapS(map);
            maptemp.setGreen(x - 1, y);
            final int temp = this.minValue(maptemp, x - 1, y, xe, ye, depth - 1, alpha, beta);
            if (temp > value) {
                value = temp;
                direction = 1;
            }
        }
//        System.out.println("Xanh: " + this.amountnode);
        return direction;
    }

    private int maxValue(final MapS map, final int x, final int y, final int xe, final int ye, final int depth, int alpha, final int beta) {
        ++this.amountnode;
        int max = -80000;
        if (this.crack || this.enemyInside(map, xe, ye, x, y)) {
            if (depth > 0) {
                if (map.isSpace(x + 1, y)) {
                    final MapS maptemp = new MapS(map);
                    maptemp.setGreen(x + 1, y);
                    final int temp = this.minValue(maptemp, x + 1, y, xe, ye, depth - 1, alpha, beta);
                    if (temp > max) {
                        max = temp;
                    }
                    if (this.alphabeta) {
                        if (max >= beta) {
                            return max;
                        }
                        if (alpha < max) {
                            alpha = max;
                        }
                    }
                }
                if (map.isSpace(x - 1, y)) {
                    final MapS maptemp = new MapS(map);
                    maptemp.setGreen(x - 1, y);
                    final int temp = this.minValue(maptemp, x - 1, y, xe, ye, depth - 1, alpha, beta);
                    if (temp > max) {
                        max = temp;
                    }
                    if (this.alphabeta) {
                        if (max >= beta) {
                            return max;
                        }
                        if (alpha < max) {
                            alpha = max;
                        }
                    }
                }
                if (map.isSpace(x, y + 1)) {
                    final MapS maptemp = new MapS(map);
                    maptemp.setGreen(x, y + 1);
                    final int temp = this.minValue(maptemp, x, y + 1, xe, ye, depth - 1, alpha, beta);
                    if (temp > max) {
                        max = temp;
                    }
                    if (this.alphabeta) {
                        if (max >= beta) {
                            return max;
                        }
                        if (alpha < max) {
                            alpha = max;
                        }
                    }
                }
                if (map.isSpace(x, y - 1)) {
                    final MapS maptemp = new MapS(map);
                    maptemp.setGreen(x, y - 1);
                    final int temp = this.minValue(maptemp, x, y - 1, xe, ye, depth - 1, alpha, beta);
                    if (temp > max) {
                        max = temp;
                    }
                    if (this.alphabeta) {
                        if (max >= beta) {
                            return max;
                        }
                        if (alpha < max) {
                            alpha = max;
                        }
                    }
                }
            }
            else {
                max = 31 * this.luonggia(new MapS(map), x, y, xe, ye, true) + 11 * this.luonggiacanh(new MapS(map), x, y, xe, ye, true);
            }
            if (max == -80000) {
                max = -40000 - depth;
            }
            return max;
        }
        final int t = 31 * this.luonggiaOutside(new MapS(map), x, y, xe, ye) + 11 * this.luonggiacanh(new MapS(map), x, y, xe, ye, true);
        if (t > 1 || t < -1) {
            return t * 5;
        }
        return t;
    }

    private int minValue(final MapS map, final int x, final int y, final int xe, final int ye, final int depth, final int alpha, int beta) {
        ++this.amountnode;
        int min = 80000;
        if (this.crack || this.enemyInside(map, xe, ye, x, y)) {
            if (map.isSpace(xe + 1, ye)) {
                final MapS maptemp = new MapS(map);
                maptemp.setRed(xe + 1, ye);
                final int temp = this.maxValue(maptemp, x, y, xe + 1, ye, depth - 1, alpha, beta);
                if (temp < min) {
                    min = temp;
                }
                if (this.alphabeta) {
                    if (min <= alpha) {
                        return min;
                    }
                    beta = ((beta < min) ? beta : min);
                }
            }
            if (map.isSpace(xe - 1, ye)) {
                final MapS maptemp = new MapS(map);
                maptemp.setRed(xe - 1, ye);
                final int temp = this.maxValue(maptemp, x, y, xe - 1, ye, depth - 1, alpha, beta);
                if (temp < min) {
                    min = temp;
                }
                if (this.alphabeta) {
                    if (min <= alpha) {
                        return min;
                    }
                    beta = ((beta < min) ? beta : min);
                }
            }
            if (map.isSpace(xe, ye + 1)) {
                final MapS maptemp = new MapS(map);
                maptemp.setRed(xe, ye + 1);
                final int temp = this.maxValue(maptemp, x, y, xe, ye + 1, depth - 1, alpha, beta);
                if (temp < min) {
                    min = temp;
                }
                if (this.alphabeta) {
                    if (min <= alpha) {
                        return min;
                    }
                    beta = ((beta < min) ? beta : min);
                }
            }
            if (map.isSpace(xe, ye - 1)) {
                final MapS maptemp = new MapS(map);
                maptemp.setRed(xe, ye - 1);
                final int temp = this.maxValue(maptemp, x, y, xe, ye - 1, depth - 1, alpha, beta);
                if (temp < min) {
                    min = temp;
                }
                if (this.alphabeta) {
                    if (min <= alpha) {
                        return min;
                    }
                    beta = ((beta < min) ? beta : min);
                }
            }
            if (min == 80000) {
                min = 40000 + depth;
            }
            return min;
        }
        final int t = 31 * this.luonggiaOutside(new MapS(map), x, y, xe, ye) + 11 * this.luonggiacanh(new MapS(map), x, y, xe, ye, false);
        if (t > 1 || t < -1) {
            return t * 5;
        }
        return t;
    }

    private int indexing(final int piece) {
        /**
         * trả về giá trị index của từng ô tương ứng với
         * giá trị mã hóa trong bảng băm zobrist*/
        if (piece == 1) // green
            return 1;
        if (piece == 2) // red
            return 2;
        if (piece == 3) // wall
            return 3;
        if (piece == 4) // temp
            return 4;
        if (piece == 5)
            return 5;
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
                        piece = 6;
                    if (i == xe && j == ye)
                        piece = 7;
                    // XOR bit
                    h ^= zobristTable[i][j][piece];
                }
            }
        }
        return h;
    }

    private int luonggiaOutside(final MapS m, final int x, final int y, final int xe, final int ye) {
        int mysum = 0;
        int hissum = 0;
        int black = 0;
        int white = 0;
        final ArrayList<Point> arp = new ArrayList<Point>();
        arp.add(new Point(x, y));
        boolean blackturn = true;
        while (!arp.isEmpty()) {
            if (blackturn) {
                black += this.greenGo(arp, m);
                blackturn = false;
            }
            else {
                white += this.greenGo(arp, m);
                blackturn = true;
            }
        }
        mysum = white + black;
        if (white > black) {
            mysum -= white - black;
        }
        else if (white < black - 1) {
            mysum -= black - 1 - white;
        }
        white = (black = 0);
        arp.add(new Point(xe, ye));
        blackturn = true;
        while (!arp.isEmpty()) {
            if (blackturn) {
                black += this.greenGo(arp, m);
                blackturn = false;
            }
            else {
                white += this.greenGo(arp, m);
                blackturn = true;
            }
        }
        hissum = white + black;
        if (hissum <= 0) {
            if (white > black) {
                hissum -= white - black;
            }
            else if (white < black - 1) {
                hissum -= black - 1 - white;
            }
        }
        return mysum - hissum;
    }

    public int luonggia(final MapS map, final int x, final int y, final int xe, final int ye, final boolean ismyturn) {
        final ArrayList<Point> green = new ArrayList<Point>();
        final ArrayList<Point> red = new ArrayList<Point>();
        green.add(new Point(x, y));
        red.add(new Point(xe, ye));
        int sumgreen = 0;
        int sumred = 0;
        if (!this.crack) {
            if (ismyturn) {
                while (!green.isEmpty() || !red.isEmpty()) {
                    sumgreen += this.greenGo(green, map);
                    sumred += this.redGo(red, map);
                }
            }
            else {
                while (!green.isEmpty() || !red.isEmpty()) {
                    sumred += this.redGo(red, map);
                    sumgreen += this.greenGo(green, map);
                }
            }
        }
        else {
            while (!green.isEmpty()) {
                sumgreen += this.greenGo(green, map);
            }
        }
        return sumgreen - sumred;
    }

    private int greenGo(final ArrayList<Point> green, final MapS map) {
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
        for (int i = 0; i < greentemp.size(); ++i) {
            green.add(greentemp.get(i));
        }
        return sumgreen;
    }

    private int redGo(final ArrayList<Point> red, final MapS map) {
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
        for (int i = 0; i < redtemp.size(); ++i) {
            red.add(redtemp.get(i));
        }
        return sumred;
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
