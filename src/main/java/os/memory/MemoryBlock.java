package os.memory;

public class MemoryBlock implements Memory {
    private final int start;
    private final int size;
    private final Memory memory;

    public MemoryBlock(int start, int size, Memory memory) {
        this.start = start;
        this.size = size;
        this.memory = memory;
    }

    public Object read(int address) {
        return memory.read(start + address);
    }

    public void write(int address, Object value) {
        memory.write(start + address, value);
    }

    public int getSize() {
        return size;
    }

    @Override
    public int getPhysicalAddress(int address) {
        return memory.getPhysicalAddress(start + address);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return start + size - 1;
    }

    public int getOffset(int address) {
        return address - getStart();
    }

    public boolean inRange(int address) {
        return address >= getStart() && address <= getEnd();
    }
}
