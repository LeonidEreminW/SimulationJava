import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstSearch {
    public static Queue<Coordinates> find(Coordinates start, EntityType type, WorldMap map) {
        int h = map.getWorldHeight();
        int w = map.getWorldWidth();
        int[][] dist = new int[h][w];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) dist[y][x] = -1;
        var from = new HashMap<Coordinates,Coordinates>();

        Queue<Coordinates> q = new LinkedList<>();
        q.add(start);
        dist[start.y][start.x] = 0;
        from.put(start, null);

        Coordinates foundTarget = null;
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};

        while (!q.isEmpty()) {
            Coordinates cur = q.remove();
            var ent = map.getEntity(cur);
            if (ent != null && ent.type == type) {
                foundTarget = cur;
                break;
            }
            for (int k = 0; k < 4; k++) {
                int nx = cur.x + dx[k];
                int ny = cur.y + dy[k];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                if (dist[ny][nx] != -1) continue; // visited
                var cell = map.getEntity(new Coordinates(nx, ny));
                // if cell is occupied by a blocking entity (not the target type), skip
                if (cell != null && cell.type != type) continue;
                dist[ny][nx] = dist[cur.y][cur.x] + 1;
                var next = new Coordinates(nx, ny);
                q.add(next);
                from.put(next, cur);
            }
        }

        if (foundTarget == null) return null;
        return getPath(from, foundTarget);
    }

    public static Deque<Coordinates> getPath(HashMap<Coordinates,Coordinates> from, Coordinates end) {
        var path = new LinkedList<Coordinates>();
        var cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = from.get(cur);
        }
        // first element is start position; remove it so queue contains steps to move
        if (!path.isEmpty()) path.removeFirst();
        return path;
    }
}
