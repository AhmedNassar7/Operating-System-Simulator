package os.processes;

import os.memory.Memory;
import os.memory.MemoryBlock;

import java.util.Arrays;
import java.util.List;

public class PCB extends MemoryBlock {
    public static final int PCB_SIZE = 5;

    private static final String KEY_MEMORY_START = "Memory Start";
    private static final String KEY_PROCESS_SIZE = "Process Size";
    private static final String KEY_PROCESS_ID = "Process ID";
    private static final String KEY_PROCESS_STATE = "Process State";
    private static final String KEY_PC = "Program Counter";

    private static final List<String> KEYS = Arrays.stream(new String[]{
        KEY_MEMORY_START,
        KEY_PROCESS_SIZE,
        KEY_PROCESS_ID,
        KEY_PROCESS_STATE,
        KEY_PC
    }).toList();

    public PCB(int pid, int memoryStart, int processSize, Memory memory) {
        super(memoryStart, PCB_SIZE, memory);

        setMemoryStart(memoryStart);
        setProcessSize(processSize);
        setProcessId(pid);
        setProcessState(ProcessState.READY);
        setProgramCounter(0);
    }

    public int getMemoryStart() {
        return (int) read(KEYS.indexOf(KEY_MEMORY_START));
    }

    private void setMemoryStart(int memoryStart) {
        write(KEYS.indexOf(KEY_MEMORY_START), memoryStart);
    }

    public int getProcessId() {
        return (int) read(KEYS.indexOf(KEY_PROCESS_ID));
    }

    private void setProcessId(int id) {
        write(KEYS.indexOf(KEY_PROCESS_ID), id);
    }

    public ProcessState getProcessState() {
        return (ProcessState) read(KEYS.indexOf(KEY_PROCESS_STATE));
    }

    public void setProcessState(ProcessState processState) {
        write(KEYS.indexOf(KEY_PROCESS_STATE), processState);
    }

    public int getProcessSize() {
        return (int) read(KEYS.indexOf(KEY_PROCESS_SIZE));
    }

    private void setProcessSize(int size) {
        write(KEYS.indexOf(KEY_PROCESS_SIZE), size);
    }

    public int getProgramCounter() {
        return (int) read(KEYS.indexOf(KEY_PC));
    }

    public void setProgramCounter(int programCounter) {
        write(KEYS.indexOf(KEY_PC), programCounter);
    }

    public void incrementProgramCounter() {
        setProgramCounter(getProgramCounter() + 1);
    }

    public String getKey(int i) {
        return KEYS.get(i);
    }
}
