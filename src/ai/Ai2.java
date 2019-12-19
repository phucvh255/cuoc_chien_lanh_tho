package ai;

import java.io.File;
import java.io.IOException;
import java.util.*;
import map.Map;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;


public class Ai2 {
    private static final int DEPTH = 7;
    private static final int DEPTH_PATH = 12;
    private static final int MAXIMUM = 80000;
    private static final int MINIMUM = -80000;
    private int[] priceofspace; // lượng giá ô trống
    private int amountnode = 0; // đếm số node được duyệt qua
    private boolean crack; // biến kiểm tra xem map đã phân vùng chưa
    private boolean alphabeta;
    private int[] parameter;
    private float learningRate;
    private float gamma;
    private int bufferSize;
    private Buffer replayBuffer;
    private Experience lastExp;
    private int lastActionIndex;
    private boolean useTargetNet;
    public Model2 model;

    public Ai2() throws IOException {
        this.priceofspace = new int[5];
        this.crack = false;
        this.alphabeta = true;
        parameter = new int[2];
        parameter[0] = 31;
        parameter[1] = 11;
        this.gamma = (float) 0.9;
        this.learningRate = (float) 0.001;
        String filePath = "G:/Project/java/CCLT/src/weight/MyComputationGraph.zip";
        File f = new File(filePath);
        if(f.exists()) {
            this.model = new Model2(1, 4, learningRate, filePath);
        }
        else this.model = new Model2(1, 4, learningRate);
        bufferSize = 50000;
        this.replayBuffer = new Buffer(bufferSize);
        this.useTargetNet = false;
    }

    public int findDirection(final Map map, final int x, final int y, final int xe, final int ye) {
        /**
         *Input:
         * map: bản đồ hiện tại
         * x: tung độ xe đỏ hiện tại
         * y: hoành độ xe đỏ hiện tại
         * xe: tung độ xe xanh hiện tại
         * ye: hoành độ xe xanh hiện tại
         *Output: direction tương ứng với hướng cần di chuyển
         */
        long startTime = System.currentTimeMillis();
        if (!map.isSpace(x - 1, y) && !map.isSpace(x + 1, y) && !map.isSpace(x, y + 1) && !map.isSpace(x, y - 1)) {
            return -1;
        }
        if(useTargetNet) {
            learn(map, x, y, xe, ye);
        }
        if (this.crack) {
            useTargetNet = false;
            return this.findPathV1(map, x, y);
        }
        // check xem bản đồ đã phân vùng chưa?
        if (!this.enemyInside(new Map(map), xe, ye, x, y)) {
            this.crack = true;
            useTargetNet = false;
            // nếu đã phân vùng thì gọi hàm findPathV1, dùng BFS để tìm đường
            return this.findPathV1(map, x, y);
        }
        // nếu chưa thì sử dụng alpha-beta để tìm đường
        useTargetNet = true;
        final int t = this.train(map, x, y, xe, ye);
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
//        long endTime = System.currentTimeMillis();
//        long time = endTime - startTime;
//        System.out.println(time);
        return t;
    }

