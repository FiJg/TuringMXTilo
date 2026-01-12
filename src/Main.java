import java.util.List;

public class Main {
    private static final int MAX_STEPS = 5000000;
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(" input empty");
            return;
        }
        MultiTapeTM tm = makeMultiplicationMachine();
        runSim(tm, args[0]);
    }

    private static void runSim(MultiTapeTM tm, String input) {
        tm.setTape(0, input);
        System.out.println("--- Simulation Start --- \nInput: " + input);
        tm.printConfig(); // init state s0

        while (!tm.isHalting() && tm.getSteps() < MAX_STEPS) {
            tm.step();
            tm.printConfig();
        }

        if (tm.getSteps() >= MAX_STEPS) {
            System.out.println("\n[ERROR] step limit reached, numbers are too large.");
        } else {
            System.out.println("\n--- Simulation ended (State: " + tm.getCurrentState() + ") ---");
        }

        System.out.println("\n--- Encoded tmachine ---\n" + tm.encode());
    }

    public static MultiTapeTM makeMultiplicationMachine() {
        TMBuilder b = new TMBuilder(4, List.of("IN", "ACC", "FAC", "OUT"));

        b.add("q_INIT", "#,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "R,S,_,_");
        b.add("q_INIT", "0,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "S,S,_,_");
        b.add("q_INIT", "1,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "S,S,_,_");
        return b.build();
    }
}