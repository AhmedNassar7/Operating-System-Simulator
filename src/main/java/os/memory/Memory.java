package os.memory;

public interface Memory {
    Object read(int address);
    void write(int address, Object value);
    int getSize();
    int getPhysicalAddress(int address);
}
