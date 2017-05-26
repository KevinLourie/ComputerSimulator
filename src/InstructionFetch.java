/**
 * Created by kzlou on 4/22/2017.
 */
public class InstructionFetch {

    /** Fetch instruction from here */
    private InstructionMemory instructionMemory;

    /** Adds to address */
    private Adder adder;

    /** Program counter */
    private Register<Integer> pc;

    /** Where to get next program from */
    private Multiplexer<Integer> pcMux;

    /**
     * Constructor
     * @param arr integer array
     */
    InstructionFetch(int[] arr, Cycler cycler) {
        instructionMemory = new InstructionMemory(arr);
        adder = new Adder();
        pc = new Register<>("InstructionFetch.pc", 0, cycler);
        pcMux = new Multiplexer<>();

        // Internal wiring
        adder.init(pc.getOutput(), new ConstantOutput<Integer>(4));
        pc.setInput(pcMux.getOutput());
        instructionMemory.setAddressInput(pc.getOutput());
    }

    /**
     * Initialize adder, PC, and pcMux
     * @param jumpAddressInput input to pcMux
     * @return Instruction Fetch
     */
    public InstructionFetch setJumpAddressInput(Output<Integer> jumpAddressInput) {
        pcMux.setInputs(adder.getOutput(), jumpAddressInput);
        return this;
    }

    /**
     * Setter for pcMux index
     * @param jumpEnableInput 1 to enable jump
     * @return Instruction Fetch
     */
    public InstructionFetch setJumpEnableInput(Output<Integer> jumpEnableInput) {
        pcMux.setIndexInput(jumpEnableInput);
        return this;
    }

    public Output<Integer> getInstructionOutput() {
        return instructionMemory.getInstructionOutput();
    }

    /**
     * Getter for the next PC. It is an input to the Instruction Decode for computing the branch target
     * @return next PC
     */
    public Output<Integer> getNextPcOutput() {
        return pcMux.getOutput();
    }
}
