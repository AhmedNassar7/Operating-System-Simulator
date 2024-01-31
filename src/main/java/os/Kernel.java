package os;

import os.memory.Memory;
import os.memory.MemoryAllocator;
import os.memory.PhysicalMemory;
import os.processes.DefaultProcessSerializer;
import os.processes.ProcessManager;

import java.util.HashMap;

public class Kernel {
    private final Memory memory;
    private final Scheduler scheduler;
    private final ProcessManager processManager;
    private final InstructionExecutor instructionExecutor;
    private final MemoryAllocator memoryAllocator;
    private final HashMap<String, Mutex> mutexes = new HashMap<>();

    public Kernel(int memorySize, int quantumSize, String processStoragePath) {
        this.memory = new PhysicalMemory(memorySize);
        this.scheduler = new Scheduler(quantumSize, this);
        this.processManager =
            new ProcessManager(processStoragePath, this, new DefaultProcessSerializer());
        this.instructionExecutor = new InstructionExecutor(this);
        this.memoryAllocator = new MemoryAllocator(memory);

        mutexes.put("file", new Mutex("file", this));
        mutexes.put("userInput", new Mutex("userInput", this));
        mutexes.put("userOutput", new Mutex("userOutput", this));
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public InstructionExecutor getInstructionExecutor() {
        return instructionExecutor;
    }

    public Mutex getMutex(String name) {
        return mutexes.get(name);
    }

    public Memory getMemory() {
        return memory;
    }

    public MemoryAllocator getMemoryAllocator() {
        return memoryAllocator;
    }
}
