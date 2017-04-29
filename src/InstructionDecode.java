/**
 * Created by kzlou on 4/22/2017.
 */
public class InstructionDecode {

    /**
     * Instruction is located here
     */
    private RegisterBank registerBank;

    /**
     * Decodes instruction
     */
    private Decoder decoder;

    private Bus<Integer, Byte> opcodeBus;

    private Bus<Integer, Byte> addressSBus;

    private Bus<Integer, Byte> addressTBus;

    private Bus<Integer, Byte> addressDBus;

    private Bus<Integer, Integer> immediateBus;

    /**
     * Instruction
     */
    private Register<Integer> instructionRegister;

    /**
     * Location of instruction
     */
    private Register<Integer> nextPC;

    /**
     * Constructor
     * @param registerBank register bank
     */
    InstructionDecode(RegisterBank registerBank, Cycler cycler) {
        opcodeBus = new Bus<Integer, Byte>() {
            @Override
            public Byte read() {
                return (byte)(input.read() >> 26);
            }
        };
        addressSBus = new Bus<Integer, Byte>() {
            @Override
            public Byte read() {
                return (byte)((input.read() >> 21) & 0x1F);
            }
        };
        addressTBus = new Bus<Integer, Byte>() {
            @Override
            public Byte read() {
                return (byte)((input.read() >> 16) & 0x1F);
            }
        };
        addressDBus = new Bus<Integer, Byte>() {
            @Override
            public Byte read() {
                return (byte)((input.read() >> 11) & 0x1F);
            }
        };
        immediateBus = new Bus<Integer, Integer>() {
            @Override
            public Integer read() {
                // Extract the bottom 16 bits and sign extend it to a 32-bits
                return (int)(short)(input.read() & 0xFFFF);
            }
        };
        this.registerBank = registerBank;
        decoder = new Decoder();
        instructionRegister = new Register<>("IR", 0, cycler);
        nextPC = new Register<>("Next PC", 0, cycler);
    }

    /**
     * Initilize data register and address register
     * @param dataInput data register input
     * @param nextPCInput address register input
     */
    public void init(Output<Integer> dataInput, Output<Integer> nextPCInput) {
        // TODO: add correct enable inputs
        addressSBus.init(instructionRegister.getOutput());
        addressTBus.init(instructionRegister.getOutput());
        addressDBus.init(instructionRegister.getOutput());
        opcodeBus.init(instructionRegister.getOutput());
        immediateBus.init(instructionRegister.getOutput());
        instructionRegister.init(dataInput, null);
        nextPC.init(nextPCInput, null);
        registerBank.initRead(addressSBus, addressTBus);
        decoder.init(null);
    }

    public Output<Integer> getAddressRegisterOutput() {
        return nextPC.getOutput();
    }
}
