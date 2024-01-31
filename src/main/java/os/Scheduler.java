package os;

import os.memory.Memory;
import os.processes.Process;
import os.processes.ProcessManager;
import os.processes.ProcessState;

import java.util.*;

public class Scheduler {
    private final Queue<Integer> readyQueue = new LinkedList<>();
    private final Queue<Integer> blockedProcesses = new LinkedList<>();

    private final int quantum;
    private final Kernel kernel;
    private int clock;
    private final HashMap<Integer, List<String>> scheduledPrograms = new HashMap<>();

    public Scheduler(int quantum, Kernel kernel) {
        this.quantum = quantum;
        this.kernel = kernel;
    }


    public void scheduleProgramAt(String path, int clockCycle) {
        if (scheduledPrograms.containsKey(clockCycle)) {
            scheduledPrograms.get(clockCycle).add(path);
        } else {
            List<String> programs = new ArrayList<>();
            programs.add(path);
            scheduledPrograms.put(clockCycle, programs);
        }
    }

    public void addScheduledPrograms() {
        ProcessManager processManager = kernel.getProcessManager();

        if (scheduledPrograms.containsKey(clock)) {
            for (String path : scheduledPrograms.get(clock)) {
                System.out.println("Program arrived at clock " + clock + ": " + path);
                Process process = processManager.createProcess(path);
                addProcessToReadyQueue(process.getPcb().getProcessId());
            }
            scheduledPrograms.remove(clock);
            printMemory("After adding scheduled programs at clock cycle " + clock);
        }
    }

    public void run() {
        ProcessManager processManager = kernel.getProcessManager();
        InstructionExecutor instructionExecutor = kernel.getInstructionExecutor();

        printClock();
        addScheduledPrograms();

        while (!readyQueue.isEmpty() || !scheduledPrograms.isEmpty()) {
            if (!readyQueue.isEmpty()) {
                Process process = chooseProcessToRun();
                int processId = process.getPcb().getProcessId();

                for (int i = 0; i < quantum && process.hasInstructions() &&
                                processManager.getProcess(processId).getPcb().getProcessState() !=
                                ProcessState.BLOCKED; i++) {
                    System.out.println("Running process #" + processId);

                    String instruction = processManager.getProcess(processId).getNextInstruction();
                    try {
                        System.out.println("Executing instruction: " + instruction);
                        instructionExecutor.execute(
                            processManager.getProcess(processId),
                            instruction
                        );
                    } catch (OSException e) {
                        System.out.println(e.getMessage());
                        processManager.getProcess(processId)
                                      .getPcb()
                                      .setProcessState(ProcessState.TERMINATED);
                        break;
                    }
                    printMemory(
                        "Memory after instruction execution at end of clock cycle " + clock);
                    clock++;
                    printClock();
                    addScheduledPrograms();
                }

                // Getting the process again because it might have been removed from memory
                process = processManager.getProcess(processId);
                if (process.getPcb().getProcessState() == ProcessState.RUNNING) {
                    if (process.hasInstructions()) {
                        addProcessToReadyQueue(processId);
                    } else {
                        process.getPcb().setProcessState(ProcessState.TERMINATED);
                        System.out.println("Process " + processId + " terminated.");
                        processManager.removeProcess(processId);
                        printQueues();
                    }
                    printMemory("At start of clock cycle " + clock);
                }
            } else {
                printMemory("At end of clock cycle " + clock);
                clock++;
                printClock();
                addScheduledPrograms();
            }
            System.out.println();
        }
    }

    private void printClock() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("############### Clock (" + clock + ") #############");
    }

    private void printMemory(String messageSuffix) {
        System.out.println();
        System.out.println();
        System.out.println("Memory Contents " + messageSuffix);
        System.out.println("-----------------------------");
        Memory memory = kernel.getMemory();
        HashMap<Integer, Process> processesInMemory =
            kernel.getProcessManager().getProcessesInMemory();
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < memory.getSize(); i++) {
            Object value = memory.read(i);

            String line = "#" + i + "# ";
            if (value != null) {
                for (Process process : processesInMemory.values()) {
                    if (process.inProcessAddressSpace(i)) {
                        line += "Process " + process.getPcb().getProcessId() + " ";
                        if (process.getPcb().inRange(i)) {
                            line +=
                                "PCB " + process.getPcb().getKey(process.getPcb().getOffset(i)) +
                                ": " +
                                value;
                        } else if (process.getInstructionsMemory().inRange(i)) {
                            line += "Instruction #" + process.getInstructionsMemory().getOffset(i) +
                                    ": " + value;
                        } else if (process.getVariablesMemory().inRange(i)) {
                            line += "Variable " + value;
                        } else {
                            line += value;
                        }
                    }
                }
            } else {
                line += "null";
            }

            lines.add(line);
        }

        System.out.println(String.join("\n", lines));
        System.out.println("------------------------------------");
        System.out.println();
        System.out.println();
    }

    private Process chooseProcessToRun() {
        int processId = readyQueue.remove();

        Process process = kernel.getProcessManager()
                                .getProcess(processId);

        process.getPcb().setProcessState(ProcessState.RUNNING);

        printQueues();

        return process;
    }

    public void addProcessToReadyQueue(int processId) {
        readyQueue.add(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.READY);

        System.out.println("Added process " + processId + " to ready queue.");
        printQueues();
    }

    public void blockProcess(int processId) {
        blockedProcesses.add(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.BLOCKED);

        System.out.println("Blocked process " + processId);
        printQueues();
    }

    public void unblockProcess(int processId) {
        blockedProcesses.remove(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.READY);

        System.out.println("Unblocked process " + processId);
        printQueues();

        addProcessToReadyQueue(processId);
    }

    public void printQueues() {
        System.out.println("Ready queue: " + readyQueue);
        System.out.println("Blocked queue: " + blockedProcesses);
    }

    public List<Integer> getBlockedProcesses() {
        return new ArrayList<>(blockedProcesses);
    }
}
