/**
 * Created by kzlou on 4/29/2017.
 */
public class Adder {
    /**
     * Current number
     */
    private Output<Integer> input1;

    private Output<Integer> input2;

    /**
     * Next number
     */
    private Output<Integer> output;

    /**
     * Constructor
     */
    Adder() {
        output = new Output<Integer>() {
            @Override
            public Integer read() {
                return input1.read() + input2.read();
            }
        };
    }

    /**
     * Initialize input
     *
     */
    public void init(Output<Integer> input1, Output<Integer> input2) {
        this.input1 = input1;
        this.input2 = input2;
    }

    /**
     * Getter for output
     * @return output
     */
    public Output<Integer> getOutput() {
        return output;
    }
}
