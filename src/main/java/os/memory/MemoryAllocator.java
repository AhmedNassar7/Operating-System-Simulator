package os.memory;

public class MemoryAllocator {
    private final Memory memory;
    private final boolean[] allocated;

    public MemoryAllocator(Memory memory) {
        this.memory = memory;
        this.allocated = new boolean[memory.getSize()];
    }

    public int allocate(int size) {
        int start = -1;
        int count = 0;
        for (int i = 0; i < allocated.length; i++) {
            if (allocated[i]) {
                start = -1;
                count = 0;
            } else {
                if (start == -1) {
                    start = i;
                }
                count++;
                if (count == size) {
                    for (int j = start; j < start + size; j++) {
                        allocated[j] = true;
                    }
                    return start;
                }
            }
        }
        return -1;
    }

    public void free(int start, int size) {
        for (int i = start; i < start + size; i++) {
            allocated[i] = false;
            memory.write(i, null);
        }
    }

    public int getMaxAvailableBlockSize() {
        int max = 0;
        int count = 0;
        for (boolean b : allocated) {
            if (b) {
                count = 0;
            } else {
                count++;
                if (count > max) {
                    max = count;
                }
            }
        }
        return max;
    }
}
