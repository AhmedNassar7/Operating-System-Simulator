package os;

import os.processes.Process;
import os.processes.ProcessData;

public class InstructionExecutor {
    private final Kernel kernel;

    public InstructionExecutor(Kernel kernel) {
        this.kernel = kernel;
    }

    public void execute(Process process, String instruction) throws OSException {
        String[] line = instruction.split(" ");
        int pid = process.getPcb().getProcessId();

        if (line[0].equals("semWait") && line.length == 2) {
            SystemCalls.semWait(kernel, process, line[1]);
        } else if (line[0].equals("semSignal") && line.length == 2) {
            SystemCalls.semSignal(kernel, process, line[1]);
        } else if (line[0].equals("assign") && line.length == 3 && line[2].matches("input")) {
            if (process.getTemp() == null) {
                SystemCalls.input(process);
                return;
            } else {
                SystemCalls.assign(process, line[1], process.getTemp());
                process.setTemp(null);
            }
        } else if (line[0].equals("assign") && line.length == 4 && line[2].equals("readFile")) {
            if (process.getTemp() == null) {
                SystemCalls.readFile(process, line[3]);
                return;
            } else {
                SystemCalls.assign(process, line[1], process.getTemp());
                process.setTemp(null);
            }
        } else if (line[0].equals("assign") && line.length == 3) {
            SystemCalls.assignVariable(process, line[1], line[2]);
        } else if (line[0].equals("print") && line.length == 2) {
            SystemCalls.print(process, line[1]);
        } else if (line[0].equals("writeFile") && line.length == 3) {
            SystemCalls.writeFile(process, line[1], line[2]);
        } else if (line[0].equals("printFromTo") && line.length == 3) {
            SystemCalls.printFromTo(process, line[1], line[2]);
        } else {
            throw new OSException("Invalid instruction " + instruction);
        }

        // Process might be unloaded from memory after executing the instruction,
        // for example after semSignal another process might be needed to be loaded to change
        // its state to ready instead of blocked, and maybe there is no enough memory, so this
        // running process should be unloaded to make space for the other process.
        Process currentProcess = kernel.getProcessManager().getProcess(pid);

        currentProcess.incrementProgramCounter();
    }

}
