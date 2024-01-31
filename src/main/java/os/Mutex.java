package os;

import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
    private final String name;
    private final Kernel kernel;
    private int owner = -1;
    private final Queue<Integer> blockedQueue = new LinkedList<>();

    public Mutex(String name, Kernel kernel) {
        this.name = name;
        this.kernel = kernel;
    }

    public void semWait(int processId) {
        if (owner == -1) {
            owner = processId;
            System.out.println("Process " + processId + " acquired mutex " + name);
        } else {
            System.out.println("Process " + processId + " blocked on mutex " + name);
            blockedQueue.add(processId);
            kernel.getScheduler().blockProcess(processId);
        }
    }

    public void semSignal(int processId) {
        if (owner != processId) {
            return;
        }

        System.out.println("Process " + processId + " released mutex " + name);
        if (blockedQueue.isEmpty()) {
            owner = -1;
        } else {
            int nextProcessId = blockedQueue.remove();
            System.out.println("Process " + nextProcessId + " acquired mutex " + name);
            kernel.getScheduler().unblockProcess(nextProcessId);
            owner = nextProcessId;
        }
    }
}
