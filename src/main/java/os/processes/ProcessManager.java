package os.processes;

import os.Kernel;
import os.Scheduler;
import os.memory.MemoryAllocator;
import os.memory.MemoryBlock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ProcessManager {
    private final HashMap<Integer, Process> processes = new HashMap<>();
    private final String processStoragePath;
    private final Kernel kernel;
    private final ProcessSerializer processSerializer;

    private int nextProcessId = 0;

    public ProcessManager(
        String processStoragePath,
        Kernel kernel,
        ProcessSerializer processSerializer
    ) {
        this.processStoragePath = processStoragePath;
        this.kernel = kernel;
        this.processSerializer = processSerializer;

        try {
            Files.createDirectories(Paths.get(processStoragePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Process createProcess(String path) {
        try {
            List<String> instructions = Files.readAllLines(Paths.get(path));

            Process process = new Process(
                nextProcessId++,
                instructions,
                allocateMemoryForProcess(Process.calculateProcessSize(instructions)),
                kernel.getMemory()
            );

            processes.put(process.getPcb().getProcessId(), process);

            System.out.println(
                "Process with PID #" + process.getPcb().getProcessId() + " created for " + path);

            return process;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Process getProcess(int processId) {
        if (!processes.containsKey(processId)) {
            return loadProcessFromDisk(processId);
        }

        return processes.get(processId);
    }

    public void removeProcess(int processId) {
        MemoryAllocator allocator = kernel.getMemoryAllocator();
        PCB pcb = processes.get(processId).getPcb();
        allocator.free(pcb.getMemoryStart(), pcb.getProcessSize());
        processes.remove(processId);
    }

    private void saveProcessToDisk(Process process) {
        ProcessData processData = process.toProcessData();
        Path path = getPathForProcess(process.getPcb().getProcessId());
        String serializedProcess = processSerializer.serialize(processData);

        System.out.println("Saving process #" + process.getPcb().getProcessId() + " to disk.");

        try {
            Files.writeString(path, serializedProcess);
            removeProcess(process.getPcb().getProcessId());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getPathForProcess(int processId) {
        return Paths.get(processStoragePath + File.separator + processId + ".txt");
    }

    private Process loadProcessFromDisk(int processId) {
        Path path = getPathForProcess(processId);

        try {
            String serializedProcess = Files.readString(path);
            ProcessData processData = processSerializer.deserialize(serializedProcess);
            Process process =
                new Process(
                    processData,
                    allocateMemoryForProcess(processData.getProcessSize()),
                    kernel.getMemory()
                );
            processes.put(processId, process);

            System.out.println(
                "Process #" + process.getPcb().getProcessId() + " loaded from disk.");
            return process;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int allocateMemoryForProcess(int size) {
        Scheduler scheduler = kernel.getScheduler();
        MemoryAllocator allocator = kernel.getMemoryAllocator();

        int start;
        while ((start = allocator.allocate(size)) == -1) {
            List<Integer> blockedProcesses = scheduler.getBlockedProcesses();
            boolean didFree = false;

            for (Integer processId : blockedProcesses) {
                if (processes.containsKey(processId)) {
                    saveProcessToDisk(processes.get(processId));
                    didFree = true;
                    break;
                }
            }

            if (!didFree) {
                for (Process process : processes.values()) {
                    if (process.getPcb().getProcessState() != ProcessState.RUNNING &&
                        process.getPcb().getProcessSize() >= size) {
                        saveProcessToDisk(process);
                        didFree = true;
                        break;
                    }
                }
            }

            if (!didFree) {
                for (Process process : processes.values()) {
                    if (process.getPcb().getProcessState() != ProcessState.RUNNING) {
                        saveProcessToDisk(process);
                        didFree = true;
                        break;
                    }
                }
            }

            if (!didFree) {
                if (processes.isEmpty()) {
                    throw new RuntimeException(
                        "Out of memory, required size: " + size + ", maximum available block: " +
                        allocator.getMaxAvailableBlockSize() + ".");
                } else {
                    saveProcessToDisk(processes.values().iterator().next());
                }
            }
        }

        return start;
    }

    public HashMap<Integer, Process> getProcessesInMemory() {
        return processes;
    }
}
