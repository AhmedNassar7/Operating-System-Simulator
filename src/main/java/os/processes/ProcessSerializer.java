package os.processes;

public interface ProcessSerializer {
    String serialize(ProcessData process);
    ProcessData deserialize(String process);
}
