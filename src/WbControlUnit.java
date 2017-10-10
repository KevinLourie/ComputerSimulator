/**
 * Created by kzlou on 5/7/2017.
 */
public class WbControlUnit {

    /** Latch for WB Selector */
    private ShiftRegister<Value8> wbSelectorLatch;

    /** For turning off wbEnable and memoryWriteEnable if go is false */
    private Gate wbEnableGate;

    /** Latch for WB enable */
    private ShiftRegister<Value8> wbEnableLatch;

    /** Latch for WB mux index */
    private ShiftRegister<Value8> wbMuxIndexLatch;

    private ShiftRegister<Value8> haltEnableLatch;

    /** Selector of the S register */
    private Output<Value8> sSelectorInput;

    /** Selector of the T register */
    private Output<Value8> tSelectorInput;

    /** S mux index. 0 uses the register bank, 1 uses the result from the ALU, and 2 for the write back */
    private Output<Value8> sMuxIndexOutput;

    /** T mux index */
    private Output<Value8> tMuxIndexOutput;

    private Output<Value8> goOutput;

    public Output<Value8> getSMuxIndexOutput() {
        return sMuxIndexOutput;
    }

    public Output<Value8> getTMuxIndexOutput() {
        return tMuxIndexOutput;
    }

    WbControlUnit(Cycler cycler) {
        wbEnableGate = new Gate("WbEnableGate", Gate.and2);
        wbSelectorLatch = new ShiftRegister<>("WbControlUnit.wbSelector", 2, Value8.zero, cycler);
        wbEnableLatch = new ShiftRegister<>("WbControlUnit.wbEnable", 2, Value8.zero, cycler);
        wbMuxIndexLatch = new ShiftRegister<>("WbControlUnit.wbMuxIndex", 2, Value8.zero, cycler);
        haltEnableLatch = new ShiftRegister<>("WbControlUnit.haltEnable", 2, Value8.zero, cycler);
        sMuxIndexOutput = () -> computeMuxIndex(sSelectorInput.read());
        tMuxIndexOutput = () -> computeMuxIndex(tSelectorInput.read());
        goOutput = () -> {
            final int sSelectorInputValue = sSelectorInput.read().intValue();
            int sStall = determineGo(sSelectorInputValue);
            final int tSelectorInputValue = tSelectorInput.read().intValue();
            int tStall = determineGo(tSelectorInputValue);
            int go = sStall == 0 && tStall == 0 ? 1 : 0;
            return new Value8(go);
        };
    }

    /**
     * Choose the correct value for S or T in case of data hazards
     *
     * @param selector either S or T selector
     * @return mux index for S or T
     */
    private Value8 computeMuxIndex(Value8 selector) {
        Value8 wbEnable0 = wbEnableLatch.getOutput(0).read();
        Value8 wbSelector0 = wbSelectorLatch.getOutput(0).read();
        Value8 wbEnable1 = wbEnableLatch.getOutput(1).read();
        Value8 wbSelector1 = wbSelectorLatch.getOutput(1).read();
        if(wbEnable0.intValue() == 1 && selector == wbSelector0) {
            return new Value8(1);
        }
        else if(wbEnable1.intValue() == 1 && selector == wbSelector1) {
            return new Value8(2);
        }
        return new Value8(0);
    }

    /**
     * Determine if a stall needs to be done for load
     * @param selector data being used in instruction
     * @return stall or not stall
     */
    private int determineGo(int selector) {
        // This means that the data has not yet been fetched from memory. If the
        // next instruction uses the data loaded from memory, it will not have the correct value.
        int wbMuxIndex = wbMuxIndexLatch.getOutput(0).read().intValue();
        boolean notYetFetched = wbMuxIndex == 0;

        // Check if the data being used in the load is the same as the data being used in the next instruction.
        int wbSelector = wbSelectorLatch.getOutput(0).read().intValue();
        boolean dataIsSame = wbSelector == selector;

        // Check if a write back will occur, but not yet.
        // TODO: It seems like this should be wbEnable0, not wbEnable. However, then R5 is never written.
        int wbEnable = wbEnableLatch.getOutput(0).read().intValue();
        boolean willWriteBack = wbEnable == 1;

        if(notYetFetched && dataIsSame && willWriteBack) {
            return 1;
        }
        return 0;
    }

    public WbControlUnit setSSelectorInput(Output<Value8> sSelectorInput) {
        this.sSelectorInput = sSelectorInput;
        return this;
    }

    public String toStringDelta() {
        Joiner j = new Joiner(" ", "WbControlUnit(", ")");
        j.add(haltEnableLatch.toStringDelta());
        j.add(wbEnableLatch.toStringDelta());
        j.add(wbMuxIndexLatch.toStringDelta());
        j.add(wbSelectorLatch.toStringDelta());
        return j.toString();
    }

    public WbControlUnit setTSelectorInput(Output<Value8> tSelectorInput) {
        this.tSelectorInput = tSelectorInput;
        return this;
    }

    public Output<Value8> getHaltEnableLatch() {
        return haltEnableLatch.getOutput(1);
    }

    public void setHaltEnableLatch(Output<Value8> haltEnableInput) {
        haltEnableLatch.setInput(haltEnableInput);
    }

    /**
     * Setter for WB enable latch input
     * @param wbEnableInput wb enable latch input
     * @return WbLatches
     */
    public WbControlUnit setWbEnableInput(Output<Value8> wbEnableInput) {
        wbEnableLatch.setInput(wbEnableInput);
        return this;
    }

    /**
     * Setter for WB selector latch input
     * @param wbSelectorInput wb selector latch input
     * @return WbLatches
     */
    public WbControlUnit setWbSelectorInput(Output<Value8> wbSelectorInput) {
        wbSelectorLatch.setInput(wbSelectorInput);
        return this;
    }

    /**
     * Setter for WB mux index latch input
     * @param wbMuxIndexInput wb mux index latch input
     * @return WbLatches
     */
    public WbControlUnit setWbMuxIndexInput(Output<Value8> wbMuxIndexInput) {
        wbMuxIndexLatch.setInput(wbMuxIndexInput);
        return this;
    }

    /**
     * Getter for wb mux index output
     * @return wb mux index output
     */
    public Output<Value8> getWbMuxIndexOutput() {
        return wbMuxIndexLatch.getOutput(1);
    }

    /**
     * Getter for wb selector output
     * @return wb selector output
     */
    public Output<Value8> getWbSelectorOutput() {
        return wbSelectorLatch.getOutput(1);
    }

    /**
     * Getter for wb enable output
     * @return wb enable output
     */
    public Output<Value8> getWbEnableOutput() {
        return wbEnableLatch.getOutput(1);
    }

    public Output<Value8> getGoOutput() {
        return goOutput;
    }
}