    private int findPathV1(final Map map, final int x, final int y) {
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
            final Map m = new Map(map);
            m.setRed(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 1;
            }
        }
        if (map.isSpace(x + 1, y)) {
            final Map m = new Map(map);
            m.setRed(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, 12);
            if (temp > max) {
                max = temp;
                dir = 3;
            }
        }
        if (map.isSpace(x, y - 1)) {
            final Map m = new Map(map);
            m.setRed(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, 12);
            if (temp > max) {
                max = temp;
                dir = 2;
            }
        }
        if (map.isSpace(x, y + 1)) {
            final Map m = new Map(map);
            m.setRed(x, y + 1);
            final int temp = this.searchPath(m, x, y + 1, 12);
            if (temp > max) {
                max = temp;
                dir = 0;
            }
        }
        return dir;
    }

    private int searchPath(final Map map, final int x, final int y, final int depth) {
        /**
         * Input:
         * depth: độ sâu của cây tìm kiếm, ban đầu bằng 12
         * Output: điểm của node cần đáng giá
         * Đánh giá điểm cho từng node trong cây tìm kiếm
         * Bằng giải thuật tìm kiếm theo chiều sâu*/
        int t = -999;
        if (depth <= 0) {
            return this.numberofegdes(map, x, y);
        }
        if (map.isSpace(x - 1, y)) {
            final Map m = new Map(map);
            m.setRed(x - 1, y);
            final int temp = this.searchPath(m, x - 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x + 1, y)) {
            final Map m = new Map(map);
            m.setRed(x + 1, y);
            final int temp = this.searchPath(m, x + 1, y, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y - 1)) {
            final Map m = new Map(map);
            m.setRed(x, y - 1);
            final int temp = this.searchPath(m, x, y - 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (map.isSpace(x, y + 1)) {
            final Map m = new Map(map);
            m.setRed(x, y + 1);
            final int temp = this.searchPath(m, x, y + 1, depth - 1);
            t = (Math.max(t, temp));
        }
        if (t < -990) {
            return -depth;
        }
        return t;
    }

    private int numberofegdes(final Map map, final int x, final int y) {
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

    private int enemy_outside_evaluate(final Map m, final int x, final int y, final int xe, final int ye) {
        /**
         * Lượng giá cho node lá bằng cách đếm
         * số ô còn lại trong phân vùng của 2 đối thủ,
         * return số ô của red - số ô của green*/
        int redScore = 0;
        int greenScore = 0;
        final Map redMap = new Map(m);
        final Map greenMap = new Map(m);
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

    private int[][] get_state(final Map map, final int x, final int y, final int xe, final int ye) {
        int[][] m = map.getMap();
        int[][] state = new int[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                int piece = m[i][j];
                if (piece == 1 || piece == 2 || piece == 3) // green or red or wall
                    state[i][j] = 1;
                if (i == x && j == y)
                    state[i][j] = 2;
                if (i == xe && j == ye)
                    state[i][j] = 3;
                else state[i][j] = 0;
            }
        }
        return state;
    }

    private boolean enemyInside(final Map m, final int xr, final int yr, final int xg, final int yg) {
        /**
         *Check xem đối thủ có ở vùng tìm kiếm hay không
         * (bản đồ đã phân vùng hay chưa)
         * Sử dụng tìm kiếm theo chiều rộng */
        final Map map = new Map(m);
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

    private Experience get_experience(final Map map, final int x, final int y, final int xe, final int ye) {
        int[][] state = get_state(map, x, y, xe, ye);
        int[] actions = new int[]{-1, -1, -1, -1};
        float[] rewards = new float[4];
        int[][][] nextStates = new int[4][15][15];
        boolean[] done = new boolean[]{false, false, false, false};
        if(map.isSpace(x + 1, y)) {
            Map m = new Map(map);
            int action = 3;
            actions[action] = action;
            m.setRed(x + 1, y);
            int[][] nextState = get_state(m, x + 1, y, xe, ye);
            nextStates[action] = nextState;
            float reward;
            if (enemyInside(m, x + 1, y, xe, ye)) {
//                reward = enemy_inside_evaluate(m, x + 1, y, xe, ye, false);
//                reward = m.amountSpacesAround(x + 1, y);
                reward = 0;
            }
            else {
//                System.out.println(1);
                int evaluate = enemy_outside_evaluate(m, x + 1, y, xe, ye);
                if(evaluate > 0)
                    reward = 1;
                else if(evaluate == 0)
                    reward = (float) 0.5;
                 else reward = -1;
//                reward =  20 * evaluate;
                done[action] = true;
            }
            rewards[action] = reward;
        }

        if(map.isSpace(x - 1, y)) {
            Map m = new Map(map);
            int action = 1;
            actions[action] = action;
            m.setRed(x - 1, y);
            int[][] nextState = get_state(m, x - 1, y, xe, ye);
            nextStates[action] = nextState;
            float reward;
            if (enemyInside(m, x - 1, y, xe, ye)) {
//                reward = enemy_inside_evaluate(m, x - 1, y, xe, ye, false);
//                reward = m.amountSpacesAround(x - 1, y);
                reward = 0;
            }
            else {
//                System.out.println(1);
                int evaluate = enemy_outside_evaluate(m, x - 1, y, xe, ye);
                if(evaluate > 0)
                    reward = 1;
                else if(evaluate == 0)
                    reward = (float) 0.5;
                else reward = -1;
//                reward = 20 * evaluate;
                done[action] = true;
            }
            rewards[action] = reward;
        }

        if(map.isSpace(x, y + 1)) {
            Map m = new Map(map);
            int action = 0;
            actions[action] = action;
            m.setRed(x, y + 1);
            int[][] nextState = get_state(m, x, y + 1, xe, ye);
            nextStates[action] = nextState;
            float reward;
            if (enemyInside(m, x, y + 1, xe, ye)) {
//                reward = enemy_inside_evaluate(m, x, y + 1, xe, ye, false);
//                reward = m.amountSpacesAround(x, y + 1);
                reward = 0;
            }
            else {
//                System.out.println(1);
                int evaluate = enemy_outside_evaluate(m, x, y + 1, xe, ye);
                if(evaluate > 0)
                    reward = 1;
                else if(evaluate == 0)
                    reward = (float) 0.5;
                else reward = -1;
//                reward = 20 * evaluate;
                done[action] = true;
            }
            rewards[action] = reward;
        }

        if(map.isSpace(x, y - 1)) {
            Map m = new Map(map);
            int action = 2;
            actions[action] = action;
            m.setRed(x, y - 1);
            int[][] nextState = get_state(m, x, y - 1, xe, ye);
            nextStates[action] = nextState;
            float reward;
            if (enemyInside(m, x, y - 1, xe, ye)) {
//                reward = enemy_inside_evaluate(m, x, y - 1, xe, ye, false);
//                reward = m.amountSpacesAround(x, y - 1);
                reward = 0;
            }
            else {
//                System.out.println(1);
                int evaluate = enemy_outside_evaluate(m, x, y - 1, xe, ye);
                if(evaluate > 0)
                    reward = 1;
                else if(evaluate == 0)
                    reward = (float) 0.5;
                else reward = -1;
//                reward = 20 * evaluate;
                done[action] = true;
            }
            rewards[action] = reward;
        }
        return new Experience(state, actions, rewards, nextStates, done);
    }

    public boolean check_action(final Map map, final int x, final int y, final int action) {
        if(action == 0)
            return map.isSpace(x, y + 1);
        else if(action == 1)
            return map.isSpace(x - 1, y);
        else if(action == 2)
            return map.isSpace(x, y - 1);
        else
            return map.isSpace(x + 1, y);
    }

    public int make_move(final Map map, final int x, final int y, final int xe, final int ye) {
        Experience exp = get_experience(map, x, y, xe, ye);
        float eps = (float) 0.5;
        Random rd = new Random();
//        int numOfNextState = exp.numOfNextState;
        float[] predict = model.forward(convert_matrix_to_ndarr(exp.state));
        int action = -1;
        System.out.println("predict" + Arrays.toString(predict));
        if (rd.nextFloat() < eps) {
            do
                action = rd.nextInt(4);
            while (!check_action(map, x, y, action));
//            System.out.println(Integer.toString(amountnode) + ": random");
        }
        else {
            float max = -100000;
            for(int i = 0; i < 4; i++) {
                int action_temp = exp.actions[i];
                if(check_action(map, x, y, action_temp) && action_temp != -1) {
                    max = Math.max(max, predict[action_temp]);
                    action = action_temp;
                }
            }
//            System.out.println(Integer.toString(amountnode) + ": " + Float.toString(predict[0]) + ", " + Float.toString(predict[1]) + ", " + Float.toString(predict[2]));
        }
        lastExp = new Experience(exp);
        lastActionIndex = action;
        replayBuffer.push(exp);
        return exp.actions[action];
    }

    private int train(final Map map, final int x, final int y, final int xe, final int ye) {
        return make_move(map, x, y, xe, ye);
    }

    public void learn(final Map map, final int x, final int y, final int xe, final int ye) {
        Experience exp = new Experience(get_experience(map, x, y, xe, ye));
        int numOfBuffer = Buffer.getNumOfBuffer();
        int batchSize = Math.min(numOfBuffer, 10);
//        System.out.println(Arrays.toString(exp.rewards));
        Experience[] batch = replayBuffer.sample(batchSize);
        batch[batchSize - 1] = new Experience(lastExp);
        for(int i = 0; i < batchSize; i++) {
            lastExp = batch[i];
            float[] lastPredict = model.forward(convert_matrix_to_ndarr(lastExp.state));
            float reward = (float) lastExp.rewards[lastActionIndex];
            float target;
            float[] predict = model.forward(convert_matrix_to_ndarr(exp.state));
            float max = -80000;
            for(int j = 0; j < exp.numOfNextState; j++) {
                int act = exp.actions[j];
                if(check_action(map, x, y, act)) {
                    max = Math.max(max, predict[j]);
                }
            }
            target = reward + gamma * max;
            lastPredict[lastActionIndex] = target;
            System.out.println("target" + Arrays.toString(lastPredict));
            model.fit(convert_matrix_to_ndarr(lastExp.state), convert_vector_to_ndarr(lastPredict));
        }
    }

    private INDArray convert_matrix_to_ndarr(int[][] state) {
        double[][][] arr = new double[1][15][15];
        //INDArray[] stateNDarr = new INDArray[1];
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                arr[0][i][j] = state[i][j];
            }
        }
        double[] flat = ArrayUtil.flattenDoubleArray(arr);
        int[] shape = new int[]{1, 1, 15, 15};
        return Nd4j.create(flat, shape, 'c');
    }

    private INDArray[] convert_vector_to_ndarr(float[] vector) {
        INDArray[] vectorNDarr = new INDArray[1];
        vectorNDarr[0] = Nd4j.create(vector, new int[]{1, 4});
        return vectorNDarr;
    }
}
