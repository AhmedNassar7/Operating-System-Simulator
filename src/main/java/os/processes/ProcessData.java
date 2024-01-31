package os.processes;

import java.util.HashMap;
import java.util.List;

public class ProcessData {
    private final int processId;
    private final int programCounter;
    private final int processSize;
    private final int memoryStart;
    private final int end;
    private final ProcessState processState;
    private final List<String> instructions;
    private final HashMap<String, Object> variables;
    private final String tempVariable;


    public ProcessData(
        int processId,
        int programCounter,
        int processSize,
        int memoryStart,
        int end,
        ProcessState processState,
        List<String> instructions,
        HashMap<String, Object> variables,
        String tempVariable
    ) {
        this.processId = processId;
        this.programCounter = programCounter;
        this.processSize = processSize;
        this.memoryStart = memoryStart;
        this.end = end;
        this.processState = processState;
        this.instructions = instructions;
        this.variables = variables;
        this.tempVariable = tempVariable;
    }

    public int getProcessId() {
        return processId;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getProcessSize() {
        return processSize;
    }

    public int getMemoryStart() {
        return memoryStart;
    }

    public int getMemoryEnd() {
        return end;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public HashMap<String, Object> getVariables() {
        return variables;
    }

    public String getTempVariable() {
        return tempVariable;
    }
}
