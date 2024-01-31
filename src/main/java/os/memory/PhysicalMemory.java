package os.memory;

public class PhysicalMemory implements Memory {
    private final Object[] memory;

    public PhysicalMemory(int size) {
        this.memory = new Object[size];
    }

    public Object read(int address) {
        return memory[address];
    }

    public void write(int address, Object data) {
        memory[address] = data;
    }

    @Override
    public int getSize() {
        return memory.length;
    }

    @Override
    public int getPhysicalAddress(int address) {
        return address;
    }


}
