/**
 * Created by kzlou on 4/1/2017.
 */
public class Multiplexer<T> implements Input<T>, Output<Integer> {

    Input<T>[] inputArray;

    int index;

    Multiplexer(Input<T>... muxOp) {
        this.inputArray = muxOp;
    }

    @Override
    public T read() {
        return inputArray[index].read();
    }

    @Override
    public void write(Integer data) {
        index = data;
    }
}
