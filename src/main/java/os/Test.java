package os;

public class Test {
    public static void main(String[] args) {
        Kernel kernel = new Kernel(40, 2, "process");
        Scheduler scheduler = kernel.getScheduler();
        scheduler.scheduleProgramAt("src/test/resources/Program_1.txt", 0);
        scheduler.scheduleProgramAt("src/test/resources/Program_2.txt", 1);
        scheduler.scheduleProgramAt("src/test/resources/Program_3.txt", 4);
        scheduler.run();
    }
}
