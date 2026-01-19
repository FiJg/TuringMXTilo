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

    /**
     * Machine def
     * tapes: 0-IN, 1-ACC, 2-FAC, 3-OUT
     * syntax: add(state, read, nextstate, write, move)
     * symbols: "_" - stay, "*" any/wildcard
     */
    public static MultiTapeTM makeMultiplicationMachine() {
        TMBuilder b = new TMBuilder(4, List.of("IN", "ACC", "FAC", "OUT"));

        // -- init
        b.add("q_INIT", "#,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "R,S,_,_");
        b.add("q_INIT", "0,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "S,S,_,_");
        b.add("q_INIT", "1,#,*,*", "q_CHECK_INPUT", "_,x,_,_", "S,S,_,_");

        // -- input check and prep
        b.add("q_CHECK_INPUT", "0,*,#,*", "q_BIN2UN_START",     "_,_,_,_", "S,S,S,S");
        b.add("q_CHECK_INPUT", "1,*,#,*", "q_BIN2UN_START",     "_,_,_,_", "S,S,S,S");
        b.add("q_CHECK_INPUT", "#,*,*,*", "q_FINISH_TO_BINARY", "_,_,_,_", "S,S,S,S");

        // -- binary to unary
        b.add("q_BIN2UN_START",    "*,*,*,*", "q_BIN2UN_READ_BIT", "_,_,_,_", "_,_,_,_");

        // read bit logic
        b.add("q_BIN2UN_READ_BIT", "0,*,*,*", "q_BIN2UN_DOUBLE",     "_,_,_,_", "R,S,S,S");
        b.add("q_BIN2UN_READ_BIT", "1,*,*,*", "q_BIN2UN_DOUBLE_ADD", "_,_,_,_", "R,S,S,S");
        b.add("q_BIN2UN_READ_BIT", "#,*,*,*", "q_MULTIPLY_START",    "_,_,_,_", "S,S,S,S");

        // double logic (FAC * 2 using OUT)
        b.add("q_BIN2UN_DOUBLE", "*,*,x,#", "q_BIN2UN_DOUBLE",   "_,_,#,x", "S,S,R,R");
        b.add("q_BIN2UN_DOUBLE", "*,*,#,#", "q_BIN2UN_RET_FAC",  "_,_,_,_", "S,S,L,L");

        // double & add Logic (FAC * 2 + 1 using OUT)
        b.add("q_BIN2UN_DOUBLE_ADD", "*,*,x,#", "q_BIN2UN_DOUBLE_ADD",   "_,_,#,x", "S,S,R,R");
        b.add("q_BIN2UN_DOUBLE_ADD", "*,*,#,#", "q_BIN2UN_RET_FAC_ADD",  "_,_,_,_", "S,S,L,L");

        // helpers for double
        b.add("q_BIN2UN_RET_FAC",     "*,*,#,x", "q_BIN2UN_RET_FAC",        "_,_,_,_", "S,S,L,L");
        b.add("q_BIN2UN_RET_FAC",     "*,*,#,#", "q_BIN2UN_WRITE_DOUBLE",   "_,_,_,_", "S,S,S,R");
        b.add("q_BIN2UN_RET_FAC_ADD", "*,*,#,x", "q_BIN2UN_RET_FAC_ADD",    "_,_,_,_", "S,S,L,L");
        b.add("q_BIN2UN_RET_FAC_ADD", "*,*,#,#", "q_BIN2UN_WRITE_DOUBLE_ADD","_,_,_,_", "S,S,S,R");

        // write back, OUT to FAC
        b.add("q_BIN2UN_WRITE_DOUBLE", "*,*,#,x", "q_BIN2UN_W2",      "_,_,x,#", "S,S,R,S");
        b.add("q_BIN2UN_WRITE_DOUBLE", "*,*,#,#", "q_BIN2UN_CLEANUP", "_,_,_,_", "S,S,S,S");
        b.add("q_BIN2UN_W2",     "*,*,#,#", "q_BIN2UN_W_NEXT",  "_,_,x,_", "S,S,R,R");
        b.add("q_BIN2UN_W_NEXT", "*,*,*,x", "q_BIN2UN_W2",      "_,_,x,#", "S,S,R,S");
        b.add("q_BIN2UN_W_NEXT", "*,*,*,#", "q_BIN2UN_CLEANUP", "_,_,_,_", "S,S,L,L");

        // write back for double+add
        b.add("q_BIN2UN_WRITE_DOUBLE_ADD", "*,*,#,x", "q_BIN2UN_W2A",     "_,_,x,#", "S,S,R,S");
        b.add("q_BIN2UN_WRITE_DOUBLE_ADD", "*,*,#,#", "q_BIN2UN_ADD_ONE", "_,_,_,_", "S,S,S,S");

        b.add("q_BIN2UN_W2A",      "*,*,#,#", "q_BIN2UN_W_NEXT_A", "_,_,x,_", "S,S,R,R");
        b.add("q_BIN2UN_W_NEXT_A", "*,*,*,x", "q_BIN2UN_W2A",      "_,_,x,#", "S,S,R,S");
        b.add("q_BIN2UN_W_NEXT_A", "*,*,*,#", "q_BIN2UN_ADD_ONE",  "_,_,_,_", "S,S,S,S");
        b.add("q_BIN2UN_ADD_ONE",  "*,*,#,#", "q_BIN2UN_CLEANUP",  "_,_,x,_", "S,S,S,S");

        // cleaning stuff , rewind FAC
        b.add("q_BIN2UN_CLEANUP", "*,*,x,*", "q_BIN2UN_CLEANUP",  "_,_,_,_", "S,S,L,S");
        b.add("q_BIN2UN_CLEANUP", "*,*,#,*", "q_BIN2UN_READ_BIT", "_,_,_,_", "S,S,R,S");

        // -- multipilcation ACC * FAC to OUT
        b.add("q_MULTIPLY_START", "*,x,*,*", "q_MULTIPLY_START", "_,_,_,_", "S,L,S,S");
        b.add("q_MULTIPLY_START", "*,*,x,*", "q_MULTIPLY_START", "_,_,_,_", "S,S,L,S");
        b.add("q_MULTIPLY_START", "*,#,#,#", "q_MUL_LOOP_FAC",   "_,_,_,_", "S,R,R,S");

        // loop FAC
        b.add("q_MUL_LOOP_FAC", "*,*,x,#", "q_MUL_COPY_ACC_TO_OUT", "_,_,_,_", "S,S,S,S");
        b.add("q_MUL_LOOP_FAC", "*,*,#,#", "q_MUL_REWIND_OUT",      "_,_,_,_", "S,S,S,L");

        // copy ACC to OUT ( for current FAC x
        b.add("q_MUL_COPY_ACC_TO_OUT", "*,x,x,#", "q_MUL_COPY_ACC_TO_OUT", "_,_,_,x", "S,R,S,R");
        b.add("q_MUL_COPY_ACC_TO_OUT", "*,#,x,#", "q_MUL_RET_ACC",         "_,_,_,_", "S,L,S,S");

        // rewind ACC
        b.add("q_MUL_RET_ACC", "*,x,x,*", "q_MUL_RET_ACC", "_,_,_,_", "S,L,S,S");
        b.add("q_MUL_RET_ACC", "*,#,x,*", "q_MUL_NEXT_FAC","_,_,x,_", "S,R,R,S"); // Mark FAC used (move R)
        b.add("q_MUL_NEXT_FAC","*,*,*,*", "q_MUL_LOOP_FAC","_,_,_,_", "S,S,S,S");

        // overwrite ACC with OUT - new total
        b.add("q_MUL_REWIND_OUT", "*,*,#,x", "q_MUL_REWIND_OUT",     "_,_,_,_", "S,S,S,L");
        b.add("q_MUL_REWIND_OUT", "*,*,#,#", "q_MUL_COPY_OVERWRITE", "_,_,_,_", "S,S,S,R");

        b.add("q_MUL_COPY_OVERWRITE", "*,*,#,x", "q_MUL_COPY_OVERWRITE", "_,x,_,#", "S,R,S,R");
        b.add("q_MUL_COPY_OVERWRITE", "*,*,#,#", "q_MUL_ERASE_OLD_TAIL", "_,_,_,_", "S,S,S,S");

        // erase any leftover x in ACC
        b.add("q_MUL_ERASE_OLD_TAIL", "*,x,*,*", "q_MUL_ERASE_OLD_TAIL", "_,#,_,_", "S,R,S,S");
        b.add("q_MUL_ERASE_OLD_TAIL", "*,#,*,*", "q_MUL_CLEANUP",        "_,_,_,_", "S,L,L,S");

        // fin clea after multiply
        b.add("q_MUL_CLEANUP", "*,x,x,*", "q_MUL_CLEANUP",  "_,_,#,_", "S,L,L,S");
        b.add("q_MUL_CLEANUP", "*,x,#,*", "q_MUL_CLEANUP",  "_,_,_,_", "S,L,S,S");
        b.add("q_MUL_CLEANUP", "*,#,x,*", "q_MUL_CLEANUP",  "_,_,#,_", "S,S,L,S");
        b.add("q_MUL_CLEANUP", "*,#,#,#", "q_TRY_NEXT_NUM", "_,_,_,_", "S,R,R,S");

        // -- next number check
        b.add("q_TRY_NEXT_NUM", "#,*,*,*", "q_CHECK_INPUT", "_,_,_,_", "R,S,S,S");
        b.add("q_TRY_NEXT_NUM", "0,*,*,*", "q_CHECK_INPUT", "_,_,_,_", "S,S,S,S");
        b.add("q_TRY_NEXT_NUM", "1,*,*,*", "q_CHECK_INPUT", "_,_,_,_", "S,S,S,S");

        // -- unary to binary convert
        b.add("q_FINISH_TO_BINARY", "*,*,*,*", "q_FIN_PARITY_CHECK", "_,_,_,_", "S,S,S,S");

        b.add("q_FIN_PARITY_CHECK", "*,#,#,*", "q_FIN_REVERSE_BITS", "_,_,_,_", "S,S,S,S"); // ACC empty? Done.
        b.add("q_FIN_PARITY_CHECK", "*,*,*,*", "q_DIV_SCAN",         "_,_,_,_", "S,S,S,S"); // Else divide.

        // divide by 2 (modulo 2, odd/even check)
        b.add("q_DIV_SCAN", "*,x,*,*", "q_DIV_HAS_ONE",  "_,#,_,_", "S,R,S,S");
        b.add("q_DIV_SCAN", "*,#,*,*", "q_WRITE_BIT_0",  "_,_,_,_", "S,S,S,S"); // Even

        b.add("q_DIV_HAS_ONE", "*,x,*,*", "q_DIV_HAS_PAIR", "_,#,x,_", "S,R,R,S");
        b.add("q_DIV_HAS_ONE", "*,#,*,*", "q_WRITE_BIT_1",  "_,_,_,_", "S,S,S,S"); // Odd

        b.add("q_DIV_HAS_PAIR", "*,*,*,*", "q_DIV_SCAN", "_,_,_,_", "S,S,S,S");

        // erite resulting bit to tape 0 - IN
        b.add("q_WRITE_BIT_0", "*,*,*,*", "q_RESTORE_ACC_START", "0,_,_,_", "R,S,S,S");
        b.add("q_WRITE_BIT_1", "*,*,*,*", "q_RESTORE_ACC_START", "1,_,_,_", "R,S,S,S");

        // restore ACC from FAC -- halving
        b.add("q_RESTORE_ACC_START", "*,*,*,*", "q_RESTORE_REWIND", "_,_,_,_", "S,L,L,S");
        b.add("q_RESTORE_REWIND",    "*,#,x,*", "q_RESTORE_REWIND", "_,_,_,_", "S,L,L,S");
        b.add("q_RESTORE_REWIND",    "*,#,#,*", "q_RESTORE_COPY",   "_,_,_,_", "S,R,R,S");

        b.add("q_RESTORE_COPY", "*,#,x,*", "q_RESTORE_COPY",      "_,x,#,_", "S,R,R,S");
        b.add("q_RESTORE_COPY", "*,#,#,*", "q_RESTORE_CHECK_END", "_,_,_,_", "S,L,L,S");

        b.add("q_RESTORE_CHECK_END", "*,x,*,*", "q_RESTORE_REWIND_ACC_ONLY", "_,_,_,_", "S,L,S,S");
        b.add("q_RESTORE_CHECK_END", "*,#,*,*", "q_FIN_REVERSE_BITS",        "_,_,_,_", "S,S,S,S");

        b.add("q_RESTORE_REWIND_ACC_ONLY", "*,x,*,*", "q_RESTORE_REWIND_ACC_ONLY", "_,_,_,_", "S,L,S,S");
        b.add("q_RESTORE_REWIND_ACC_ONLY", "*,#,*,*", "q_DIV_SCAN",                "_,_,_,_", "S,R,S,S");

        // reverse bits (t0 -> t3)
        b.add("q_FIN_REVERSE_BITS", "*,*,*,*", "q_REV_MOVE_LEFT", "_,_,_,_", "L,S,S,S");
        b.add("q_REV_MOVE_LEFT",    "0,*,*,*", "q_REV_MOVE_LEFT", "_,_,_,0", "L,S,S,R");
        b.add("q_REV_MOVE_LEFT",    "1,*,*,*", "q_REV_MOVE_LEFT", "_,_,_,1", "L,S,S,R");
        b.add("q_REV_MOVE_LEFT",    "#,*,*,*", "HALT",            "_,_,_,_", "S,S,S,S");

        return b.build();
    }
}